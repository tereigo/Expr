package com.tereigo.expr;

import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;

final class OptimizedExprEvaluator implements ExprEvaluator {
    private final ExprEvaluatorInterpreter interpreter;
    private final ExprContext ctx;

    OptimizedExprEvaluator(final ExprContext ctx, final String source) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source), ctx), ctx);
    }

    OptimizedExprEvaluator(final ExprContext ctx, final ByteBuffer source) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source), ctx), ctx);
    }

    private OptimizedExprEvaluator(final ASTRoot root, final ExprContext ctx) {
        this.interpreter = new ExprEvaluatorInterpreter(root);
        this.ctx = ctx;
    }

    @Override
    public boolean evaluateBool() {
        final Variant result = interpreter.evaluate(ctx);
        return result.getAsBoolean();
    }

    @Override
    public long evaluateLong() {
        final Variant result = interpreter.evaluate(ctx);
        return result.getAsLong();
    }

    @Override
    public double evaluateDouble() {
        final Variant result = interpreter.evaluate(ctx);
        return result.getAsDouble();
    }

    @Override
    public String evaluateString() {
        final Variant result = interpreter.evaluate(ctx);
        return result.getAsString();
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        final Variant result = interpreter.evaluate(ctx);
        return result.getAsByteBuffer();
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        final Variant result = interpreter.evaluate(ctx);
        return result.getAsObject();
    }
}
