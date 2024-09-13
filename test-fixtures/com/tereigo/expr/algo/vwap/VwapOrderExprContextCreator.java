package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.domains.order.OrderExprContextCreator;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.domains.order.OrderLocalExprContextEnricher;

public class VwapOrderExprContextCreator implements OrderExprContextCreator {

    @Override
    public void enrich(final OrderFieldResolver orderFieldResolver, final ExprContextBuilder ctx) {
        final ExprContext vwapExprContext = ExprContextFactory.localContext(new OrderLocalExprContextEnricher(orderFieldResolver),
                                                                                   new VwapOrderExprContextEnricher(orderFieldResolver))
                                                                     .getAsExprContext();
        ctx.addExprContext("vwap", vwapExprContext);
    }
}
