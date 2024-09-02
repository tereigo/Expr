package com.tereigo.expr;

import com.tereigo.expr.variant.Variant;

import java.nio.ByteBuffer;

import static com.tereigo.expr.ExceptionUtils.getExceptionMsg;

/*
   This is the main public class for clients

   It can be used for the evaluation of the raw string:
        ExprEvaluator evaluator = new ExprEvaluator("1 == 1 + 1");
        evaluator.evaluateBool()

    And for evaluation of the precompiled expression:
        ASTRoot root = ExprCompiler.compile("$ric == 'VOD.L' and $productId == 123 or 5 != 2");
        ExprEvaluator exprEvaluator = new ExprEvaluator(root);
        exprEvaluator.evaluateBool(ctx);
 */
public final class ExprEvaluator {
    private final String source;
    private final ExprInterpreter interpreter;

    public ExprEvaluator(final ByteBuffer source) {
        this(ExprCompiler.compile(source));
    }

    public ExprEvaluator(final String source) {
        this(ExprCompiler.compile(source));
    }

    public ExprEvaluator(final ASTRoot root) {
        this.source = root.source();
        this.interpreter = new ExprInterpreter(root.expr());
    }

    public boolean evaluateBool() {
        Variant result = evaluateImpl(null);
        return result.getAsBoolean();
    }

    public boolean evaluateBool(final ExprContext ctx) {
        Variant result = evaluateImpl(ctx);
        return result.getAsBoolean();
    }

    public long evaluateLong() {
        Variant result = evaluateImpl(null);
        return result.getAsLong();
    }

    public long evaluateLong(final ExprContext ctx) {
        Variant result = evaluateImpl(ctx);
        return result.getAsLong();
    }

    public double evaluateDouble() {
        Variant result = evaluateImpl(null);
        return result.getAsDouble();
    }

    public double evaluateDouble(final ExprContext ctx) {
        Variant result = evaluateImpl(ctx);
        return result.getAsDouble();
    }

    public String evaluateString() {
        Variant result = evaluateImpl(null);
        return result.getAsString();
    }

    public String evaluateString(final ExprContext ctx) {
        Variant result = evaluateImpl(ctx);
        return result.getAsString();
    }

    public ByteBuffer evaluateByteBuffer() {
        Variant result = evaluateImpl(null);
        return result.getAsByteBuffer();
    }

    public ByteBuffer evaluateByteBuffer(final ExprContext ctx) {
        Variant result = evaluateImpl(ctx);
        return result.getAsByteBuffer();
    }

    public Object evaluateAsObject() {
        Variant result = evaluateImpl(null);
        return result.getAsObject();
    }

    public Object evaluateAsObject(final ExprContext ctx) {
        Variant result = evaluateImpl(ctx);
        return result.getAsObject();
    }

    private Variant evaluateImpl(final ExprContext ctx) {
        try {
            return ctx != null ? interpreter.evaluate(ctx) : interpreter.evaluate();
        } catch (RuntimeError err) {
            // Let's enhance the error with the relevant context info
            String msg = "Expression evaluation error [line " + err.token.line + ", pos " + (err.token.pos + 1) + "]: "
                    + err.getMessage() + (source.isEmpty() ? "" : " in expression '" + source + "'");
            throw new RuntimeError(err.token, msg);
        } catch (RuntimeException ex) {
            // Let's enhance the error with the relevant context info
            String msg = "Expression evaluation error: " + getExceptionMsg(ex) + getSourceString();
            throw new RuntimeException(msg, ex);
        }
    }

    private String getSourceString() {
        return source.isEmpty() ? "" : " in expression '" + source + "'";
    }
}
