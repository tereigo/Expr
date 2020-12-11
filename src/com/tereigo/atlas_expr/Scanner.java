package com.tereigo.atlas_expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tereigo.atlas_expr.TokenType.AND;
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
import static com.tereigo.atlas_expr.TokenType.LEFT_PAREN;
import static com.tereigo.atlas_expr.TokenType.LEFT_BRACKET;
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
import static com.tereigo.atlas_expr.TokenType.RIGHT_PAREN;
import static com.tereigo.atlas_expr.TokenType.RIGHT_BRACKET;
import static com.tereigo.atlas_expr.TokenType.STRING;
import static com.tereigo.atlas_expr.TokenType.TRUE;

/*
  Converts a given String into a list of Tokens
 */
class Scanner {
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("not",    NOT);
    keywords.put("and",    AND);
    keywords.put("or",     OR);
    keywords.put("in",     IN);
    keywords.put("false",  FALSE);
    keywords.put("true",   TRUE);
  }
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> tokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }
    tokens.add(new Token(EOF, "", null, line, start));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '[': addToken(LEFT_BRACKET); break;
      case ']': addToken(RIGHT_BRACKET); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case '*': addToken(MUL); break;
      case '%': addToken(MODULUS); break;
      case '!':
        addToken(match('=') ? NOT_EQUAL : NOT);
        break;
      case '=':
        if (match('=')) {
          addToken(EQUAL_EQUAL);
        } else {
          error(line, start,"Expected '==' comparison not found");
        }
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd()) {
            advance();
          }
        } else {
          addToken(DIV);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n': // tereni: TODO: do we need EOL?
        line++;
        break;

      case '"':
        string();
        break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          error(line, start, "Unexpected character");
        }
        break;
    }
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) {
      advance();
    }
    String text = source.substring(start, current).toLowerCase();
    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);
  }

  private void number() {
    while (isDigit(peek())) {
      advance();
    }

    boolean isDouble = false;

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      isDouble = true;
      // Consume the "."
      advance();
      while (isDigit(peek())) {
        advance();
      }
    }
    String literal = source.substring(start, current);
    double asDouble = Double.parseDouble(literal);
    if (isDouble || asDouble > Long.MAX_VALUE || asDouble < Long.MIN_VALUE) {
      addToken(DOUBLE_NUMBER, asDouble);
    } else {
      long asLong = Long.parseLong(literal);
      addToken(LONG_NUMBER, asLong);
    }
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }
      advance();
    }

    if (isAtEnd()) {
      error(line, start, "Unterminated string");
      return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private boolean match(char expected) {
    if (isAtEnd()) {
      return false;
    }
    if (source.charAt(current) != expected) {
      return false;
    }
    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) {
      return '\0';
    }
    return source.charAt(current + 1);
  }

  // tereni: TODO: may be switch to Character.
  private boolean isAlpha(char c) {
    //Character.isLetter()
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
            c == '_' || c == '$';           // tereni: TODO: do we need '_'?
  }

  // tereni: TODO: Character.isLetterOrDigit()
  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  // tereni: TODO: Character.isDigit()
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    current++;
    return source.charAt(current - 1);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line, start));
  }

  static void error(int line, int start, String message) {
    throw new ParseError(errorMsg(line, "at pos " + (start + 1), message));
  }

  private static String errorMsg(int line, String where, String message) {
    return "[line " + line + "] Error " + where + ": " + message;
  }

}
