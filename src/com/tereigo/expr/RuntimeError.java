package com.tereigo.expr;

class RuntimeError extends RuntimeException {
  final Token token;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }

  RuntimeError(Token token, String message, Throwable cause) {
    super(message, cause);
    this.token = token;
  }
}
