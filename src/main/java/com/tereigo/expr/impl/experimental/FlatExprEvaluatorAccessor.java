package com.tereigo.expr.impl.experimental;

import com.tereigo.expr.ExprConstant;
import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorWithContext;
import com.tereigo.expr.FlatExprEvaluatorFactory;
import com.tereigo.expr.impl.Expr;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public final class FlatExprEvaluatorAccessor {

    private FlatExprEvaluatorAccessor() { }

    public static ExprEvaluatorWithContext create(final FlatExprEvaluatorFactory.Pass pass,
                                                  final ByteBuffer source,
                                                  final Map<String, ExprConstant> constants) {
        Objects.requireNonNull(pass);
        return new FlatExprEvaluatorImpl(source, (Map<String, Expr.Literal>)(Map<String, ?>) constants);
    }

    public static ExprEvaluatorWithContext create(final FlatExprEvaluatorFactory.Pass pass,
                                                  final String source,
                                                  final Map<String, ExprConstant> constants) {
        Objects.requireNonNull(pass);
        return new FlatExprEvaluatorImpl(source, (Map<String, Expr.Literal>)(Map<String, ?>) constants);
    }

    /**
     * Optimized evaluation for the cases when ExprContext is known upfront and it stays immutable for the evaluation
     * It optimizes the compiled AST tree before evaluation by resolving all function and object calls and storing
     * them as the direct functions, then flattens the optimized tree.
     *
     * @param ctx    - Static immutable evaluation context. This context will be used for the subsequent evaluation
     * @param source - Expression
     */
    public static ExprEvaluator create(final FlatExprEvaluatorFactory.Pass pass,
                                       final ExprContext ctx,
                                       final String source,
                                       final Map<String, ExprConstant> constants) {
        Objects.requireNonNull(pass);
        return new FlatExprEvaluatorOptimized(ctx, source, (Map<String, Expr.Literal>)(Map<String, ?>) constants);
    }

    public static ExprEvaluator create(final FlatExprEvaluatorFactory.Pass pass,
                                       final ExprContext ctx,
                                       final ByteBuffer source,
                                       final Map<String, ExprConstant> constants) {
        Objects.requireNonNull(pass);
        return new FlatExprEvaluatorOptimized(ctx, source, (Map<String, Expr.Literal>)(Map<String, ?>) constants);
    }
}
