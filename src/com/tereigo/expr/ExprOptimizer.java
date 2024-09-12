package com.tereigo.expr;

final class ExprOptimizer {

    private ExprOptimizer() { }

    static ASTRoot optimize(final ASTRoot root, final ExprContext ctx) {
        return new AstOptimizer(ctx).optimize(root);
    }
}
