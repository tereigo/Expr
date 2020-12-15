package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.Variant;

import java.nio.ByteBuffer;

/*
   This is the main public class for clients

   It can be used for the evaluation of the raw string:
        ExprEvaluator evaluator = new ExprEvaluator("1 == 1 + 1");
        evaluator.evaluateBool()

    And for evaluation of the precompiled expression:
        Expr expr = ExprCompiler.compile("$ric == \"VOD.L\" and $productId == 123 or 5 != 2");
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr, ctx);
        exprEvaluator.evaluateBool();
 */
public final class ExprEvaluator {
    private final Interpreter interpreter;

    public ExprEvaluator(final String source) {
        this(ExprCompiler.compile(source));
    }

    public ExprEvaluator(final String source, final ExprContext ctx) {
        this(ExprCompiler.compile(source), ctx);
    }

    public ExprEvaluator(final Expr expression) {
        this.interpreter = new Interpreter(expression);
    }

    public ExprEvaluator(final Expr expression, final ExprContext ctx) {
        this.interpreter = new Interpreter(expression, ctx);
    }

    public boolean evaluateBool() {
        Variant result = evaluateImpl();
        return result.getAsBoolean();
    }

    public long evaluateLong() {
        Variant result = evaluateImpl();
        return result.getAsLong();
    }

    public double evaluateDouble() {
        Variant result = evaluateImpl();
        return result.getAsDouble();
    }

    public String evaluateString() {
        Variant result = evaluateImpl();
        return result.getAsString();
    }

    public ByteBuffer evaluateByteBuffer() {
        Variant result = evaluateImpl();
        return result.getAsByteBuffer();
    }

    public Object evaluateAsObject() {
        Variant result = evaluateImpl();
        return result.getAsObject();
    }

    private Variant evaluateImpl() {
        return interpreter.evaluate();
    }

}
