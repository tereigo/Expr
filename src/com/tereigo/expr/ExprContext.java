package com.tereigo.expr;

import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;

public interface ExprContext {

    /*
      Retrieve external value by name
      Value can be of any ExprType
      Returns "null" if not found
     */
    Variant get(final String name, final MutableVariant result);

    /*
      Retrieve external function by name
      Returns "null" if not found
     */
    Object getFunction(final String name);

}
