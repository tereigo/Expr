package com.tereigo.expr.impl;

class RuntimeError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    final transient Token token;

    RuntimeError(final Token token, final String message) {
        super(message);
        this.token = token;
    }

    RuntimeError(final Token token, final String message, final Throwable cause) {
        super(message, cause);
        this.token = token;
    }
}
