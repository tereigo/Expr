package com.tereigo.expr;

/*
  Context storage for Expr native functions
 */
class ExprContextNative {

  private static final ExprContext INSTANCE = ExprContextFactory.createNative();

  public static ExprContext get() {
      return INSTANCE;
  }

}
