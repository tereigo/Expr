package com.tereigo.expr;

public class ExceptionUtils {

    private ExceptionUtils() { }

    public static String getExceptionMsg(RuntimeException ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.toString();
    }
}

