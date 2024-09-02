package com.tereigo.expr;

public final class ExprContextFactory {

    private ExprContextFactory() { }

    public static MutableExprContext create() {
        return createEmpty();
    }

    public static MutableExprContext createEmpty() {
        return new ExprContextImpl();
    }

    public static MutableExprContext createNative() {
        final MutableExprContext ctx = new ExprContextImpl();
        ctx.enrich(ExprContextNativeEnricher.get());
        return ctx;
    }
}
