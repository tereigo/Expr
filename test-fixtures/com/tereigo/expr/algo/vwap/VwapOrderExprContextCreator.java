package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.order.OrderExprContextCreator;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.domains.order.OrderLocalExprContextEnricher;

public class VwapOrderExprContextCreator implements OrderExprContextCreator {

    @Override
    public void enrich(final OrderFieldResolver orderFieldResolver, final MutableExprContext ctx) {
        final MutableExprContext vwapExprContext = ExprContextFactory.createLocalContext(new OrderLocalExprContextEnricher(orderFieldResolver),
                                                                                         new VwapOrderExprContextEnricher(orderFieldResolver));
        ctx.defineExprContext("vwap", vwapExprContext);
    }
}
