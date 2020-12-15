package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

public class ExprContextCombined implements ExprContext {
    final ExprContext ctx1;
    final ExprContext ctx2;

    public ExprContextCombined(ExprContext ctx1, ExprContext ctx2) {
        this.ctx1 = ctx1;
        this.ctx2 = ctx2;
    }

    @Override
    public MutableVariant get(Token token, MutableVariant result) {
        MutableVariant res = ctx1.get(token, result);
        return res != null ? res : ctx2.get(token, result);
    }

    @Override
    public Object getFunction(Token token) {
        Object res = ctx1.getFunction(token);
        return res != null ? res : ctx2.getFunction(token);
    }

    @Override
    public String toString() {
        return ctx1.toString() + "," + ctx2.toString();
    }
}
