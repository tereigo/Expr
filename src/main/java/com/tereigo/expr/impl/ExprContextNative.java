package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;

/*
  Context storage for Expr native functions
 */
public final class ExprContextNative {

    private static final ExprContext INSTANCE;

    static {
        INSTANCE = new ExprContextBuilderImpl(ExprContextNativeEnricher.get()).getAsExprContext();
    }

    private ExprContextNative() { }

    public static ExprContext get() {
        return INSTANCE;
    }
}
