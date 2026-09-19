package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;

public final class ExprOptimizer {

    private ExprOptimizer() { }

    public static ASTRoot optimize(final ASTRoot root, final ExprContext ctx) {
        return new AstOptimizer(ctx).optimize(root);
    }
}
