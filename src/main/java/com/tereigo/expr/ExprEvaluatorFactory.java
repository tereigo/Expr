package com.tereigo.expr;

import com.tereigo.expr.impl.ExprEvaluatorAccessor;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

/*
   This is the main entry point for the clients

   It can be used for the evaluation of the raw string:
        ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create("1 == 1 + 1");
        evaluator.evaluateBool()

    And for evaluation with the provided context:
        ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create("$ric == 'VOD.L' and $productId == 123 or 5 != 2");
        evaluator.evaluateBool(ctx);

    And for the optimized evaluation where the context is static and known upfront at the moment of compilation:
        ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, "$ric == 'VOD.L' and $productId == 123 or 5 != 2");
        evaluator.evaluateBool();
 */
public final class ExprEvaluatorFactory {

    /**
     * This interface is to create ExprEvaluator which either used only native functions (evaluator.evaluate())
     * or the context will be supplied during evaluation (evaluator.evaluate(exprContext))
     */
    public static ExprEvaluatorWithContext create(final ByteBuffer source) {
        return ExprEvaluatorAccessor.create(PASS, source, Collections.emptyMap());
    }

    public static ExprEvaluatorWithContext create(final String source) {
        return ExprEvaluatorAccessor.create(PASS, source, Collections.emptyMap());
    }

    public static ExprEvaluatorWithContext create(final ByteBuffer source, final Map<String, ?> constants) {
        return ExprEvaluatorAccessor.create(PASS, source, constants);
    }

    public static ExprEvaluatorWithContext create(final String source, final Map<String, ?> constants) {
        return ExprEvaluatorAccessor.create(PASS, source, constants);
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
        return ExprEvaluatorAccessor.create(PASS, ctx, source, Collections.emptyMap());
    }

    public static ExprEvaluator create(final ExprContext ctx, final String source, final Map<String, ?> constants) {
        return ExprEvaluatorAccessor.create(PASS, ctx, source, constants);
    }

    public static ExprEvaluator create(final ExprContext ctx, final ByteBuffer source) {
        return ExprEvaluatorAccessor.create(PASS, ctx, source, Collections.emptyMap());
    }

    public static ExprEvaluator create(final ExprContext ctx, final ByteBuffer source, final Map<String, ?> constants) {
        return ExprEvaluatorAccessor.create(PASS, ctx, source, constants);
    }

    // Private constructor so only ExprEvaluatorFactory itself can create a Pass instance,
    // which is what actually restricts ExprEvaluatorAccessor to being called via this factory.
    public static class Pass {
        private Pass() { }
    }
    private static final Pass PASS = new Pass();

    private ExprEvaluatorFactory() { }
}
