package com.tereigo.expr.impl;

public class RuntimeError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public final transient Token token;

    public RuntimeError(final Token token, final String message) {
        super(message);
        this.token = token;
    }

    public RuntimeError(final Token token, final String message, final Throwable cause) {
        super(message, cause);
        this.token = token;
    }
}
