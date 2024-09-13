package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextEnricher;

/*
  Provides access to global functions
 */
public class FalconLocalExprContextEnricher implements ExprContextEnricher {
    private final FalconDataProvider falcon;

    public FalconLocalExprContextEnricher(final FalconDataProvider falcon) {
        this.falcon = falcon;
    }

    @Override
    public void enrich(final ExprContextBuilder ctx) {
        ctx.addLong("engineTimeMs", falcon::getEngineTimeMs);

        ctx.addDouble("random", falcon::getNextRandom);

        ctx.addFunction("tuidByClientId", (result, clientId) ->
                result.accept(falcon.getTuidByClientId((int) clientId.getAsLong()))
        );

        ctx.addString("nodeName", falcon::getNodeName);
    }
}
