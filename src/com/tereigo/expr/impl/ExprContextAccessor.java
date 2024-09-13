package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextBuilderFactory;

public final class ExprContextAccessor {

    private ExprContextAccessor() { }

    public static ExprContextBuilder createEmpty(final ExprContextBuilderFactory.Pass ignoredPass) {
        return new ExprContextBuilderImpl();
    }

    public static ExprContextBuilder createNative(final ExprContextBuilderFactory.Pass ignoredPass) {
        return new ExprContextBuilderImpl(ExprContextNativeEnricher.get());
    }
}
