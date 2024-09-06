package com.tereigo.expr.domains.algo;

import com.tereigo.expr.CustomExprContext;

/*
  Provides access to Algo functions
 */
// TODO: remove
public class AlgoExprContext extends CustomExprContext {

    public AlgoExprContext(final AlgoDataProvider algo) {
        ctx.defineString("nodeType", algo::getAlgoType);
    }

}
