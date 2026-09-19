package com.tereigo.expr;

import com.tereigo.expr.impl.ExprEvaluatorCacheAccessor;

public final class ExprEvaluatorCacheFactory {

    public static <T extends ExprEvaluator> ExprEvaluatorSupplier<T> create(final ExprEvaluatorCreator<T> creator) {
        return ExprEvaluatorCacheAccessor.create(PASS, creator);
    }

    // Private constructor so only ExprEvaluatorCacheFactory itself can create a Pass instance,
    // which is what actually restricts ExprEvaluatorCacheAccessor to being called via this factory.
    public static class Pass {
        private Pass() { }
    }
    private static final Pass PASS = new Pass();

    private ExprEvaluatorCacheFactory() { }
}
