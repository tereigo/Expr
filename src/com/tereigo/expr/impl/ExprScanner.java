package com.tereigo.expr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tereigo.expr.impl.TokenType.AND;
import static com.tereigo.expr.impl.TokenType.BETWEEN;
import static com.tereigo.expr.impl.TokenType.COMMA;
import static com.tereigo.expr.impl.TokenType.DIV;
import static com.tereigo.expr.impl.TokenType.DOT;
import static com.tereigo.expr.impl.TokenType.DOUBLE_NUMBER;
import static com.tereigo.expr.impl.TokenType.EOF;
import static com.tereigo.expr.impl.TokenType.EQUAL_EQUAL;
import static com.tereigo.expr.impl.TokenType.FALSE;
import static com.tereigo.expr.impl.TokenType.GREATER;
import static com.tereigo.expr.impl.TokenType.GREATER_EQUAL;
import static com.tereigo.expr.impl.TokenType.IDENTIFIER;
import static com.tereigo.expr.impl.TokenType.IN;
import static com.tereigo.expr.impl.TokenType.LEFT_BRACKET;
import static com.tereigo.expr.impl.TokenType.LEFT_PAREN;
import static com.tereigo.expr.impl.TokenType.LESS;
import static com.tereigo.expr.impl.TokenType.LESS_EQUAL;
import static com.tereigo.expr.impl.TokenType.LONG_NUMBER;
import static com.tereigo.expr.impl.TokenType.MATH_E;
import static com.tereigo.expr.impl.TokenType.MATH_PI;
import static com.tereigo.expr.impl.TokenType.MINUS;
import static com.tereigo.expr.impl.TokenType.MODULUS;
import static com.tereigo.expr.impl.TokenType.MUL;
import static com.tereigo.expr.impl.TokenType.NOT;
import static com.tereigo.expr.impl.TokenType.NOT_EQUAL;
import static com.tereigo.expr.impl.TokenType.OR;
import static com.tereigo.expr.impl.TokenType.PLUS;
import static com.tereigo.expr.impl.TokenType.RIGHT_BRACKET;
import static com.tereigo.expr.impl.TokenType.RIGHT_PAREN;
import static com.tereigo.expr.impl.TokenType.STRING;
import static com.tereigo.expr.impl.TokenType.TERNARY_ELSE;
import static com.tereigo.expr.impl.TokenType.TERNARY_IF;
import static com.tereigo.expr.impl.TokenType.TRUE;
import static com.tereigo.expr.impl.TokenType.WITHIN;

/*
  Converts a given String into a list of Tokens
 */
final class ExprScanner {
  private static final Map<String, TokenType> KEYWORDS;

  static {
    KEYWORDS = new HashMap<>();
    KEYWORDS.put("not",    NOT);
    KEYWORDS.put("and",    AND);
    KEYWORDS.put("or",     OR);
    KEYWORDS.put("in",     IN);
    KEYWORDS.put("within", WITHIN);
    KEYWORDS.put("between", BETWEEN);
    KEYWORDS.put("false",  FALSE);
    KEYWORDS.put("true",   TRUE);
    KEYWORDS.put("pi",     MATH_PI);
    KEYWORDS.put("e",      MATH_E);
  }
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  // TODO: switch to ByteBuffer
  ExprScanner(final String source) {
    this.source = source;
    tokenize();
  }

  List<Token> tokens() {
    return tokens;
  }

  private void tokenize() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }
    tokens.add(new Token(EOF, "", null, line, start));
  }

  private void scanToken() {
    final char c = advance();
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
      case '?': addToken(TERNARY_IF); break;
      case ':': addToken(TERNARY_ELSE); break;
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

      case '\n':
        line++;
        break;

      case '"':
        string('"');
        break;

      case '\'':
        string('\'');
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
    final String text = source.substring(start, current).toLowerCase();
    TokenType type = KEYWORDS.get(text);
    if (type == null) {
      type = IDENTIFIER;
    }
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
    final String literal = source.substring(start, current);
    final double asDouble = Double.parseDouble(literal);
    if (isDouble || asDouble > Long.MAX_VALUE || asDouble < Long.MIN_VALUE) {
      addToken(DOUBLE_NUMBER, asDouble, literal);
    } else {
      // this is a special case to be able to parse Long.MIN_VALUE ("-9223372036854775808")
      // this is because Long.parseLong("9223372036854775808") fails because 9223372036854775808 > Long.MAX_VALUE (9223372036854775807)
      if (literal.equals("9223372036854775808") && tokens.size() > 0 && tokens.get(tokens.size() - 1).type == MINUS) {
        // remove previous "minus"
        tokens.remove(tokens.size() - 1);
        addToken(LONG_NUMBER, Long.MIN_VALUE, "-9223372036854775808");
      } else {
        final long asLong = Long.parseLong(literal);
        addToken(LONG_NUMBER, asLong, literal);
      }
    }
  }

  private void string(final char expectedStringEnd) {
    final int stringStartPos = current - 1;
    while (peek() != expectedStringEnd && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }
      advance();
    }

    if (isAtEnd()) {
      error(line, stringStartPos, "Unterminated string");
      return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes.
    final String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private boolean match(final char expected) {
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

  private static boolean isAlpha(final char c) {
    return Character.isLetter(c) || c == '_' || c == '$';
  }

  private static boolean isAlphaNumeric(final char c) {
    return Character.isLetterOrDigit(c);
  }

  private static boolean isDigit(final char c) {
    return Character.isDigit(c);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    current++;
    return source.charAt(current - 1);
  }

  private void addToken(final TokenType type) {
    addToken(type, null);
  }

  private void addToken(final TokenType type, final Object literal) {
    final String text = source.substring(start, current);
    addToken(type, literal, text);
  }

  private void addToken(final TokenType type, final Object literal, final String text) {
    tokens.add(new Token(type, text, literal, line, start));
  }

  static void error(final int line, final int start, final String message) {
    throw new ParseError(line, start + 1, message);
  }

}
