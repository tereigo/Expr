package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

interface ExprContext {

  MutableVariant get(Token token, MutableVariant result);

  Object getFunction(Token token);

  ExprContext EMPTY = new ExprContext() {
    @Override
    public MutableVariant get(Token token, MutableVariant result) {
      return null;
    }

    @Override
    public Object getFunction(Token token) {
      return null;
    }
  };
}
