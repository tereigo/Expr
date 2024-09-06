package com.tereigo.expr;

public final class ExprContextFactory {

    private ExprContextFactory() { }

    public static MutableExprContext create() {
        return createEmpty();
    }

    /**
     * For global context we start with the full set of native functions
     */
    public static MutableExprContext createGlobalContext() {
        return createNative();
    }

    /**
     * Or we can also add some client-defined additional global "native" functions
     */
    public static MutableExprContext createGlobalContext(ExprContextEnricher... additionalEnricher) {
        final MutableExprContext ctx = createNative();
        ctx.enrich(additionalEnricher);
        return ctx;
    }

    /**
     * For local contexts we start with an empty context
     * Local context is the one accessible via "." (dot) operator: "algo.name", "order.price"
     * In these cases "name" is the function available in the local context of "algo"
     * and "price" is the function available in the local context of "order"
     */
    public static MutableExprContext createLocalContext(ExprContextEnricher... additionalEnricher) {
        final MutableExprContext ctx = createEmpty();
        ctx.enrich(additionalEnricher);
        return ctx;
    }

    private static MutableExprContext createEmpty() {
        return new ExprContextImpl();
    }

    static MutableExprContext createNative() {
        final MutableExprContext ctx = new ExprContextImpl();
        ctx.enrich(ExprContextNativeEnricher.get());
        return ctx;
    }
}
