package com.tereigo.expr;

public final class ExprContextFactory {

    private ExprContextFactory() { }

    /**
     * For global context we start with the full set of native functions
     * We can also add some client-defined additional global "native" functions
     */
    public static MutableExprContext createGlobalContext(final ExprContextEnricher... enrichers) {
        final MutableExprContext ctx = createNative();
        ctx.enrich(enrichers);
        return ctx;
    }

    /**
     * For local contexts we start with an empty context
     * Local context is the one accessible via "." (dot) operator: "algo.name", "order.price"
     * In these cases "name" is the function available in the local context of "algo"
     * and "price" is the function available in the local context of "order"
     */
    public static MutableExprContext createLocalContext(final ExprContextEnricher... enrichers) {
        final MutableExprContext ctx = createEmpty();
        ctx.enrich(enrichers);
        return ctx;
    }

    static ExprContextImpl createNative() {
        final ExprContextImpl ctx = new ExprContextImpl();
        ctx.enrich(ExprContextNativeEnricher.get());
        return ctx;
    }

    private static MutableExprContext createEmpty() {
        return new ExprContextImpl();
    }
}
