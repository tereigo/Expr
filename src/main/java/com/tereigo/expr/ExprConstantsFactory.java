package com.tereigo.expr;

import com.tereigo.expr.impl.ExprConstantsAccessor;

public final class ExprConstantsFactory {

    public static ExprConstantsBuilder create() {
        return ExprConstantsAccessor.create(PASS);
    }

    // Private constructor so only ExprConstantsFactory itself can create a Pass instance,
    // which is what actually restricts ExprConstantsAccessor to being called via this factory.
    public static class Pass {
        private Pass() { }
    }
    private static final Pass PASS = new Pass();

    private ExprConstantsFactory() { }
}
