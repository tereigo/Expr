package com.tereigo.atlas_expr;

/*
  Context storage for Expr native functions
 */
class ExprContextNative {

  static final MutableExprContext INSTANCE = ExprContextFactory.create();

  static {
    ExprContextNativeEnricher.INSTANCE.enrich(INSTANCE);
  }

}
