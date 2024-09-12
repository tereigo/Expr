package com.tereigo.expr.impl;

class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static String getExceptionMsg(final RuntimeException ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.toString();
    }
}

