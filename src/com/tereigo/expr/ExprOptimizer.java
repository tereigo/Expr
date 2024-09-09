package com.tereigo.expr;

final class ExprOptimizer {

  private ExprOptimizer() { }

  static ASTRoot optimize(ASTRoot root, ExprContext ctx) {
    return new AstOptimizer(ctx).optimize(root);
  }
}
