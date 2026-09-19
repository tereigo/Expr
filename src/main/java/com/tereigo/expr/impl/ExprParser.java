package com.tereigo.expr.impl;

import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;
import com.tereigo.expr.variant.VariantFactory;
import com.tereigo.expr.variant.VariantUtils;

import java.util.*;

import static com.tereigo.expr.impl.TokenType.*;

/*
    This Expr module is largely based on the brilliant book "Crafting interpreters" by Bob Nystrom
    http://craftinginterpreters.com/contents.html

    It's been refactored significantly to introduce the following main features:
    1. Garbage-free expression evaluation!
    2. Supported types: long, double, boolean, String, ByteBuffer, Enums (as Strings), user-defined objects:
       "1 != 2 and PI > 3.0 and 'ABC'.contains('BC') and not(true != false) or deployment.type == 'PROD' and today.date > parseDate('30.01.2024')"
    3. Support all arithmetic / boolean operators: "(5 * 3 == 15) and not (true != false)"
    4. Support all Java Math and String functions (except those that produce garbage)
    5. Ternary operator: "5 * (true ? 3 : 1) == 15"
    6. Support "IN / WITHIN / BETWEEN" operators: "(2 in [1, 2, 3]) or ('Wen' in ['Sat', 'Sun']) and (3 between [2, 5])"
    7. Support user-defined functions with up to 5 arguments: "isEven(4) or calcSquareArea(w, h) < 100"
    8. Support adding custom user defined objects/domains: "env.country == 'Russia' and env.type == 'PROD' and today.hour > 12 and algo.activeOrder.qty > 1000"
    9. Support both traditional and functional syntax: "pctOf(5, 100) == 5.pctOf(100) and round(PI) == 3 and PI.round == 3"
    10. AST graph optimization by resolving all identifiers during expression parsing
    11. Multi-line expressions
    12. Hierarchical AST graph output

    Another good step-by-step guide of building a parser: https://ruslanspivak.com/lsbasi-part1/

    Expression language grammar:

    expression : logic_or
    logic_or   : logic_and ( "or" logic_and )* ;
    logic_and  : ternary_if ( "and" in_operator )* ;
    ternary_if  : in_operator ( "?" expression ":" expression ) ;
    in_operator: range_operator ( ( "in" | "not in" ) ) "[" expression ( "," expression )* "]" ) ;
    range_operator: equality ( ("within" | "between" | "not within" | "not between") "[" expression "," expression "]" ) ;
    equality   : comparison ( ( "!=" | "==" ) comparison )* ;
    comparison : term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term       : factor ( ( "-" | "+" ) factor )* ;
    factor     : unary_minus ( ( "/" | "*" | "%" ) unary_minus )* ;
    unary_minus: ( "-" call ) | unary_not ;
    unary_not  : ( "!" "(" expression ")" ) | call;
    call       : primary ( "(" arguments? ")" | "." IDENTIFIER "(" arguments? ")" )* ;
    arguments  : expression ( "," expression )* ;
    primary    : BOOLEAN | DOUBLE_NUMBER | LONG_NUMBER | STRING | IDENTIFIER | "(" expression ")" ;

    Lexems:
    BOOLEAN: true|false|True|False|TRUE|FALSE
    STRING: any characters within double (") or single (') quotes

    You can find plenty of the expression examples in the tests
*/

final class ExprParser {

    private final List<Token> tokens;
    private final HashMap<String, Expr.Literal> constants = new HashMap<>();
    private int current = 0;

    ExprParser(final List<Token> tokens, final Map<String, Expr.Literal> constants) {
        this.tokens = tokens;
        this.constants.putAll(constants);
    }

    private static boolean areTypesCompatible(final ExprType type, final Variant val) {
        if ((type == ExprType.DOUBLE || type == ExprType.LONG) && VariantUtils.isNumber(val)) {
            return true;
        }
        if ((type == ExprType.STRING || type == ExprType.BYTE_BUFFER) && VariantUtils.isStringOrByteBuffer(val)) {
            return true;
        }
        return false;
    }

