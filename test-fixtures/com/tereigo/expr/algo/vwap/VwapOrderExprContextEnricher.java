package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.domains.order.OrderFieldResolverImpl;
import com.tereigo.expr.falcon.msg.VwapOrderInstMsgRo;

public final class VwapOrderExprContextEnricher implements ExprContextEnricher {
    private final OrderFieldResolver orderResolver;

    public VwapOrderExprContextEnricher(OrderFieldResolver orderResolver) {
        this.orderResolver = orderResolver;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
        ctx.defineFunction("volumeLimit", result -> result.accept(toVwap(orderResolver).getVolumeLimit()));
    }

    private static VwapOrderInstMsgRo toVwap(OrderFieldResolver orderResolver) {
        return (VwapOrderInstMsgRo)((OrderFieldResolverImpl)orderResolver).getOrder();
    }
}
