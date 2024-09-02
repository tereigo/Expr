package com.tereigo.expr.domains.algo;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

/*
  Provides access to Algo functions
 */
public class AlgoExprContextEnricher implements ExprContextEnricher {
    private final AlgoDataProvider algoDataProvider;

    public AlgoExprContextEnricher(final AlgoDataProvider algoDataProvider) {
        this.algoDataProvider = algoDataProvider;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
      ctx.defineFunction("algoNodeType", result -> result.accept(algoDataProvider.getAlgoType()));
    }

}
