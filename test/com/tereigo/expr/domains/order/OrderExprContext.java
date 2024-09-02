package com.tereigo.expr.domains.order;

import com.tereigo.expr.CustomExprContext;

public class OrderExprContext extends CustomExprContext {

    public OrderExprContext(OrderFieldResolver orderResolver) {
        ctx.defineLong("productId", orderResolver::productId);
        ctx.defineByteBuffer("ric", orderResolver::ric);
        ctx.defineByteBuffer("tuid", orderResolver::tuid);
    }
}
