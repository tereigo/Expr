package com.tereigo.expr.falcon;

import com.tereigo.expr.CustomExprContext;

/*
  Provides access to global functions
 */
public class FalconExprContext extends CustomExprContext {
  private static FalconExprContext INSTANCE;

  private FalconExprContext(final FalconDataProvider falcon) {
    ctx.defineLong("engineTime", falcon::getEngineTime);

    ctx.defineDouble("random", falcon::getNextRandom);

    ctx.defineFunction("tuidByClientId", (result, clientId) ->
      result.accept(falcon.getTuidByClientId((int)clientId.getAsLong()))
    );

    ctx.defineString("nodeName", falcon::getNodeName);
  }

  public static void init(final FalconDataProvider falcon) {
    INSTANCE = new FalconExprContext(falcon);
  }

  public static FalconExprContext get() {
    return INSTANCE;
  }
}
