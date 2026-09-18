package com.tereigo.expr.impl;

import com.tereigo.expr.ExprConstantsBuilder;
import com.tereigo.expr.ExprConstantsFactory;

import java.util.Objects;

public final class ExprConstantsAccessor {

    private ExprConstantsAccessor() { }

    public static ExprConstantsBuilder create(final ExprConstantsFactory.Pass pass) {
        Objects.requireNonNull(pass);
        return new ExprConstantsBuilderImpl();
    }
}
