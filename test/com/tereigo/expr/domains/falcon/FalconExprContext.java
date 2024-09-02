package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.CustomExprContext;

/*
  Provides access to global functions
 */
public class FalconExprContext extends CustomExprContext {

  public FalconExprContext(final FalconDataProvider falcon) {
    ctx.defineLong("engineTime", falcon::getEngineTime);

    ctx.defineDouble("random", falcon::getNextRandom);

    ctx.defineFunction("tuidByClientId", (result, clientId) ->
      result.accept(falcon.getTuidByClientId((int)clientId.getAsLong()))
    );

    ctx.defineString("nodeName", falcon::getNodeName);
  }

}
