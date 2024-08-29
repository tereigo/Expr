package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.Variant;
import com.tereigo.atlas_expr.variant.VariantFactory;
import com.tereigo.atlas_expr.variant.VariantUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tereigo.atlas_expr.TokenType.AND;
import static com.tereigo.atlas_expr.TokenType.BETWEEN;
import static com.tereigo.atlas_expr.TokenType.COMMA;
import static com.tereigo.atlas_expr.TokenType.DIV;
import static com.tereigo.atlas_expr.TokenType.DOT;
import static com.tereigo.atlas_expr.TokenType.DOUBLE_NUMBER;
import static com.tereigo.atlas_expr.TokenType.EOF;
import static com.tereigo.atlas_expr.TokenType.EQUAL_EQUAL;
import static com.tereigo.atlas_expr.TokenType.FALSE;
import static com.tereigo.atlas_expr.TokenType.GREATER;
import static com.tereigo.atlas_expr.TokenType.GREATER_EQUAL;
import static com.tereigo.atlas_expr.TokenType.IDENTIFIER;
import static com.tereigo.atlas_expr.TokenType.IN;
import static com.tereigo.atlas_expr.TokenType.LEFT_BRACKET;
import static com.tereigo.atlas_expr.TokenType.LEFT_PAREN;
import static com.tereigo.atlas_expr.TokenType.LESS;
import static com.tereigo.atlas_expr.TokenType.LESS_EQUAL;
import static com.tereigo.atlas_expr.TokenType.LONG_NUMBER;
import static com.tereigo.atlas_expr.TokenType.MINUS;
import static com.tereigo.atlas_expr.TokenType.MODULUS;
import static com.tereigo.atlas_expr.TokenType.MUL;
import static com.tereigo.atlas_expr.TokenType.NOT;
import static com.tereigo.atlas_expr.TokenType.NOT_EQUAL;
import static com.tereigo.atlas_expr.TokenType.OR;
import static com.tereigo.atlas_expr.TokenType.PLUS;
import static com.tereigo.atlas_expr.TokenType.RIGHT_BRACKET;
import static com.tereigo.atlas_expr.TokenType.RIGHT_PAREN;
import static com.tereigo.atlas_expr.TokenType.STRING;
import static com.tereigo.atlas_expr.TokenType.TERNARY_ELSE;
import static com.tereigo.atlas_expr.TokenType.TERNARY_IF;
import static com.tereigo.atlas_expr.TokenType.TRUE;
import static com.tereigo.atlas_expr.TokenType.WITHIN;

/*
    This Expr module is largely based on the brilliant book "Crafting interpreters" by Bob Nystrom
    http://craftinginterpreters.com/contents.html

    It's been refactored significantly to introduce the following main features:
    1. Support Long values
    2. Support ByteBuffer values
    3. Support "IN" operator
    4. Make expression evaluation garbage-free
    5. Hierarchical AST graph output

    But conceptually and architecturally it's an exact replica of what's presented in the book

    Another good step-by-step guide of building a parser: https://ruslanspivak.com/lsbasi-part1/


    Expression language grammar:

    expression : logic_or
    logic_or   : logic_and ( "or" logic_and )* ;
    logic_and  : ternary_if ( "and" in_operator )* ;
    ternary_if  : in_operator ( "?" expression ":" expression ) ;
    in_operator: range_operator ( "in" "[" LIST_ENTRY ( "," LIST_ENTRY )* "]" ) ;
    range_operator: equality ( ("within" | "between") "[" expression "," expression ) ;
    equality   : comparison ( ( "!=" | "==" ) comparison )* ;
    comparison : term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term       : factor ( ( "-" | "+" ) factor )* ;
    factor     : unary ( ( "/" | "*" | "%" ) unary )* ;
    unary      : ( "!" | "-" ) unary | call ;
    call       : primary ( "(" arguments? ")" | "." IDENTIFIER "(" arguments? ")" )* ;
    arguments  : expression ( "," expression )* ;
    primary    : BOOLEAN | DOUBLE_NUMBER | LONG_NUMBER | STRING | IDENTIFIER | "(" expression ")" ;

    Lexems:
    BOOLEAN: true|false|True|False|TRUE|FALSE
    LIST_ENTRY: (DOUBLE_NUMBER | LONG_NUMBER | STRING)

    You can find plenty of the expression examples in the tests
*/

