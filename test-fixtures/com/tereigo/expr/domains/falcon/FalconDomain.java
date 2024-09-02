package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.falcon.utils.OrderPrice;
import com.tereigo.expr.falcon.utils.PriceUtils;
import com.tereigo.expr.variant.VariantUtils;

public final class FalconDomain {

    public static void defineShortcuts(final MutableExprContext ctx) {
        ctx.defineFunction("ltod", (result, arg1) -> {
            if (VariantUtils.isLong(arg1)) {
                result.accept(PriceUtils.ltod(arg1.getAsLong()));
            } else {
                throw new RuntimeException("Operand must be a LONG number");
            }
        });

        ctx.defineFunction("dtol", (result, arg1) -> {
            if (VariantUtils.isDouble(arg1)) {
                result.accept((long)PriceUtils.dtol(arg1.getAsDouble()));
            } else {
                throw new RuntimeException("Operand must be a DOUBLE number");
            }
        });

        ctx.defineFunction("isMarketPrice", (result, arg1) -> {
            if (VariantUtils.isLong(arg1)) {
                result.accept(OrderPrice.isMarket(arg1.getAsLong()));
            } else {
                throw new RuntimeException("Operand must be a LONG number");
            }
        });

        ctx.defineFunction("isLimitPrice", (result, arg1) -> {
            if (VariantUtils.isLong(arg1)) {
                result.accept(OrderPrice.isLimit(arg1.getAsLong()));
            } else {
                throw new RuntimeException("Operand must be a LONG number");
            }
        });

        ctx.defineFunction("isValidPrice", (result, arg1) -> {
            if (VariantUtils.isLong(arg1)) {
                result.accept(OrderPrice.isValid(arg1.getAsLong()));
            } else {
                throw new RuntimeException("Operand must be a LONG number");
            }
        });
    }
}
