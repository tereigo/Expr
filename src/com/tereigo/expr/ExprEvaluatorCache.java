package com.tereigo.expr;

import com.sun.istack.internal.NotNull;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

// TODO: test
final class ExprEvaluatorCache {
    private final Map<ByteBuffer, ExprEvaluator> compiledExpressions = new HashMap<>();

    @GeneratesGarbage
    @NotNull
    public ExprEvaluator getExprEvaluator(final ByteBuffer expr) {
        ExprEvaluator entry = compiledExpressions.get(expr);
        if (entry == null) {
            final ByteBuffer exprClone = ByteBufferUtils.clone(expr);
            entry = new ExprEvaluator(exprClone);
            compiledExpressions.put(exprClone, entry);
        }
        return entry;
    }
}

