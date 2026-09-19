package com.tereigo.expr.utils;

public class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static String getExceptionMsg(final Throwable ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.toString();
    }
}

