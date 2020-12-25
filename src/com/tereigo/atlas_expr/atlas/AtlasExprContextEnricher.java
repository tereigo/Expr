package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.ExprContextEnricher;
import com.tereigo.atlas_expr.MutableExprContext;

/*
  Provides access to Atlas functions
 */
public class AtlasExprContextEnricher implements ExprContextEnricher {
    private final AtlasDataProvider atlasDataProvider;

    public AtlasExprContextEnricher(AtlasDataProvider atlasDataProvider) {
        this.atlasDataProvider = atlasDataProvider;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
        ctx.defineFunction("atlasEngineTime", result -> result.accept(atlasDataProvider.getEngineTime()));

        ctx.defineFunction("atlasRandom", result -> result.accept(atlasDataProvider.getNextRandom()));

        ctx.defineFunction("atlasTuidByClientId", (result, clientId) -> result.accept(atlasDataProvider.getTuidByClientId((int)clientId.getAsLong())));

        ctx.defineFunction("atlasNodeName", result -> result.accept(atlasDataProvider.getNodeName()));
    }

}
