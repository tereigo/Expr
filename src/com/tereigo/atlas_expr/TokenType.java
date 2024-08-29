package com.tereigo.atlas_expr;

enum TokenType {
  // Single-character tokens.
  LEFT_PAREN,        // '('
  RIGHT_PAREN,       // ')'
  LEFT_BRACKET,      // '['
  RIGHT_BRACKET,     // ']'
  COMMA,             // ','
  DOT,               // '.'
  MINUS,             // '-'
  PLUS,              // '+'
  DIV,               // '/'
  MUL,               // '*'
  MODULUS,           // '%'
  TERNARY_IF,        // '?'
  TERNARY_ELSE,      // ':'

  // One or two character tokens.
  NOT,              // '!' or 'not'
  NOT_EQUAL,        // '!='
  EQUAL_EQUAL,      // '=='
  GREATER,          // '>'
  GREATER_EQUAL,    // '>='
  LESS,             // '<'
  LESS_EQUAL,       // '<='

  // Literals.
  IDENTIFIER,
  STRING,
  DOUBLE_NUMBER,
  LONG_NUMBER,

  // Keywords.
  AND,              // 'and'
  OR,               // 'or'
  IN,               // 'in'
  WITHIN,           // 'within'
  BETWEEN,          // 'between'
  FALSE,
  TRUE,

  EOF
}
