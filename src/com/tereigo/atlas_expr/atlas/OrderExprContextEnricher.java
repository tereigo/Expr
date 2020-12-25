package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.ExprContextEnricher;
import com.tereigo.atlas_expr.MutableExprContext;
import com.tereigo.atlas_expr.atlas.utils.ReferenceDataCache;

/*
  Provides access to Atlas order functions
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
