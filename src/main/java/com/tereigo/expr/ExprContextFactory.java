package com.tereigo.expr;

import com.tereigo.expr.impl.ExprContextAccessor;

public final class ExprContextFactory {

    /**
     * For global context we start with the full set of native functions
     * We can also add some additional client-defined global "native" functions
     */
    public static ExprContextBuilder globalContext(final ExprContextEnricher... enrichers) {
        final ExprContextBuilder ctx = ExprContextAccessor.createNative(PASS);
        ctx.enrich(enrichers);
        return ctx;
    }

    /**
     * For local contexts we start with an empty context
     * Local context is the one accessible via "." (dot) operator: "algo.name", "order.price"
     * In these cases "name" is the function available in the local context of "algo"
     * and "price" is the function available in the local context of "order"
     */
    public static ExprContextBuilder localContext(final ExprContextEnricher... enrichers) {
        final ExprContextBuilder ctx = ExprContextAccessor.createEmpty(PASS);
        ctx.enrich(enrichers);
        return ctx;
    }

    // Private constructor so only ExprContextFactory itself can create a Pass instance,
    // which is what actually restricts ExprContextAccessor to being called via this factory.
    public static class Pass {
        private Pass() { }
    }
    private static final Pass PASS = new Pass();

    private ExprContextFactory() { }

}
