package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluatorWithContext;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;
import java.util.Map;

final class ExprEvaluatorImpl extends ExprEvaluatorBase implements ExprEvaluatorWithContext {

    ExprEvaluatorImpl(final ByteBuffer source) {
        this(ExprCompiler.compile(source));
    }

    ExprEvaluatorImpl(final ByteBuffer source, final Map<String, Expr.Literal> constants) {
        this(ExprCompiler.compile(source, constants));
    }

    ExprEvaluatorImpl(final String source) {
        this(ExprCompiler.compile(source));
    }

    ExprEvaluatorImpl(final String source, final Map<String, Expr.Literal> constants) {
        this(ExprCompiler.compile(source, constants));
    }

    ExprEvaluatorImpl(final ASTRoot root) {
        super(root);
    }

    @Override
    public boolean evaluateBool() {
        return evaluateBool(ExprContextNative.get());
    }

    @Override
    public boolean evaluateBool(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsBoolean();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
        }
    }

    @Override
    public long evaluateLong() {
        return evaluateLong(ExprContextNative.get());
    }

    @Override
    public long evaluateLong(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsLong();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
        }
    }

    @Override
    public double evaluateDouble() {
        return evaluateDouble(ExprContextNative.get());
    }

    @Override
    public double evaluateDouble(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsDouble();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
        }
    }

    @Override
    public double evaluateNumber() {
        return evaluateNumber(ExprContextNative.get());
    }

    @Override
    public double evaluateNumber(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsNumber();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
        }
    }

    @Override
    public String evaluateString() {
        return evaluateString(ExprContextNative.get());
    }

    @Override
    public String evaluateString(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsString();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
        }
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        return evaluateByteBuffer(ExprContextNative.get());
    }

    @Override
    public ByteBuffer evaluateByteBuffer(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsByteBuffer();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
        }
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        return evaluateAsObject(ExprContextNative.get());
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsObject();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
        }
    }
}
