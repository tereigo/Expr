package com.tereigo.expr;

import com.tereigo.expr.annotations.GeneratesGarbage;

import java.nio.ByteBuffer;

public interface ExprEvaluatorWithContext extends ExprEvaluator {

    boolean evaluateBool(final ExprContext ctx);

    long evaluateLong(final ExprContext ctx);

    double evaluateDouble(final ExprContext ctx);

    String evaluateString(final ExprContext ctx);

    ByteBuffer evaluateByteBuffer(final ExprContext ctx);

    @GeneratesGarbage
    Object evaluateAsObject(final ExprContext ctx);

}
