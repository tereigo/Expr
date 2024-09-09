package com.tereigo.expr;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;

class ExprEvaluatorTestBase {
    protected static final double EPS = 0.00001;

    protected ExprContext createContext() {
        final MutableExprContext ctx = ExprContextFactory.create();
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
        ExprEvaluator evaluator = new ExprEvaluator(text);
        evaluator.evaluateAsObject(ctx);
    }

    protected void evaluateOptimized(ExprContext ctx, String text) {
        ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        evaluator.evaluateAsObject(ctx);
    }

    protected void evaluate(ASTRoot root, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(root);
        evaluator.evaluateAsObject(ctx);
    }

    protected boolean evaluateBool(ASTRoot root, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(root);
        return evaluator.evaluateBool(ctx);
    }

    protected long evaluateLong(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateLong();
    }

    protected long evaluateLong(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateLong(ctx);
    }

    protected long evaluateLongOptimized(ExprContext ctx, String text) {
        ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateLong(ctx);
    }

    protected double evaluateDouble(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDouble(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateDouble(ctx);
    }

    protected double evaluateDoubleOptimized(ExprContext ctx, String text) {
        ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateDouble(ctx);
    }

    protected boolean evaluateBool(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBool(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateBool(ctx);
    }

    protected boolean evaluateBoolOptimized(ExprContext ctx, String text) {
        ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateBool(ctx);
    }

    protected String evaluateString(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateString();
    }

    protected String evaluateString(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateString(ctx);
    }

    protected String evaluateStringOptimized(ExprContext ctx, String text) {
        ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateString(ctx);
    }

    protected ByteBuffer evaluateByteBuffer(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateByteBuffer(ctx);
    }

    protected ByteBuffer evaluateByteBufferOptimized(ExprContext ctx, String text) {
        ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateByteBuffer(ctx);
    }

    protected double evaluateDouble(ASTRoot expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateDouble();
    }

    protected long evaluateLong(ASTRoot expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateLong();
    }

    protected boolean evaluateBool(ASTRoot expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateBool();
    }

    protected String evaluateString(ASTRoot expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateString();
    }
}