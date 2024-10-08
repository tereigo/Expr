package com.tereigo.expr;

import java.nio.ByteBuffer;

public interface ExprEvaluatorCreator<Evaluator extends ExprEvaluator> {

    Evaluator create(final ByteBuffer source);

}
