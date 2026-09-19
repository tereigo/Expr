package com.tereigo.expr.impl.experimental;

import com.tereigo.expr.ExprConstant;
import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorWithContext;
import com.tereigo.expr.FlatExprEvaluatorFactory;
import com.tereigo.expr.impl.ExprEvaluationWithContextTest;

import java.util.Map;

// Re-runs every test in ExprEvaluationWithContextTest against the experimental flat-AST evaluator
// (see com.tereigo.expr.impl.experimental) instead of the tree-walking one.
//
// Known gap: contextTestsAfterOptimizationForDebug() calls ExprEvaluatorFactory.create(ctx, ...)
// directly rather than through the overridable hooks, so it harmlessly re-tests the tree
// evaluator rather than the flat one when inherited here.
public class FlatExprEvaluationWithContextTest extends ExprEvaluationWithContextTest {

    @Override
    protected ExprEvaluatorWithContext createEvaluator(final String text) {
        return FlatExprEvaluatorFactory.create(text);
    }

    @Override
    protected ExprEvaluatorWithContext createEvaluator(final String text, final Map<String, ExprConstant> constants) {
        return FlatExprEvaluatorFactory.create(text, constants);
    }

    @Override
    protected ExprEvaluator createOptimizedEvaluator(final ExprContext ctx, final String text) {
        return FlatExprEvaluatorFactory.create(ctx, text);
    }

    @Override
    protected ExprEvaluator createOptimizedEvaluator(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        return FlatExprEvaluatorFactory.create(ctx, text, constants);
    }
}
