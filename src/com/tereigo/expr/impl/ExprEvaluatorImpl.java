package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluatorWithContext;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;

final class ExprEvaluatorImpl extends ExprEvaluatorBase implements ExprEvaluatorWithContext {

    ExprEvaluatorImpl(final ByteBuffer source) {
        this(ExprCompiler.compile(source));
    }

    ExprEvaluatorImpl(final String source) {
        this(ExprCompiler.compile(source));
    }

    ExprEvaluatorImpl(final ASTRoot root) {
        super(root);
    }

    @Override
    public boolean evaluateBool() {
        final Variant result = evaluate(ExprContextNative.get());
        return result.getAsBoolean();
    }

    @Override
    public boolean evaluateBool(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        return result.getAsBoolean();
    }

    @Override
    public long evaluateLong() {
        final Variant result = evaluate(ExprContextNative.get());
        return result.getAsLong();
    }

    @Override
    public long evaluateLong(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        return result.getAsLong();
    }

    @Override
    public double evaluateDouble() {
        final Variant result = evaluate(ExprContextNative.get());
        return result.getAsDouble();
    }

    @Override
    public double evaluateDouble(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        return result.getAsDouble();
    }

    @Override
    public String evaluateString() {
        final Variant result = evaluate(ExprContextNative.get());
        return result.getAsString();
    }

    @Override
    public String evaluateString(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        return result.getAsString();
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        final Variant result = evaluate(ExprContextNative.get());
        return result.getAsByteBuffer();
    }

    @Override
    public ByteBuffer evaluateByteBuffer(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        return result.getAsByteBuffer();
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        final Variant result = evaluate(ExprContextNative.get());
        return result.getAsObject();
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        return result.getAsObject();
    }
}
