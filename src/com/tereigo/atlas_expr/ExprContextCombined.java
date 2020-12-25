package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

final class ExprContextCombined implements ExprContext {
    ExprContext ctx1;
    ExprContext ctx2; // can be null

    public void init(ExprContext ctx1) {
        this.ctx1 = ctx1;
        this.ctx2 = null;
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
    public MutableVariant get(String name, MutableVariant result) {
        MutableVariant res = ctx1.get(name, result);
        return res != null ? res : ctx2 != null ? ctx2.get(name, result) : null;
    }

    @Override
    public Object getFunction(String name) {
        Object res = ctx1.getFunction(name);
        return res != null ? res : ctx2 != null ? ctx2.getFunction(name) : null;
    }

    @Override
    public String toString() {
        return (ctx1 != null ? "ctx1: " + ctx1.toString() : "") + (ctx2 != null ? ", ctx2: " + ctx2.toString() : "");
    }
}
