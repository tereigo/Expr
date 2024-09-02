package com.tereigo.expr;

import com.tereigo.expr.variant.MutableVariant;

public interface ExprContext {

  /*
    Retrieve external value by name
    Value can be of any ExprType
    Returns "null" if not found
   */
  MutableVariant get(String name, MutableVariant result);

  /*
    Retrieve external function by name
    Returns "null" if not found
   */
  Object getFunction(String name);

}
