package com.tereigo.expr.impl;

final class ParseError extends RuntimeException {

    ParseError(final int line, final int pos, final String message) {
        super("[line " + line + ", pos " + pos + "]: " + message);
    }

    ParseError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
