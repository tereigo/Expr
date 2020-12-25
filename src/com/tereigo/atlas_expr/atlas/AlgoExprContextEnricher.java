package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.ExprContextEnricher;
import com.tereigo.atlas_expr.MutableExprContext;

/*
  Provides access to Algo functions
 */
public class AlgoExprContextEnricher implements ExprContextEnricher {

    private final AlgoDataProvider algoDataProvider;

    public AlgoExprContextEnricher(AlgoDataProvider algoDataProvider) {
        this.algoDataProvider = algoDataProvider;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
      ctx.defineFunction("algoNodeType", result -> result.accept(algoDataProvider.getAlgoType()));
    }

}
