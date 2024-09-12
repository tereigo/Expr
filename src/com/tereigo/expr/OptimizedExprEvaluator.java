package com.tereigo.expr;

import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;

import static com.tereigo.expr.ExceptionUtils.getExceptionMsg;

final class OptimizedExprEvaluator implements ExprEvaluator {
    private final String source;
    private final ExprInterpreter interpreter;
    private final ExprContext ctx;

    OptimizedExprEvaluator(final ExprContext ctx, final String source) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source), ctx), ctx);
    }

    OptimizedExprEvaluator(final ExprContext ctx, final ByteBuffer source) {
        this(ExprOptimizer.optimize(ExprCompiler.compile(source), ctx), ctx);
    }

    OptimizedExprEvaluator(final ASTRoot root, final ExprContext ctx) {
        this.source = root.source();
        this.interpreter = new ExprInterpreter(root.expr());
        this.ctx = ctx;
    }

    @Override
    public boolean evaluateBool() {
        final Variant result = evaluateImpl(ctx);
        return result.getAsBoolean();
    }

    @Override
    public long evaluateLong() {
        final Variant result = evaluateImpl(ctx);
        return result.getAsLong();
    }

    @Override
    public double evaluateDouble() {
        final Variant result = evaluateImpl(ctx);
        return result.getAsDouble();
    }

    @Override
    public String evaluateString() {
        final Variant result = evaluateImpl(ctx);
        return result.getAsString();
    }

    @Override
    public ByteBuffer evaluateByteBuffer() {
        final Variant result = evaluateImpl(ctx);
        return result.getAsByteBuffer();
    }

    @GeneratesGarbage
    @Override
    public Object evaluateAsObject() {
        final Variant result = evaluateImpl(ctx);
        return result.getAsObject();
    }

    private Variant evaluateImpl(final ExprContext ctx) {
        try {
            return interpreter.evaluate(ctx);
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
