package com.tereigo.expr.function;

import com.tereigo.expr.ExprContext;

@FunctionalInterface
public interface ExprContextSupplier {

    ExprContext getAsExprContext();

}
