package com.tereigo.expr;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;

class ExprEvaluatorTestBase {
    protected static final double EPS = 0.00001;

    protected ExprContext createContext() {
        final MutableExprContext ctx = ExprContextFactory.createGlobalContext();
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineLong("$productId", () -> 123L);
        ctx.defineString("$ric", () -> "VOD.L");
        ctx.defineBool("$enabled", () -> true);
        ctx.defineByteBuffer("$tuid", () -> constant("CLIENT1"));
        return ctx.getAsExprContext();
    }

    protected void evaluate(final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        evaluator.evaluateAsObject();
    }

    protected void evaluate(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        evaluator.evaluateAsObject(ctx);
    }

    protected void evaluateOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        evaluator.evaluateAsObject(ctx);
    }

    protected void evaluate(final ASTRoot root, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(root);
        evaluator.evaluateAsObject(ctx);
    }

    protected boolean evaluateBool(final ASTRoot root, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(root);
        return evaluator.evaluateBool(ctx);
    }

    protected long evaluateLong(final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateLong();
    }

    protected long evaluateLong(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateLong(ctx);
    }

    protected long evaluateLongOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateLong(ctx);
    }

    protected double evaluateDouble(final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDouble(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateDouble(ctx);
    }

    protected double evaluateDoubleOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateDouble(ctx);
    }

    protected boolean evaluateBool(final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBool(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateBool(ctx);
    }

    protected boolean evaluateBoolOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateBool(ctx);
    }

    protected String evaluateString(final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateString();
    }

    protected String evaluateString(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateString(ctx);
    }

    protected String evaluateStringOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateString(ctx);
    }

    protected ByteBuffer evaluateByteBuffer(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateByteBuffer(ctx);
    }

    protected ByteBuffer evaluateByteBufferOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = new ExprEvaluator(ctx, text);
        return evaluator.evaluateByteBuffer(ctx);
    }

    protected double evaluateDouble(final ASTRoot expr) {
        final ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateDouble();
    }

    protected long evaluateLong(final ASTRoot expr) {
        final ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateLong();
    }

    protected boolean evaluateBool(final ASTRoot expr) {
        final ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateBool();
    }

    protected String evaluateString(final ASTRoot expr) {
        final ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        return exprEvaluator.evaluateString();
    }
}