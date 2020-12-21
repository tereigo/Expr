package com.tereigo.atlas_expr;

/*
  It's a result of the expression compilation
  And this is the entry point for evaluation
  This class is needed just to record the original source expression to be able to dump it in errors
 */
final class ASTRoot {
    private final String source;
    private final Expr expr;

    public ASTRoot(final String source, final Expr expr) {
        this.source = source;
        this.expr = expr;
    }

    public String source() {
        return source;
    }

    public Expr expr() {
        return expr;
    }
}
