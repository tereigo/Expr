package com.tereigo.expr.domains.order;

import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;

/*
  Provides access to order functions
 */
public class OrderExprContextEnricher implements ExprContextEnricher {
    private final OrderFieldResolver orderFieldResolver;

    public OrderExprContextEnricher(final OrderFieldResolver orderFieldResolver) {
      this.orderFieldResolver = orderFieldResolver;
    }

    @Override
    public void enrich(MutableExprContext ctx) {
        ctx.defineFunction("orderTuid", result -> result.accept(orderFieldResolver.tuid()));
        ctx.defineFunction("orderRic", result -> result.accept(orderFieldResolver.ric()));
        ctx.defineFunction("orderProductId", result -> result.accept(orderFieldResolver.productId()));
    }
}
