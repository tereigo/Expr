package com.tereigo.atlas_expr.function;

import com.tereigo.atlas_expr.ExprContext;

@FunctionalInterface
public interface ExprContextSupplier {

    ExprContext getAsExprContext();

}
