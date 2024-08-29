package com.tereigo.atlas_expr;

public class ExceptionUtils {

    private ExceptionUtils() { }

    public static String getExceptionMsg(RuntimeException ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.toString();
    }
}

