package com.tereigo.expr;

import java.nio.ByteBuffer;

public interface ExprEvaluatorSupplier<T extends ExprEvaluator> {

    T getEvaluator(final ByteBuffer expr);

}
