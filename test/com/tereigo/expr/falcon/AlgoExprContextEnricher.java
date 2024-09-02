package com.tereigo.expr.falcon;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

/*
  Provides access to Algo functions
 */
public class AlgoExprContextEnricher implements ExprContextEnricher {
    private static AlgoExprContextEnricher INSTANCE;

    private final AlgoDataProvider algoDataProvider;

    private AlgoExprContextEnricher(final AlgoDataProvider algoDataProvider) {
        this.algoDataProvider = algoDataProvider;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
      ctx.defineFunction("algoNodeType", result -> result.accept(algoDataProvider.getAlgoType()));
    }

    public static void init(final AlgoDataProvider algoDataProvider) {
        INSTANCE = new AlgoExprContextEnricher(algoDataProvider);
    }

    public static AlgoExprContextEnricher get() {
        return INSTANCE;
    }
}
