package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

public class ExprContextCombined implements ExprContext {
    ExprContext ctx1;
    ExprContext ctx2; // can be null

    public ExprContextCombined init(ExprContext ctx1) {
        this.ctx1 = ctx1;
        this.ctx2 = null;
        return this;
    }

    public ExprContextCombined init(ExprContext ctx1, ExprContext ctx2) {
        this.ctx1 = ctx1;
        this.ctx2 = ctx2;
        return this;
    }

    public static ExprContextCombined create(ExprContext ctx1, ExprContext ctx2) {
        final ExprContextCombined result = new ExprContextCombined();
        return result.init(ctx1, ctx2);
    }

    @Override
    public MutableVariant get(Token token, MutableVariant result) {
        MutableVariant res = ctx1.get(token, result);
        return res != null ? res : ctx2 != null ? ctx2.get(token, result) : null;
    }

    @Override
    public Object getFunction(Token token) {
        Object res = ctx1.getFunction(token);
        return res != null ? res : ctx2 != null ? ctx2.getFunction(token) : null;
    }

    @Override
    public String toString() {
        return ctx1.toString() + "," + ctx2.toString();
    }
}
