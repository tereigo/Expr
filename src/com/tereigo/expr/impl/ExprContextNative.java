package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;

/*
  Context storage for Expr native functions
 */
final class ExprContextNative {

  private static final ExprContext INSTANCE;

  public static ExprContext get() {
      return INSTANCE;
  }

  static {
      INSTANCE = new ExprContextImpl(ExprContextNativeEnricher.get());
  }
}
