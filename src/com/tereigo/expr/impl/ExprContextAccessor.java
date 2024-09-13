package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextBuilderFactory;

import java.util.Objects;

public final class ExprContextAccessor {

    private ExprContextAccessor() { }

    public static ExprContextBuilder createEmpty(final ExprContextBuilderFactory.Pass pass) {
        Objects.requireNonNull(pass);
        return new ExprContextBuilderImpl();
    }

    public static ExprContextBuilder createNative(final ExprContextBuilderFactory.Pass pass) {
        Objects.requireNonNull(pass);
        return new ExprContextBuilderImpl(ExprContextNativeEnricher.get());
    }
}
