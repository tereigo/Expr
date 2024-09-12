package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.domains.order.OrderFieldResolverImpl;
import com.tereigo.expr.falcon.msg.VwapOrderInstMsgRo;

public final class VwapOrderExprContextEnricher implements ExprContextEnricher {
    private final OrderFieldResolver orderResolver;

    public VwapOrderExprContextEnricher(final OrderFieldResolver orderResolver) {
        this.orderResolver = orderResolver;
    }

    private static VwapOrderInstMsgRo toVwap(final OrderFieldResolver orderResolver) {
        return (VwapOrderInstMsgRo) ((OrderFieldResolverImpl) orderResolver).getOrder();
    }

    @Override
    public void enrich(final MutableExprContext ctx) {
        ctx.defineFunction("volumeLimit", result -> result.accept(toVwap(orderResolver).getVolumeLimit()));
    }
}