    Expr parse() {
        final Expr result = expression();
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
            final Token operator = previous();
            final Expr right = logic_and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    // logic_and  : ternary_if ( "and" in_operator )* ;
    private Expr logic_and() {
        Expr expr = ternary_if_operator();

        while (match(AND)) {
            final Token operator = previous();
            final Expr right = in_operator();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    // ternary_if : in_operator ( "?" expression ":" expression ) ;
    private Expr ternary_if_operator() {
        Expr expr = in_operator();

        if (match(TERNARY_IF)) {
            final Token operator = previous();
            final Expr trueExpr = expression();
            if (match(TERNARY_ELSE)) {
                final Expr falseExpr = expression();
                expr = new Expr.Ternary(operator, expr, trueExpr, falseExpr);
            } else {
                throw error(peek(), "Expect ':' after ternary ('?') operator");
            }
        }

        return expr;
    }

    // in_operator: range_operator ( ( "in" | "not in" ) ) "[" expression ( "," expression )* "]" ) ;
    private Expr in_operator() {
        final Expr expr = range_operator();
        // "not in"
        if (peek().type == NOT && next(1) != null && next(1).type == IN) {
            final Token not_operator = consume(NOT, "Expected NOT operator");
            consume(IN, "Expected IN operator");
            return new Expr.Unary(not_operator, parseInOperands(expr));
        }
        if (match(IN)) {
            return parseInOperands(expr);
        }
        return expr;
    }

    private Expr.InOperator parseInOperands(final Expr expr) {
        final Token operator = previous();
        if (match(LEFT_BRACKET)) {
            final List<Expr> values = list();
            consume(RIGHT_BRACKET, "Expect ']' after '['");
            validateInValues(values);
            return new Expr.InOperator(expr, operator, values);
        } else {
            throw error(peek(), "Expect '[' after IN operator");
        }
    }

    private void validateInValues(final List<Expr> values) {
        ExprType type = null;
        for (int i = 0; i < values.size(); i++) {
            final Expr expr = values.get(i);
            if (expr instanceof Expr.Literal) {
                final Variant val = ((Expr.Literal) expr).result;
                if (type == null) {
                    type = val.exprType();
                } else {
                    if (!areTypesCompatible(type, val)) {
                        throw error(peek(), "Different value types in IN operator list: " + type + " and " + val.exprType());
                    }
                }
            }
        }
    }

    private List<Expr> list() {
        final List<Expr> values = new ArrayList<>();
        do {
            final Expr entry = expression();
            values.add(entry);
        } while (match(COMMA));
        return values;
    }

    // range_operator: equality ( ("within" | "between" | "not within" | "not between") "[" expression "," expression "]" ) ;
    private Expr range_operator() {
        final Expr expr = equality();

        // "not within/between"
        if (peek().type == NOT && next(1) != null && (next(1).type == WITHIN || next(1).type == BETWEEN)) {
            final Token not_operator = consume(NOT, "Expected NOT operator");
            match(WITHIN, BETWEEN);
            return new Expr.Unary(not_operator, parseRangeOperands(expr));
        }
        if (match(WITHIN, BETWEEN)) {
            return parseRangeOperands(expr);
        }
        return expr;
    }

    private Expr.BaseExpr parseRangeOperands(final Expr expr) {
        final Token operator = previous();
        if (match(LEFT_BRACKET)) {
            final Expr entry1 = range_entry();
            consume(COMMA, "Expect 2 values separated by ',' in range operator");
            final Expr entry2 = range_entry();
            consume(RIGHT_BRACKET, "Expect ']' after '[' and 2 numbers");
            if (operator.type == WITHIN) {
                if (entry1 instanceof Expr.Literal && entry2 instanceof Expr.Literal) {
                    final Variant min = VariantUtils.min(((Expr.Literal) entry1).result, ((Expr.Literal) entry2).result);
                    final Variant max = VariantUtils.max(((Expr.Literal) entry1).result, ((Expr.Literal) entry2).result);
                    return new Expr.StaticWithinOperator(expr, operator, min, max);
                }
                return new Expr.WithinOperator(expr, operator, entry1, entry2);
            } else {
                if (entry1 instanceof Expr.Literal && entry2 instanceof Expr.Literal) {
                    final Variant min = VariantUtils.min(((Expr.Literal) entry1).result, ((Expr.Literal) entry2).result);
                    final Variant max = VariantUtils.max(((Expr.Literal) entry1).result, ((Expr.Literal) entry2).result);
                    return new Expr.StaticBetweenOperator(expr, operator, min, max);
                }
                return new Expr.BetweenOperator(expr, operator, entry1, entry2);
            }
        } else {
            throw error(peek(), "Expect '[' after WITHIN/BETWEEN operator");
        }
    }

    private Expr range_entry() {
        final Expr expr = expression();
        if (expr instanceof Expr.Literal) {
            final Variant val = ((Expr.Literal) expr).result;
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
            final Token operator = previous();
            final Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // comparison : term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            final Token operator = previous();
            final Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // term       : factor ( ( "-" | "+" ) factor )* ;
    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            final Token operator = previous();
            final Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // factor     : unary_minus ( ( "/" | "*" | "%" ) unary_minus )* ;
    private Expr factor() {
        Expr expr = unary_minus();

        while (match(DIV, MUL, MODULUS)) {
            final Token operator = previous();
            final Expr right = unary_minus();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // unary_minus: ( "-" call ) | unary_not ;
    private Expr unary_minus() {
        if (match(MINUS)) {
            final Token operator = previous();
            final Expr right = call();
            if (operator.type == MINUS && right instanceof Expr.Literal) {
                final Expr.Literal literal = (Expr.Literal) right;
                final Variant val = literal.result;
                if (!VariantUtils.isNumber(val)) {
                    throw error(peek(), "Unary minus is applicable to numbers only");
                }
                // if parsing "-1" then we combine from Unary(-) + Literal(1) into Literal (-1)
                final MutableVariant result = VariantFactory.createEmpty();
                VariantUtils.negateNumber(result, literal.result);
                return VariantUtils.isDouble(result) ? new Expr.Literal(result.getAsDouble()) : new Expr.Literal(result.getAsLong());
            }
            return new Expr.Unary(operator, right);
        }
        return unary_not();
    }

    // unary_not  : ( "!" "(" expression ")" ) | call;
    private Expr unary_not() {
        if (match(NOT)) {
            final Token operator = previous();
            if (match(LEFT_PAREN)) {
                final Expr right = expression();
                consume(RIGHT_PAREN, "Expect ')' after '('");
                return new Expr.Unary(operator, right);
            } else {
                throw error(peek(), "Operator NOT should be applied to the expression in parens '()'");
            }
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
                final List<Expr> args = arguments(5);
                expr = new Expr.Call(((Expr.Identifier) expr).operator, args);
            } else if (match(DOT)) {
                final Token name = consume(IDENTIFIER, "Expect function name after '.'");
                if (peek().type == LEFT_PAREN) {
                    consume(LEFT_PAREN, "Expect '(' after '.'function_name");
                    final List<Expr> args = arguments(4);
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
    private List<Expr> arguments(final int maxArgs) {
        final List<Expr> args = new ArrayList<>(maxArgs);
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
        if (match(MATH_PI)) {
            return Expr.Literal.PI;
        }
        if (match(MATH_E)) {
            return Expr.Literal.E;
        }

        if (match(DOUBLE_NUMBER)) {
            return new Expr.Literal((double) previous().literal);
        } else if (match(LONG_NUMBER)) {
            return new Expr.Literal((long) previous().literal);
        } else if (match(STRING)) {
            return new Expr.Literal((String) previous().literal);
        }

        if (match(IDENTIFIER)) {
            final Expr.Literal constant = constants.get(previous().lexeme);
            return constant != null ? constant : new Expr.Identifier(previous());
        }

        if (match(LEFT_PAREN)) {
            final Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression");
            return expr;
        }

        throw error(peek(), "Expect expression");
    }

    private boolean match(final TokenType... types) {
        for (final TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(final TokenType type, final String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    private boolean check(final TokenType type) {
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

    private Token next(final int forward) {
        if (current + forward >= tokens.size()) {
            return null;
        }
        return tokens.get(current + forward);
    }

    private Token previous() {
        return previous(1);
    }

    private Token previous(final int back) {
        return tokens.get(current - back);
    }

    private ParseError error(final Token token, final String message) {
        return new ParseError(token.line, token.pos + 1, message);
    }

}
