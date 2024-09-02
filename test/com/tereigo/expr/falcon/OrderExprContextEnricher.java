package com.tereigo.expr.falcon;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.falcon.utils.ReferenceDataCache;

/*
  Provides access to order functions
 */
public class OrderExprContextEnricher implements ExprContextEnricher {
    private final ReferenceDataCache refData;
    private final OrderFieldResolver orderFieldResolver;

    private OrderExprContextEnricher(ReferenceDataCache refData) {
      this.refData = refData;
      this.orderFieldResolver = new OrderFieldResolver(refData);
    }

    @Override
    public void enrich(MutableExprContext ctx) {
        ctx.defineFunction("orderClientIdToTuid", (result, clientId) -> result.accept(refData.getTuidByClientId((int)clientId.getAsLong())));
        ctx.defineFunction("orderTuid", result -> result.accept(orderFieldResolver.tuid()));
    }
}
