package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.ExprContext;
import com.tereigo.atlas_expr.ExprContextFactory;
import com.tereigo.atlas_expr.MutableExprContext;
import com.tereigo.atlas_expr.variant.MutableVariant;

/*
  Provides access to Atlas functions
 */
public class AtlasExprContext implements ExprContext {
  private final MutableExprContext ctx = ExprContextFactory.create();

  public AtlasExprContext(final AtlasDataProvider atlas) {
    ctx.defineFunction("engineTime", result -> result.accept(atlas.getEngineTime()));

    ctx.defineFunction("random", result -> result.accept(atlas.getNextRandom()));

    ctx.defineFunction("tuidByClientId", (result, clientId) ->
      result.accept(atlas.getTuidByClientId((int)clientId.getAsLong()))
    );

    ctx.defineFunction("nodeName", result -> result.accept(atlas.getNodeName()));
  }

  @Override
  public MutableVariant get(String name, MutableVariant result) {
    return ctx.get(name, result);
  }

  @Override
  public Object getFunction(String name) {
    return ctx.getFunction(name);
  }
}
