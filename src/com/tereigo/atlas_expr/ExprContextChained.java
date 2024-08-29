package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

final class ExprContextChained implements ExprContext {
    private ExprContext ctx;

    public ExprContextChained(ExprContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public MutableVariant get(String name, MutableVariant result) {
        return ctx.get(name, result);
    }

    @Override
    public Object getFunction(String name) {
        return ctx.getFunction(name);
    }

    // Adding another context
    public ExprContext add(ExprContext ctx) {
        // New context has higher priority so it is "ctx1" in ExprContextCombined
        this.ctx = ExprContextCombined.create(ctx, this.ctx);
        return this;
    }
}
