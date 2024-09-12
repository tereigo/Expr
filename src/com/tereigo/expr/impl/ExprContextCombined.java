package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;

final class ExprContextCombined implements ExprContext {
    private ExprContext ctx1;
    private ExprContext ctx2; // can be null

    public static ExprContextCombined create(final ExprContext ctx1, final ExprContext ctx2) {
        final ExprContextCombined result = new ExprContextCombined();
        return result.init(ctx1, ctx2);
    }

    public void init(final ExprContext ctx1) {
        this.ctx1 = ctx1;
        this.ctx2 = null;
    }

    public ExprContextCombined init(final ExprContext ctx1, final ExprContext ctx2) {
        this.ctx1 = ctx1;
        this.ctx2 = ctx2;
        return this;
    }

    @Override
    public Variant get(final String name, final MutableVariant result) {
        // TODO: if we make sure ExprContext always contains native context then we won't need to have 2
        final Variant res = ctx1.get(name, result);
        return res != null ? res : ctx2 != null ? ctx2.get(name, result) : null;
    }

    @Override
    public Object getFunction(final String name) {
        final Object res = ctx1.getFunction(name);
        return res != null ? res : ctx2 != null ? ctx2.getFunction(name) : null;
    }

    @Override
    public String toString() {
        return (ctx1 != null ? "ctx1: " + ctx1 : "") + (ctx2 != null ? ", ctx2: " + ctx2 : "");
    }
}
