package com.tereigo.expr.falcon;

import com.tereigo.expr.CustomExprContext;

/*
  Provides access to Algo functions
 */
public class AlgoExprContext extends CustomExprContext {
    private static AlgoExprContext INSTANCE;

    private AlgoExprContext(final AlgoDataProvider algo) {
        ctx.defineString("nodeType", algo::getAlgoType);
    }

    public static void init(final AlgoDataProvider algo) {
        INSTANCE = new AlgoExprContext(algo);
    }

    public static AlgoExprContext get() {
        return INSTANCE;
    }
}
