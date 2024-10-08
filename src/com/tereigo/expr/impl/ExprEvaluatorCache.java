package com.tereigo.expr.impl;

import com.sun.istack.internal.NotNull;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorCreator;
import com.tereigo.expr.ExprEvaluatorSupplier;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

final class ExprEvaluatorCache<T extends ExprEvaluator> implements ExprEvaluatorSupplier<T> {
    private final Map<ByteBuffer, T> cache = new HashMap<>();
    private final ExprEvaluatorCreator<T> creator;

    public ExprEvaluatorCache(final ExprEvaluatorCreator<T> creator) {
        this.creator = creator;
    }

    @GeneratesGarbage
    @NotNull
    @Override
    public T getEvaluator(final ByteBuffer expr) {
        T entry = cache.get(expr);
        if (entry == null) {
            final ByteBuffer exprClone = ByteBufferUtils.clone(expr);
            entry = creator.create(exprClone);
            cache.put(exprClone, entry);
        }
        return entry;
    }
}

