package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.domains.order.OrderExprContext;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.domains.order.OrderFieldResolverImpl;
import com.tereigo.expr.falcon.msg.VwapOrderInstMsgRo;

public final class VwapOrderExprContext extends OrderExprContext {

    public VwapOrderExprContext(OrderFieldResolver orderResolver) {
        super(orderResolver);

        ctx.defineFunction("volumeLimit", result -> result.accept(toVwap(orderResolver).getVolumeLimit()));
    }

    private static VwapOrderInstMsgRo toVwap(OrderFieldResolver orderResolver) {
        return (VwapOrderInstMsgRo)((OrderFieldResolverImpl)orderResolver).getOrder();
    }
}
