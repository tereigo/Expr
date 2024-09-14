package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;

final class ExprEvaluatorOptimized extends ExprEvaluatorBase implements ExprEvaluator {
    private final ExprContext ctx;

    ExprEvaluatorOptimized(final ExprContext ctx, final String source) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source), ctx), ctx);
    }

    ExprEvaluatorOptimized(final ExprContext ctx, final ByteBuffer source) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source), ctx), ctx);
    }

    private ExprEvaluatorOptimized(final ASTRoot root, final ExprContext ctx) {
        super(root);
        this.ctx = ctx;
    }

    @Override
    public boolean evaluateBool() {
        final Variant result = evaluate(ctx);
        return result.getAsBoolean();
    }

    @Override
    public long evaluateLong() {
        final Variant result = evaluate(ctx);
        return result.getAsLong();
    }

    @Override
    public double evaluateDouble() {
        final Variant result = evaluate(ctx);
        return result.getAsDouble();
    }

    @Override
    public double evaluateNumber() {
        final Variant result = evaluate(ctx);
        return result.getAsNumber();
    }

    @Override
    public String evaluateString() {
        final Variant result = evaluate(ctx);
        return result.getAsString();
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        final Variant result = evaluate(ctx);
        return result.getAsByteBuffer();
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        final Variant result = evaluate(ctx);
        return result.getAsObject();
    }
}
