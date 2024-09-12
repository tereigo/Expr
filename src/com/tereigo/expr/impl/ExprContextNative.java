package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextFactory;

/*
  Context storage for Expr native functions
 */
final class ExprContextNative {

  private static final ExprContext INSTANCE = ExprContextFactory.createNative();

  public static ExprContext get() {
      return INSTANCE;
  }

}
