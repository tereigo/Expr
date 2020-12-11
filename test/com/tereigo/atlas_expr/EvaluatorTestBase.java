package com.tereigo.atlas_expr;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;

class EvaluatorTestBase {
    protected static final double EPS = 0.00001;

    protected ExprEnvironmentImpl createEnvironment() {
        final ExprEnvironmentImpl env = new ExprEnvironmentImpl();
        env.defineDouble("$PI", () -> 3.14);
        env.defineLong("$productId", () -> 123L);
        env.defineString("$ric", () -> "VOD.L");
        env.defineBool("$enabled", () -> true);
        env.defineByteBuffer("$tuid", () -> constant("CLIENT1"));
        return env;
    }

    protected void evaluate(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        evaluator.evaluateAsObject();
    }

    protected void evaluate(String text, ExprEnvironmentImpl env) {
        ExprEvaluator evaluator = new ExprEvaluator(text, env);
        evaluator.evaluateAsObject();
    }

    protected long evaluateLong(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateLong();
    }

    protected long evaluateLong(String text, ExprEnvironmentImpl env) {
        ExprEvaluator evaluator = new ExprEvaluator(text, env);
        return evaluator.evaluateLong();
    }

    protected double evaluateDouble(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateDouble();
    }

    protected double evaluateDouble(String text, ExprEnvironmentImpl env) {
        ExprEvaluator evaluator = new ExprEvaluator(text, env);
        return evaluator.evaluateDouble();
    }

    protected boolean evaluateBool(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateBool();
    }

    protected boolean evaluateBool(String text, ExprEnvironmentImpl env) {
        ExprEvaluator evaluator = new ExprEvaluator(text, env);
        return evaluator.evaluateBool();
    }

    protected String evaluateString(String text) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateString();
    }

    protected String evaluateString(String text, ExprEnvironmentImpl env) {
        ExprEvaluator evaluator = new ExprEvaluator(text, env);
        return evaluator.evaluateString();
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