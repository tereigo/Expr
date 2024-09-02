package com.tereigo.expr;

/*
  Context storage for Expr native functions
 */
class ExprContextNative {

  private static final MutableExprContext INSTANCE = ExprContextFactory.createNative();

  public static MutableExprContext get() {
    return INSTANCE;
  }

}
