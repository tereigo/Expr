package com.tereigo.expr.domains.algo;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextEnricher;

/*
  Provides access to Algo functions
 */
public class AlgoGlobalExprContextEnricher implements ExprContextEnricher {
    private final AlgoDataProvider algoDataProvider;

    public AlgoGlobalExprContextEnricher(final AlgoDataProvider algoDataProvider) {
        this.algoDataProvider = algoDataProvider;
    }

    @Override
    public void enrich(final ExprContextBuilder ctx) {
        ctx.addFunction("algoNodeType", result -> result.accept(algoDataProvider.getAlgoType()));
    }

}
