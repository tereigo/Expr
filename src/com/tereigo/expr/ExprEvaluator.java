package com.tereigo.expr;

import com.tereigo.expr.annotations.GeneratesGarbage;

import java.nio.ByteBuffer;

public interface ExprEvaluator {

    boolean evaluateBool();

    long evaluateLong();

    double evaluateDouble();

    String evaluateString();

    ByteBuffer evaluateByteBuffer();

    @GeneratesGarbage
    Object evaluateAsObject();

}
