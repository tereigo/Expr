package com.tereigo.expr.impl;

final class ParseError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ParseError(final int line, final int pos, final String message) {
        super("[line " + line + ", pos " + pos + "]: " + message);
    }

    ParseError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
