package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.MutableExprContext;

public final class ExprContextAccessor {

    private ExprContextAccessor() {
    }

    public static MutableExprContext createEmpty(final ExprContextFactory.Pass ignoredPass) {
        return new ExprContextImpl();
    }

    public static MutableExprContext createNative(final ExprContextFactory.Pass ignoredPass) {
        return new ExprContextImpl(ExprContextNativeEnricher.get());
    }
}
