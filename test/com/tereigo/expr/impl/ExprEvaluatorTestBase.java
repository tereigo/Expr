package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.ExprEvaluatorWithContext;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;

class ExprEvaluatorTestBase {
    protected static final double EPS = 0.00001;

    protected ExprContext createContext() {
        final ExprContextBuilder ctx = ExprContextFactory.globalContext();
        ctx.addDouble("$PI", () -> 3.14);
        ctx.addLong("$productId", () -> 123L);
        ctx.addString("$ric", () -> "VOD.L");
        ctx.addBool("$enabled", () -> true);
        ctx.addByteBuffer("$tuid", () -> constant("CLIENT1"));
        return ctx.getAsExprContext();
    }

    protected void evaluate(final String text) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        evaluator.evaluateAsObject();
    }

    protected void evaluate(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        evaluator.evaluateAsObject(ctx);
    }

    protected void evaluateOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        evaluator.evaluateAsObject();
    }

    protected long evaluateLong(final String text) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateLong();
    }

    protected long evaluateLong(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateLong(ctx);
    }

    protected long evaluateLongOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        return evaluator.evaluateLong();
    }

    protected double evaluateDouble(final String text) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDouble(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateDouble(ctx);
    }

    protected double evaluateDoubleOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        return evaluator.evaluateDouble();
    }

    protected boolean evaluateBool(final String text) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBool(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateBool(ctx);
    }

    protected boolean evaluateBoolOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        return evaluator.evaluateBool();
    }

    protected String evaluateString(final String text) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateString();
    }

    protected String evaluateString(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateString(ctx);
    }

    protected String evaluateStringOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        return evaluator.evaluateString();
    }

    protected ByteBuffer evaluateByteBuffer(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateByteBuffer(ctx);
    }

    protected ByteBuffer evaluateByteBufferOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        return evaluator.evaluateByteBuffer();
    }

    protected void evaluate(final ASTRoot root, final ExprContext ctx) {
        final ExprEvaluatorImpl evaluator = new ExprEvaluatorImpl(root);
        evaluator.evaluateAsObject(ctx);
    }

    protected boolean evaluateBool(final ASTRoot root, final ExprContext ctx) {
        final ExprEvaluatorImpl evaluator = new ExprEvaluatorImpl(root);
        return evaluator.evaluateBool(ctx);
    }

    protected double evaluateDouble(final ASTRoot expr) {
        final ExprEvaluatorImpl exprEvaluator = new ExprEvaluatorImpl(expr);
        return exprEvaluator.evaluateDouble();
    }

    protected long evaluateLong(final ASTRoot expr) {
        final ExprEvaluatorImpl exprEvaluator = new ExprEvaluatorImpl(expr);
        return exprEvaluator.evaluateLong();
    }

    protected boolean evaluateBool(final ASTRoot expr) {
        final ExprEvaluatorImpl exprEvaluator = new ExprEvaluatorImpl(expr);
        return exprEvaluator.evaluateBool();
    }

    protected String evaluateString(final ASTRoot expr) {
        final ExprEvaluatorImpl exprEvaluator = new ExprEvaluatorImpl(expr);
        return exprEvaluator.evaluateString();
    }
}