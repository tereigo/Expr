package com.tereigo.expr.function;

import com.tereigo.expr.ExprContext;

@FunctionalInterface
public interface ExprContextConsumer {

    void accept(ExprContext value);
}