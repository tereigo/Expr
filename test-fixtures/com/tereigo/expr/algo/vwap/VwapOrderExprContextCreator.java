package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.order.OrderExprContextCreator;
import com.tereigo.expr.domains.order.OrderFieldResolver;

public class VwapOrderExprContextCreator implements OrderExprContextCreator {

    @Override
    public void enrich(OrderFieldResolver orderFieldResolver, MutableExprContext ctx) {
        final VwapOrderExprContext vwapExprContext = new VwapOrderExprContext(orderFieldResolver);
        ctx.defineExprContext("vwap", () -> vwapExprContext);
    }
}
