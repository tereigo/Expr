package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextEnricher;

/*
  Provides access to Falcon functions in the global context
 */
public class FalconGlobalExprContextEnricher implements ExprContextEnricher {
    private final FalconDataProvider falconDataProvider;

    public FalconGlobalExprContextEnricher(final FalconDataProvider falconDataProvider) {
        this.falconDataProvider = falconDataProvider;
    }

    @Override
    public void enrich(final ExprContextBuilder ctx) {
        ctx.addFunction("falconEngineTimeMs", result -> result.accept(falconDataProvider.getEngineTimeMs()));

        ctx.addFunction("falconRandom", result -> result.accept(falconDataProvider.getNextRandom()));

        ctx.addFunction("falconTuidByClientId", (result, clientId) -> result.accept(falconDataProvider.getTuidByClientId((int) clientId.getAsLong())));

        ctx.addFunction("falconNodeName", result -> result.accept(falconDataProvider.getNodeName()));
    }

}
