package com.tereigo.atlas_expr.variant;

import com.tereigo.atlas_expr.ExprType;

public final class VariantUtils {

    private VariantUtils() {}

    public static boolean isNumber(Variant operand) {
        return isDouble(operand) || isLong(operand);
    }

    public static boolean isLong(Variant operand) {
        return operand.exprType() == ExprType.LONG;
    }

    public static boolean isDouble(Variant operand) {
        return operand.exprType() == ExprType.DOUBLE;
    }

    public static boolean isString(Variant operand) {
        return operand.exprType() == ExprType.STRING;
    }

    public static boolean isByteBuffer(Variant operand) {
        return operand.exprType() == ExprType.BYTE_BUFFER;
    }

    public static boolean isBoolean(Variant operand) {
        return operand.exprType() == ExprType.BOOL;
    }

    public static boolean isExprContext(Variant operand) {
        return operand.exprType() == ExprType.EXPR_CONTEXT;
    }

    public static boolean isObject(Variant operand) {
        return operand.exprType() == ExprType.OBJECT;
    }
}
