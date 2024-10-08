package com.tereigo.expr;

import com.tereigo.expr.impl.ExprEvaluatorCacheAccessor;

public final class ExprEvaluatorCacheFactory {

    public static <T extends ExprEvaluator> ExprEvaluatorSupplier<T> create(final ExprEvaluatorCreator<T> creator) {
        return ExprEvaluatorCacheAccessor.create(PASS, creator);
    }

    public static class Pass { }
    private static final Pass PASS = new Pass();

    private ExprEvaluatorCacheFactory() { }
}
