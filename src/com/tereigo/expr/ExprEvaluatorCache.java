package com.tereigo.expr;

import com.sun.istack.internal.NotNull;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

// TODO: test
final class ExprEvaluatorCache {
    private final Map<ByteBuffer, ExprEvaluatorWithContext> cache = new HashMap<>();

    @GeneratesGarbage
    @NotNull
    public ExprEvaluatorWithContext getExprEvaluator(final ByteBuffer expr) {
        ExprEvaluatorWithContext entry = cache.get(expr);
        if (entry == null) {
            final ByteBuffer exprClone = ByteBufferUtils.clone(expr);
            entry = ExprEvaluatorFactory.create(exprClone);
            cache.put(exprClone, entry);
        }
        return entry;
    }
}

