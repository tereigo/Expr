package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

public class ExprContextChained implements ExprContext {
    ExprContext ctx;

    public ExprContextChained(ExprContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public MutableVariant get(Token token, MutableVariant result) {
        return ctx.get(token, result);
    }

    // Adding another context
    public ExprContext add(ExprContext ctx) {
        // New context has higher priority so it is "ctx1" in ExprContextCombined
        this.ctx = new ExprContextCombined(ctx, this.ctx);
        return this;
    }
}
