package com.tereigo.expr.domains.order;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

public class OrderLocalExprContextEnricher implements ExprContextEnricher {
    private final OrderFieldResolver orderFieldResolver;

    public OrderLocalExprContextEnricher(final OrderFieldResolver orderFieldResolver) {
        this.orderFieldResolver = orderFieldResolver;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
        ctx.defineLong("productId", orderFieldResolver::productId);
        ctx.defineByteBuffer("ric", orderFieldResolver::ric);
        ctx.defineByteBuffer("tuid", orderFieldResolver::tuid);
    }
}
