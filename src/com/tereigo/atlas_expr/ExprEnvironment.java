package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;

interface ExprEnvironment {

  MutableVariant get(Token name, MutableVariant result);

  ExprEnvironment EMPTY = (name, result) -> null;
}
