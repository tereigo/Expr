package com.tereigo.atlas_expr;

final class ParseError extends RuntimeException {

    ParseError(int line, int pos, String message) {
        super("[line " + line + ", pos " + pos + "]: " + message);
    }

    ParseError(String message) {
        super(message);
    }
}
