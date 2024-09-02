package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

/*
  Provides access to global functions
 */
public class FalconExprContextEnricher implements ExprContextEnricher {
    private final FalconDataProvider falconDataProvider;

    public FalconExprContextEnricher(final FalconDataProvider falconDataProvider) {
        this.falconDataProvider = falconDataProvider;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
        ctx.defineFunction("falconEngineTime", result -> result.accept(falconDataProvider.getEngineTime()));

        ctx.defineFunction("falconRandom", result -> result.accept(falconDataProvider.getNextRandom()));

        ctx.defineFunction("falconTuidByClientId", (result, clientId) -> result.accept(falconDataProvider.getTuidByClientId((int)clientId.getAsLong())));

        ctx.defineFunction("falconNodeName", result -> result.accept(falconDataProvider.getNodeName()));
    }

}
