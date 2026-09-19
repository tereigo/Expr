package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;
import java.util.Map;

final class ExprEvaluatorOptimized extends ExprEvaluatorBase implements ExprEvaluator {
    private final ExprContext ctx;

    ExprEvaluatorOptimized(final ExprContext ctx, final String source, final Map<String, Expr.Literal> constants) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source, constants), ctx), ctx);
    }

    ExprEvaluatorOptimized(final ExprContext ctx, final ByteBuffer source, final Map<String, Expr.Literal> constants) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source, constants), ctx), ctx);
    }

    private ExprEvaluatorOptimized(final ASTRoot root, final ExprContext ctx) {
        super(root);
        this.ctx = ctx;
    }

    @Override
    public boolean evaluateBool() {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsBoolean();
        } catch (final RuntimeException ex) {
            throw wrapTypeError(ex);
        }
    }

    @Override
    public long evaluateLong() {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsLong();
        } catch (final RuntimeException ex) {
            throw wrapTypeError(ex);
        }
    }

    @Override
    public double evaluateDouble() {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsDouble();
        } catch (final RuntimeException ex) {
            throw wrapTypeError(ex);
        }
    }

    @Override
    public double evaluateNumber() {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsNumber();
        } catch (final RuntimeException ex) {
            throw wrapTypeError(ex);
        }
    }

    @Override
    public String evaluateString() {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsString();
        } catch (final RuntimeException ex) {
            throw wrapTypeError(ex);
        }
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsByteBuffer();
        } catch (final RuntimeException ex) {
            throw wrapTypeError(ex);
        }
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsObject();
        } catch (final RuntimeException ex) {
            throw wrapTypeError(ex);
        }
    }
}