final class ExprParser {

  private final List<Token> tokens;
  private int current = 0;

  ExprParser(List<Token> tokens) {
    this.tokens = tokens;
  }

  Expr parse() {
    Expr result = expression();
    if (current < tokens.size() - 1) {
      throw error(peek(), "Malformed expression: parsing ended prematurely");
    }
    return result;
  }

  // expression : logic_or
  private Expr expression() {
    return logic_or();
  }

  // logic_or   : logic_and ( "or" logic_and )* ;
  private Expr logic_or() {
    Expr expr = logic_and();

    while (match(OR)) {
      Token operator = previous();
      Expr right = logic_and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  // logic_and  : ternary_if ( "and" in_operator )* ;
  private Expr logic_and() {
    Expr expr = ternary_if_operator();

    while (match(AND)) {
      Token operator = previous();
      Expr right = in_operator();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;

//    Expr expr = in_operator();
//
//    while (match(AND)) {
//      Token operator = previous();
//      Expr right = in_operator();
//      expr = new Expr.Logical(expr, operator, right);
//    }
//
//    return expr;
  }

  // ternary_if : in_operator ( "?" expression ":" expression ) ;
  private Expr ternary_if_operator() {
    Expr expr = in_operator();

    if (match(TERNARY_IF)) {
      Token operator = previous();
      Expr trueExpr = expression();
      if (match(TERNARY_ELSE)) {
        Expr falseExpr = expression();
        expr = new Expr.Ternary(operator, expr, trueExpr, falseExpr);
      } else {
        throw error(peek(), "Expect ':' after ternary ('?') operator");
      }
    }

    return expr;
  }

  // in_operator: range_operator ( "in" "[" LIST_ENTRY ( "," LIST_ENTRY )* "]" ) ;
  private Expr in_operator() {
    Expr expr = range_operator();
    if (match(IN)) {
      Token operator = previous();
      if (match(LEFT_BRACKET)) {
        List<Variant> values = list();
        consume(RIGHT_BRACKET, "Expect ']' after '['");
        return new Expr.InOperator(expr, operator, values);
      } else {
        throw error(peek(), "Expect '[' after IN operator");
      }
    }
    return expr;
  }

  private List<Variant> list() {
    List<Variant> values = new ArrayList<>();
    ExprType type = null;
    do {
      Variant entry = list_entry();
      if (type == null) {
        type = entry.exprType();
      } else if (type != entry.exprType()) {
        throw error(peek(), "Different value types in IN operator list: " + type + " and " + entry.exprType());
      }
      values.add(entry);
    } while (match(COMMA));
    return values;
  }

  private Variant list_entry() {
    if (match(DOUBLE_NUMBER)) {
      return VariantFactory.createImmutableDouble((double)previous().literal);
    } else if (match(LONG_NUMBER)) {
      return VariantFactory.createImmutableLong((long)previous().literal);
    } else if (match(STRING)) {
      return VariantFactory.createImmutableString((String)previous().literal);
    }
    throw error(peek(), "Expect number/string list entry inside '[]'");
  }

  // range_operator: equality ( ("within" | "between") "[" expression "," expression ) ;
  private Expr range_operator() {
    Expr expr = equality();

    if (match(WITHIN) || match(BETWEEN)) {
      Token operator = previous();
      if (match(LEFT_BRACKET)) {
        final Expr val1 = range_entry();
        consume(COMMA, "Expect 2 values separated by ',' in range operator");
        final Expr val2 = range_entry();
        consume(RIGHT_BRACKET, "Expect ']' after '[' and 2 numbers");
        if (operator.type == WITHIN) {
          return new Expr.WithinOperator(expr, operator, val1, val2);
        } else {
          return new Expr.BetweenOperator(expr, operator, val1, val2);
        }
      } else {
        throw error(peek(), "Expect '[' after WITHIN/BETWEEN operator");
      }
    }
    return expr;
  }

  private Expr range_entry() {
    final Expr expr = expression();
    if (expr instanceof Expr.Literal) {
      final Variant val = ((Expr.Literal)expr).result;
      if (!VariantUtils.isNumber(val)) {
        throw error(peek(), "Expect a number inside '[]' for range WITHIN/BETWEEN operator");
      }
    }
    return expr;
  }


  // equality   : comparison ( ( "!=" | "==" ) comparison )* ;
  private Expr equality() {
    Expr expr = comparison();

    while (match(NOT_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // comparison : term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
  private Expr comparison() {
    Expr expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // term       : factor ( ( "-" | "+" ) factor )* ;
  private Expr term() {
    Expr expr = factor();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // factor     : unary ( ( "/" | "*" | "%" ) unary )* ;
  private Expr factor() {
    Expr expr = unary();

    while (match(DIV, MUL, MODULUS)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // unary      : ( "!" | "-" ) unary | call ;
  private Expr unary() {
    if (match(NOT, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      // safety check that "NOT" always followed by "()"
      // This is to avoid runtime errors for syntax: not $ric == "VOD.L" which compiles into AST: not($ric) == "VOD.L"
      if (operator.type == NOT && !(right instanceof Expr.Grouping)) {
        throw error(previous(), "Operator NOT should be applied to the expression in parens '()'");
      }
      return new Expr.Unary(operator, right);
    }

    return call();
  }

  // call       : primary ( "(" arguments? ")" | "." IDENTIFIER "(" arguments? ")" )* ;
  private Expr call() {
    Expr expr = primary();
    while (true) {
      if (match(LEFT_PAREN)) {
        if (!(expr instanceof Expr.Identifier)) {
          throw error(previous(2), "Function name should be an identifier");
        }
        List<Expr> args = arguments(5);
        expr = new Expr.Call(((Expr.Identifier) expr).operator, args);
      } else if (match(DOT)) {
        Token name = consume(IDENTIFIER, "Expect function name after '.'");
        if (peek().type == LEFT_PAREN) {
          consume(LEFT_PAREN, "Expect '(' after '.'function_name");
          List<Expr> args = arguments(4);
          expr = new Expr.ObjectCall(expr, name, args);
        } else {
          expr = new Expr.ObjectCall(expr, name, Collections.emptyList());
        }
      } else {
        break;
      }
    }
    return expr;
  }

  // arguments  : expression ( "," expression )* ;
  private List<Expr> arguments(int maxArgs) {
    List<Expr> args = new ArrayList<>(maxArgs);
    if (!check(RIGHT_PAREN)) {
      do {
        if (args.size() >= maxArgs) {
          throw error(peek(), "Can't have more than " + maxArgs + " arguments");
        }
        args.add(expression());
      } while (match(COMMA));
    }

    consume(RIGHT_PAREN, "Expect ')' after arguments");

    return args;
  }

  // primary    : BOOLEAN | DOUBLE_NUMBER | LONG_NUMBER | STRING | IDENTIFIER | "(" expression ")" ;
  private Expr primary() {
    if (match(FALSE)) {
      return Expr.Literal.BOOL_FALSE;
    }
    if (match(TRUE)) {
      return Expr.Literal.BOOL_TRUE;
    }

    if (match(DOUBLE_NUMBER)) {
      return new Expr.Literal((double)previous().literal);
    } else if (match(LONG_NUMBER)) {
      return new Expr.Literal((long)previous().literal);
    } else if (match(STRING)) {
      return new Expr.Literal((String)previous().literal);
    }

    if (match(IDENTIFIER)) {
      return new Expr.Identifier(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression");
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) {
      return advance();
    }
    throw error(peek(), message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) {
      return false;
    }
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) {
      current++;
    }
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return previous(1);
  }

  private Token previous(int back) {
    return tokens.get(current - back);
  }

  private ParseError error(Token token, String message) {
    return new ParseError(token.line, token.pos + 1, message);
  }

}
