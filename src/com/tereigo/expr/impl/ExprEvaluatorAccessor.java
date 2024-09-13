package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.ExprEvaluatorWithContext;

import java.nio.ByteBuffer;
import java.util.Objects;

public final class ExprEvaluatorAccessor {

    private ExprEvaluatorAccessor() {
    }

    public static ExprEvaluatorWithContext create(final ExprEvaluatorFactory.Pass pass,
                                                  final ByteBuffer source) {
        Objects.requireNonNull(pass);
        return new ExprEvaluatorImpl(source);
    }

    public static ExprEvaluatorWithContext create(final ExprEvaluatorFactory.Pass pass,
                                                  final String source) {
        Objects.requireNonNull(pass);
        return new ExprEvaluatorImpl(source);
    }

    /**
     * Optimized evaluation for the cases where the context is known upfront it and stays immutable for the evaluation
     * It optimized the compiled AST tree before evaluation by resolving all function and object calls and storing them as the direct functions
     * It means it doesn't have to do function/object lookups via hash map during the evaluation
     *
     * @param ctx    - Static immutable evaluation context. This context will be used for the subsequent evaluation
     * @param source - Expression
     */
    public static ExprEvaluator create(final ExprEvaluatorFactory.Pass ignoredPass,
                                       final ExprContext ctx,
                                       final String source) {
        return new ExprEvaluatorOptimized(ctx, source);
    }

    public static ExprEvaluator create(final ExprEvaluatorFactory.Pass ignoredPass,
                                       final ExprContext ctx,
                                       final ByteBuffer source) {
        return new ExprEvaluatorOptimized(ctx, source);
    }
}
