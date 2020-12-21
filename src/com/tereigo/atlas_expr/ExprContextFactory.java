package com.tereigo.atlas_expr;

public final class ExprContextFactory {

    private ExprContextFactory() { }

    public static MutableExprContext create() {
        return new ExprContextImpl();
    }
}
