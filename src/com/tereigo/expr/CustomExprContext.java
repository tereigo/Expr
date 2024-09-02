package com.tereigo.expr;

import com.tereigo.expr.variant.MutableVariant;

/*
  This is the base class for all custom ExprContexts
 */
public abstract class CustomExprContext implements ExprContext {
  protected final MutableExprContext ctx = ExprContextFactory.create();

  @Override
  public MutableVariant get(String name, MutableVariant result) {
    return ctx.get(name, result);
  }

  @Override
  public Object getFunction(String name) {
    return ctx.getFunction(name);
  }
}
