package com.tereigo.atlas_expr.variant;

import com.tereigo.atlas_expr.ExprType;

public final class VariantUtils {

    private VariantUtils() {}

    // see "IMPORTANT NOTE" in VariantImpl
    public static boolean canCompare(Variant v1, Variant v2) {
        return v1.exprType() == v2.exprType() ||
                (v1.exprType() == ExprType.STRING && v2.exprType() == ExprType.BYTE_BUFFER) ||
                (v1.exprType() == ExprType.BYTE_BUFFER && v2.exprType() == ExprType.STRING);
    }

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
}
