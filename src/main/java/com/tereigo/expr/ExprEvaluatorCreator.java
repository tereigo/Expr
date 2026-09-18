package com.tereigo.expr;

import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;

public interface ExprEvaluatorCreator<Evaluator extends ExprEvaluator> {

    Evaluator create(final ByteBuffer source);

    default Evaluator create(final String source) {
        return create(ByteBufferUtils.constant(source));
    }
}
