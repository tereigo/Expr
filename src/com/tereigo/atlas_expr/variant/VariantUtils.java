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
}
