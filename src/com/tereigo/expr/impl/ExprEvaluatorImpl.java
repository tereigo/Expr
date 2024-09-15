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
        final Variant result = evaluate(ExprContextNative.get());
        try {
            return result.getAsBoolean();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public boolean evaluateBool(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsBoolean();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public long evaluateLong() {
        final Variant result = evaluate(ExprContextNative.get());
        try {
            return result.getAsLong();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public long evaluateLong(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsLong();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public double evaluateDouble() {
        final Variant result = evaluate(ExprContextNative.get());
        try {
            return result.getAsDouble();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public double evaluateDouble(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsDouble();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public double evaluateNumber() {
        final Variant result = evaluate(ExprContextNative.get());
        try {
            return result.getAsNumber();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public double evaluateNumber(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsNumber();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public String evaluateString() {
        final Variant result = evaluate(ExprContextNative.get());
        try {
            return result.getAsString();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public String evaluateString(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsString();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        final Variant result = evaluate(ExprContextNative.get());
        try {
            return result.getAsByteBuffer();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @Override
    public ByteBuffer evaluateByteBuffer(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsByteBuffer();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        final Variant result = evaluate(ExprContextNative.get());
        try {
            return result.getAsObject();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject(final ExprContext ctx) {
        final Variant result = evaluate(ctx);
        try {
            return result.getAsObject();
        } catch (final RuntimeException ex) {
            throw new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex));
        }
    }
}
