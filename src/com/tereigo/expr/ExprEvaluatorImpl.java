package com.tereigo.expr;

import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;

import static com.tereigo.expr.ExceptionUtils.getExceptionMsg;

final class ExprEvaluatorImpl implements ExprEvaluatorWithContext {
    private final String source;
    private final ExprInterpreter interpreter;

    ExprEvaluatorImpl(final ByteBuffer source) {
        this(ExprCompiler.compile(source));
    }

    ExprEvaluatorImpl(final String source) {
        this(ExprCompiler.compile(source));
    }

    ExprEvaluatorImpl(final ASTRoot root) {
        this.source = root.source();
        this.interpreter = new ExprInterpreter(root.expr());
    }

    @Override
    public boolean evaluateBool() {
        final Variant result = evaluateImpl(null);
        return result.getAsBoolean();
    }

    @Override
    public boolean evaluateBool(final ExprContext ctx) {
        final Variant result = evaluateImpl(ctx);
        return result.getAsBoolean();
    }

    @Override
    public long evaluateLong() {
        final Variant result = evaluateImpl(null);
        return result.getAsLong();
    }

    @Override
    public long evaluateLong(final ExprContext ctx) {
        final Variant result = evaluateImpl(ctx);
        return result.getAsLong();
    }

    @Override
    public double evaluateDouble() {
        final Variant result = evaluateImpl(null);
        return result.getAsDouble();
    }

    @Override
    public double evaluateDouble(final ExprContext ctx) {
        final Variant result = evaluateImpl(ctx);
        return result.getAsDouble();
    }

    @Override
    public String evaluateString() {
        final Variant result = evaluateImpl(null);
        return result.getAsString();
    }

    @Override
    public String evaluateString(final ExprContext ctx) {
        final Variant result = evaluateImpl(ctx);
        return result.getAsString();
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        final Variant result = evaluateImpl(null);
        return result.getAsByteBuffer();
    }

    @Override
    public ByteBuffer evaluateByteBuffer(final ExprContext ctx) {
        final Variant result = evaluateImpl(ctx);
        return result.getAsByteBuffer();
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        final Variant result = evaluateImpl(null);
        return result.getAsObject();
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject(final ExprContext ctx) {
        final Variant result = evaluateImpl(ctx);
        return result.getAsObject();
    }

    private Variant evaluateImpl(final ExprContext ctx) {
        try {
            return ctx != null ? interpreter.evaluate(ctx) : interpreter.evaluate();
        } catch (final RuntimeError err) {
            // Let's enhance the error with the relevant context info
            final String msg = "Expression evaluation error [line " + err.token.line + ", pos " + (err.token.pos + 1) + "]: "
                    + err.getMessage() + getSourceString();
            throw new RuntimeError(err.token, msg);
        } catch (final RuntimeException ex) {
            // Let's enhance the error with the relevant context info
            final String msg = "Expression evaluation error: " + getExceptionMsg(ex) + getSourceString();
            throw new RuntimeException(msg, ex);
        }
    }

    private String getSourceString() {
        return source.isEmpty() ? "" : " in expression '" + source + "'";
    }
}
