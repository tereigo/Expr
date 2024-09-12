package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

/*
  Provides access to Falcon functions in the global context
 */
public class FalconGlobalExprContextEnricher implements ExprContextEnricher {
    private final FalconDataProvider falconDataProvider;

    public FalconGlobalExprContextEnricher(final FalconDataProvider falconDataProvider) {
        this.falconDataProvider = falconDataProvider;
    }

    @Override
    public void enrich(final MutableExprContext ctx) {
        ctx.defineFunction("falconEngineTimeMs", result -> result.accept(falconDataProvider.getEngineTimeMs()));

        ctx.defineFunction("falconRandom", result -> result.accept(falconDataProvider.getNextRandom()));

        ctx.defineFunction("falconTuidByClientId", (result, clientId) -> result.accept(falconDataProvider.getTuidByClientId((int) clientId.getAsLong())));

        ctx.defineFunction("falconNodeName", result -> result.accept(falconDataProvider.getNodeName()));
    }

}
