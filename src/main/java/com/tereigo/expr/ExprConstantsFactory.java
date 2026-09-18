package com.tereigo.expr;

import com.tereigo.expr.impl.ExprConstantsAccessor;

public final class ExprConstantsFactory {

    public static ExprConstantsBuilder create() {
        return ExprConstantsAccessor.create(PASS);
    }

    public static class Pass { }
    private static final Pass PASS = new Pass();

    private ExprConstantsFactory() { }
}
