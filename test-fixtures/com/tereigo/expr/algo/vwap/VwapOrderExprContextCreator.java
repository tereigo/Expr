package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextBuilderFactory;
import com.tereigo.expr.domains.order.OrderExprContextCreator;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.domains.order.OrderLocalExprContextEnricher;

public class VwapOrderExprContextCreator implements OrderExprContextCreator {

    @Override
    public void enrich(final OrderFieldResolver orderFieldResolver, final ExprContextBuilder ctx) {
        final ExprContextBuilder vwapExprContext = ExprContextBuilderFactory.localContext(new OrderLocalExprContextEnricher(orderFieldResolver),
                new VwapOrderExprContextEnricher(orderFieldResolver));
        ctx.defineExprContext("vwap", vwapExprContext);
    }
}
