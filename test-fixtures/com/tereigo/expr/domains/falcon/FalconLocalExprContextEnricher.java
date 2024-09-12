package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

/*
  Provides access to global functions
 */
public class FalconLocalExprContextEnricher implements ExprContextEnricher {
    private final FalconDataProvider falcon;

    public FalconLocalExprContextEnricher(final FalconDataProvider falcon) {
        this.falcon = falcon;
    }

    @Override
    public void enrich(final MutableExprContext ctx) {
        ctx.defineLong("engineTimeMs", falcon::getEngineTimeMs);

        ctx.defineDouble("random", falcon::getNextRandom);

        ctx.defineFunction("tuidByClientId", (result, clientId) ->
                result.accept(falcon.getTuidByClientId((int) clientId.getAsLong()))
        );

        ctx.defineString("nodeName", falcon::getNodeName);
    }
}
