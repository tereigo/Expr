package com.tereigo.atlas_expr;

import java.nio.ByteBuffer;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;

class ExprEvaluatorTestBase {
    protected static final double EPS = 0.00001;

    protected ExprContextImpl createContext() {
        final ExprContextImpl ctx = new ExprContextImpl();
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineLong("$productId", () -> 123L);
        ctx.defineString("$ric", () -> "VOD.L");
        ctx.defineBool("$enabled", () -> true);
        ctx.defineByteBuffer("$tuid", () -> constant("CLIENT1"));
        return ctx;
    }

    protected void evaluate(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        evaluator.evaluateAsObject();
    }

    protected void evaluate(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text, ctx);
        evaluator.evaluateAsObject();
    }

    protected long evaluateLong(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateLong();
    }

    protected long evaluateLong(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text, ctx);
        return evaluator.evaluateLong();
    }

    protected double evaluateDouble(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDouble(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text, ctx);
        return evaluator.evaluateDouble();
    }

    protected boolean evaluateBool(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBool(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text, ctx);
        return evaluator.evaluateBool();
    }

    protected String evaluateString(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateString();
    }

    protected String evaluateString(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text, ctx);
        return evaluator.evaluateString();
    }

    protected ByteBuffer evaluateByteBuffer(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text, ctx);
        return evaluator.evaluateByteBuffer();
    }

    protected double evaluateDouble(Expr expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateDouble();
    }

    protected long evaluateLong(Expr expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateLong();
    }

    protected boolean evaluateBool(Expr expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateBool();
    }

    protected String evaluateString(Expr expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateString();
    }

}