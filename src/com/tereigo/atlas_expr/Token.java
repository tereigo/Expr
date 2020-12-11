package com.tereigo.atlas_expr;

class Token {
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int line;
  final int pos; // start position of this token in line

  Token(TokenType type, String lexeme, Object literal) {
    this(type, lexeme, literal, 0, 0);
  }

  Token(TokenType type, String lexeme, Object literal, int line, int pos) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
    this.pos = pos;
  }

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}
