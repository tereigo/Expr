package com.tereigo.atlas_expr;

class ParseError extends RuntimeException {
    public ParseError(String message) {
        super(message);
    }
}
