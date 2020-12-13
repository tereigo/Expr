package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

interface ExprContext {

  MutableVariant get(Token token, MutableVariant result);

  ExprContext EMPTY = (name, result) -> null;
}
