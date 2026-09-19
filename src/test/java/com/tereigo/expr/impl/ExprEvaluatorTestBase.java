package com.tereigo.expr.impl;

import com.tereigo.expr.ExprConstant;
import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.ExprEvaluatorWithContext;

import java.nio.ByteBuffer;
import java.util.Map;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;

public class ExprEvaluatorTestBase {
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

    // Overridable hooks: a subclass wanting to run the same tests against a different
    // evaluation pipeline (e.g. the experimental flat-AST evaluator) only needs to override these 4.
    protected ExprEvaluatorWithContext createEvaluator(final String text) {
        return ExprEvaluatorFactory.create(text);
    }

    protected ExprEvaluatorWithContext createEvaluator(final String text, final Map<String, ExprConstant> constants) {
        return ExprEvaluatorFactory.create(text, constants);
    }

    protected ExprEvaluator createOptimizedEvaluator(final ExprContext ctx, final String text) {
        return ExprEvaluatorFactory.create(ctx, text);
    }

    protected ExprEvaluator createOptimizedEvaluator(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        return ExprEvaluatorFactory.create(ctx, text, constants);
    }

    protected void evaluate(final String text) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        evaluator.evaluateAsObject();
    }

    protected void evaluate(final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        evaluator.evaluateAsObject();
    }

    protected void evaluate(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        evaluator.evaluateAsObject(ctx);
    }

    protected void evaluate(final String text, final Map<String, ExprConstant> constants, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        evaluator.evaluateAsObject(ctx);
    }

    protected void evaluateOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text);
        evaluator.evaluateAsObject();
    }

    protected void evaluateOptimized(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text, constants);
        evaluator.evaluateAsObject();
    }

    protected long evaluateLong(final String text) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateLong();
    }

    protected long evaluateLong(final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateLong();
    }

    protected long evaluateLong(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateLong(ctx);
    }

    protected long evaluateLong(final String text, final Map<String, ExprConstant> constants, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateLong(ctx);
    }

    protected long evaluateLongOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text);
        return evaluator.evaluateLong();
    }

    protected long evaluateLongOptimized(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text, constants);
        return evaluator.evaluateLong();
    }

    protected double evaluateDouble(final String text) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDouble(final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDouble(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateDouble(ctx);
    }

    protected double evaluateDouble(final String text, final Map<String, ExprConstant> constants, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateDouble(ctx);
    }

    protected double evaluateDoubleOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDoubleOptimized(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text, constants);
        return evaluator.evaluateDouble();
    }

    protected double evaluateNumber(final String text) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateNumber();
    }

    protected double evaluateNumber(final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateNumber();
    }

    protected double evaluateNumber(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateNumber(ctx);
    }

    protected double evaluateNumber(final String text, final Map<String, ExprConstant> constants, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateNumber(ctx);
    }

    protected double evaluateNumberOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text);
        return evaluator.evaluateNumber();
    }

    protected double evaluateNumberOptimized(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text, constants);
        return evaluator.evaluateNumber();
    }

    protected boolean evaluateBool(final String text) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBool(final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBool(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateBool(ctx);
    }

    protected boolean evaluateBool(final String text, final Map<String, ExprConstant> constants, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateBool(ctx);
    }

    protected boolean evaluateBoolOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBoolOptimized(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text, constants);
        return evaluator.evaluateBool();
    }

    protected String evaluateString(final String text) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateString();
    }

    protected String evaluateString(final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateString();
    }

    protected String evaluateString(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateString(ctx);
    }

    protected String evaluateString(final String text, final Map<String, ExprConstant> constants, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateString(ctx);
    }

    protected String evaluateStringOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text);
        return evaluator.evaluateString();
    }

    protected String evaluateStringOptimized(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text, constants);
        return evaluator.evaluateString();
    }

    protected ByteBuffer evaluateByteBuffer(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text);
        return evaluator.evaluateByteBuffer(ctx);
    }

    protected ByteBuffer evaluateByteBuffer(final String text, final Map<String, ExprConstant> constants, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = createEvaluator(text, constants);
        return evaluator.evaluateByteBuffer(ctx);
    }

    protected ByteBuffer evaluateByteBufferOptimized(final ExprContext ctx, final String text) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text);
        return evaluator.evaluateByteBuffer();
    }

    protected ByteBuffer evaluateByteBufferOptimized(final ExprContext ctx, final String text, final Map<String, ExprConstant> constants) {
        final ExprEvaluator evaluator = createOptimizedEvaluator(ctx, text, constants);
        return evaluator.evaluateByteBuffer();
    }

    // The following ASTRoot-based helpers are used only by ExprPrinterTest, which tests the
    // Expr-tree printer directly and has no flat-AST equivalent - deliberately not hook-ified.
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
