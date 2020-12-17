package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

interface ExprContext {

  MutableVariant get(Token token, MutableVariant result);

  Object getFunction(Token token);

}
