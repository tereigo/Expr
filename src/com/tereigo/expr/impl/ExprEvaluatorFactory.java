package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorWithContext;

import java.nio.ByteBuffer;

/*
   This is the main public class for clients

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
// TODO: can we move it to the interface somehow?
public final class ExprEvaluatorFactory {

    public static ExprEvaluatorWithContext create(final ByteBuffer source) {
        return new ExprEvaluatorImpl(source);
    }

    public static ExprEvaluatorWithContext create(final String source) {
        return new ExprEvaluatorImpl(source);
    }

    /**
     * Optimized evaluation for the cases where the context is known upfront it and stays immutable for the evaluation
     * It optimized the compiled AST tree before evaluation by resolving all function and object calls and storing them as the direct functions
     * It means it doesn't have to do function/object lookups via hash map during the evaluation
     * @param ctx - Static immutable evaluation context. This context will be used for the subsequent evaluation
     * @param source - Expression
     */
    public static ExprEvaluator create(final ExprContext ctx, final String source) {
        return new OptimizedExprEvaluator(ctx, source);
    }

    public static ExprEvaluator create(final ExprContext ctx, final ByteBuffer source) {
        return new OptimizedExprEvaluator(ctx, source);
    }

    private ExprEvaluatorFactory() { }
}
