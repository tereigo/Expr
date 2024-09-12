package com.tereigo.expr;

import com.tereigo.expr.impl.ExprEvaluatorAccessor;

import java.nio.ByteBuffer;

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

    public static ExprEvaluatorWithContext create(final ByteBuffer source) {
        return ExprEvaluatorAccessor.create(PASS, source);
    }

    public static ExprEvaluatorWithContext create(final String source) {
        return ExprEvaluatorAccessor.create(PASS, source);
    }

    /**
     * Optimized evaluation for the cases where the context is known upfront it and stays immutable for the evaluation
     * It optimized the compiled AST tree before evaluation by resolving all function and object calls and storing them as the direct functions
     * It means it doesn't have to do function/object lookups via hash map during the evaluation
     * @param ctx - Static immutable evaluation context. This context will be used for the subsequent evaluation
     * @param source - Expression
     */
    public static ExprEvaluator create(final ExprContext ctx, final String source) {
        return ExprEvaluatorAccessor.create(PASS, ctx, source);
    }

    public static ExprEvaluator create(final ExprContext ctx, final ByteBuffer source) {
        return ExprEvaluatorAccessor.create(PASS, ctx, source);
    }

    private ExprEvaluatorFactory() { }

    private static final Pass PASS = new Pass();
    public static class Pass { }
}
