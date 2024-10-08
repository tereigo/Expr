package com.tereigo.expr;

import com.sun.istack.internal.NotNull;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class ExprEvaluatorCache<T extends ExprEvaluator> {
    private final Map<ByteBuffer, T> cache = new HashMap<>();
    private final ExprEvaluatorCreator<T> creator;

    public ExprEvaluatorCache(final ExprEvaluatorCreator<T> creator) {
        this.creator = creator;
    }

    @GeneratesGarbage
    @NotNull
    public T getExprEvaluator(final ByteBuffer expr) {
        T entry = cache.get(expr);
        if (entry == null) {
            final ByteBuffer exprClone = ByteBufferUtils.clone(expr);
            entry = creator.create(exprClone);
            cache.put(exprClone, entry);
        }
        return entry;
    }
}

