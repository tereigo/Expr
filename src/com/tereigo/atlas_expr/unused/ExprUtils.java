package com.tereigo.atlas_expr.unused;

final class ExprUtils {

    private ExprUtils() {}

    public static String stringify(Object object) {
        if (object instanceof String) {
            return "\"" + object + "\"";
        }
        return object.toString();
    }

/*
    public static String stringify(Object object) {
        if (object == null) {
            return "nil";
        }
        // convert rounded double to integer representation
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
*/
}
