package com.tereigo.expr;

import com.tereigo.expr.impl.experimental.FlatExprEvaluatorAccessor;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

/*
   Experimental alternative entry point for the clients.

   It mirrors ExprEvaluatorFactory's API exactly, but evaluation goes through the flat-AST
   pipeline (see com.tereigo.expr.impl.experimental) instead of walking the Expr tree directly.
   See ExprEvaluatorFactory for full usage documentation - both are used the same way:
        ExprEvaluatorWithContext evaluator = FlatExprEvaluatorFactory.create("1 == 1 + 1");
        evaluator.evaluateBool()
 */
public final class FlatExprEvaluatorFactory {

    public static ExprEvaluatorWithContext create(final ByteBuffer source) {
        return FlatExprEvaluatorAccessor.create(PASS, source, Collections.emptyMap());
    }

    public static ExprEvaluatorWithContext create(final String source) {
        return FlatExprEvaluatorAccessor.create(PASS, source, Collections.emptyMap());
    }

    public static ExprEvaluatorWithContext create(final ByteBuffer source, final Map<String, ExprConstant> constants) {
        return FlatExprEvaluatorAccessor.create(PASS, source, constants);
    }

    public static ExprEvaluatorWithContext create(final String source, final Map<String, ExprConstant> constants) {
        return FlatExprEvaluatorAccessor.create(PASS, source, constants);
    }

    /**
     * Optimized evaluation for the cases where the context is known upfront and it stays immutable for the evaluation
     * It optimizes the compiled AST tree before evaluation by resolving all function and object calls and storing them as the direct functions
     * It means it doesn't have to do function/object lookups via hash map during the evaluation
     *
     * @param ctx    - Static immutable evaluation context. This context will be used for the subsequent evaluation
     * @param source - Expression
     */
    public static ExprEvaluator create(final ExprContext ctx, final String source) {
        return FlatExprEvaluatorAccessor.create(PASS, ctx, source, Collections.emptyMap());
    }

    public static ExprEvaluator create(final ExprContext ctx, final String source, final Map<String, ExprConstant> constants) {
        return FlatExprEvaluatorAccessor.create(PASS, ctx, source, constants);
    }

    public static ExprEvaluator create(final ExprContext ctx, final ByteBuffer source) {
        return FlatExprEvaluatorAccessor.create(PASS, ctx, source, Collections.emptyMap());
    }

    public static ExprEvaluator create(final ExprContext ctx, final ByteBuffer source, final Map<String, ExprConstant> constants) {
        return FlatExprEvaluatorAccessor.create(PASS, ctx, source, constants);
    }

    // Private constructor so only FlatExprEvaluatorFactory itself can create a Pass instance,
    // which is what actually restricts FlatExprEvaluatorAccessor to being called via this factory.
    public static class Pass {
        private Pass() { }
    }
    private static final Pass PASS = new Pass();

    private FlatExprEvaluatorFactory() { }
}
