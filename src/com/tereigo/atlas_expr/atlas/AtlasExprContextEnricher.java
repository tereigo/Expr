package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.ExprContextEnricher;
import com.tereigo.atlas_expr.MutableExprContext;

/*
  Provides access to Atlas functions
 */
public class AtlasExprContextEnricher implements ExprContextEnricher {
    private static AtlasExprContextEnricher INSTANCE;
    private final AtlasDataProvider atlasDataProvider;

    private AtlasExprContextEnricher(final AtlasDataProvider atlasDataProvider) {
        this.atlasDataProvider = atlasDataProvider;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
        ctx.defineFunction("atlasEngineTime", result -> result.accept(atlasDataProvider.getEngineTime()));

        ctx.defineFunction("atlasRandom", result -> result.accept(atlasDataProvider.getNextRandom()));

        ctx.defineFunction("atlasTuidByClientId", (result, clientId) -> result.accept(atlasDataProvider.getTuidByClientId((int)clientId.getAsLong())));

        ctx.defineFunction("atlasNodeName", result -> result.accept(atlasDataProvider.getNodeName()));
    }

    public static void init(final AtlasDataProvider atlas) {
        INSTANCE = new AtlasExprContextEnricher(atlas);
    }

    public static AtlasExprContextEnricher get() {
        return INSTANCE;
    }
}
