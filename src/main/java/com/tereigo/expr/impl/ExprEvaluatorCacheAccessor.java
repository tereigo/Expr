package com.tereigo.expr.impl;

import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorCacheFactory;
import com.tereigo.expr.ExprEvaluatorCreator;
import com.tereigo.expr.ExprEvaluatorSupplier;

import java.util.Objects;

public final class ExprEvaluatorCacheAccessor {

    private ExprEvaluatorCacheAccessor() { }

    public static <T extends ExprEvaluator> ExprEvaluatorSupplier<T> create(final ExprEvaluatorCacheFactory.Pass pass,
                                                                            final ExprEvaluatorCreator<T> creator) {
        Objects.requireNonNull(pass);
        return new ExprEvaluatorCache<>(creator);
    }
}
