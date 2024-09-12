package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;

final class ExprOptimizer {

    private ExprOptimizer() { }

    static ASTRoot optimize(final ASTRoot root, final ExprContext ctx) {
        return new AstOptimizer(ctx).optimize(root);
    }
}
