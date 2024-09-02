package com.tereigo.expr.falcon;

import com.tereigo.expr.CustomExprContext;

public final class OrderExprContext extends CustomExprContext {

    public OrderExprContext(OrderFieldResolver orderResolver) {
        ctx.defineLong("productId", orderResolver::productId);
        ctx.defineByteBuffer("ric", orderResolver::ric);
        ctx.defineByteBuffer("tuid", orderResolver::tuid);
    }
}
