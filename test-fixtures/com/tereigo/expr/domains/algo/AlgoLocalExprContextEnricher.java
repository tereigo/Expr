package com.tereigo.expr.domains.algo;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextEnricher;

/*
  Provides access to Algo functions
 */
public class AlgoLocalExprContextEnricher implements ExprContextEnricher {
    private final AlgoDataProvider algo;

    public AlgoLocalExprContextEnricher(final AlgoDataProvider algoDataProvider) {
        this.algo = algoDataProvider;
    }

    @Override
    public void enrich(final ExprContextBuilder ctx) {
        ctx.addString("nodeType", algo::getAlgoType);
    }

}
