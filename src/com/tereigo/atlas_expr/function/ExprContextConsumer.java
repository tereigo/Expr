package com.tereigo.atlas_expr.function;

import com.tereigo.atlas_expr.ExprContext;

@FunctionalInterface
public interface ExprContextConsumer {

    void accept(ExprContext value);
}