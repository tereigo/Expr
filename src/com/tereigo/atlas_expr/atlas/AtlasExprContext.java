package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.CustomExprContext;

/*
  Provides access to Atlas functions
 */
public class AtlasExprContext extends CustomExprContext {
  private static AtlasExprContext INSTANCE;

  private AtlasExprContext(final AtlasDataProvider atlas) {
    ctx.defineLong("engineTime", atlas::getEngineTime);

    ctx.defineDouble("random", atlas::getNextRandom);

    ctx.defineFunction("tuidByClientId", (result, clientId) ->
      result.accept(atlas.getTuidByClientId((int)clientId.getAsLong()))
    );

    ctx.defineString("nodeName", atlas::getNodeName);
  }

  public static void init(final AtlasDataProvider atlas) {
    INSTANCE = new AtlasExprContext(atlas);
  }

  public static AtlasExprContext get() {
    return INSTANCE;
  }
}
