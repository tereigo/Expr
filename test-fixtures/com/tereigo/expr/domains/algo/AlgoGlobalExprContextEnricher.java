package com.tereigo.expr.domains.algo;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

/*
  Provides access to Algo functions
 */
public class AlgoGlobalExprContextEnricher implements ExprContextEnricher {
    private final AlgoDataProvider algoDataProvider;

    public AlgoGlobalExprContextEnricher(final AlgoDataProvider algoDataProvider) {
        this.algoDataProvider = algoDataProvider;
    }

    @Override
    public void enrich(final MutableExprContext ctx) {
        ctx.defineFunction("algoNodeType", result -> result.accept(algoDataProvider.getAlgoType()));
    }

}
