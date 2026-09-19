package com.tereigo.expr.impl;

import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorCreator;
import com.tereigo.expr.ExprEvaluatorSupplier;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.annotations.NotThreadSafe;
import com.tereigo.expr.utils.ByteBufferUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.HashMap;

// `cache` is a plain HashMap with no synchronization. Concurrent calls to getEvaluator()
// from multiple threads can corrupt the map (e.g. during a resize) or race on the
// get/create/put sequence below. Confine an instance to a single thread, or synchronize
// access externally, until this is changed to something like a ConcurrentHashMap.
@NotThreadSafe
final class ExprEvaluatorCache<T extends ExprEvaluator> implements ExprEvaluatorSupplier<T> {
    private final HashMap<ByteBuffer, T> cache = new HashMap<>();
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

