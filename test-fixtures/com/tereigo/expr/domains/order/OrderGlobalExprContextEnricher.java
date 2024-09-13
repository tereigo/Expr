package com.tereigo.expr.domains.order;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextEnricher;

/*
  Provides access to order functions
 */
public class OrderGlobalExprContextEnricher implements ExprContextEnricher {
    private final OrderFieldResolver orderFieldResolver;

    public OrderGlobalExprContextEnricher(final OrderFieldResolver orderFieldResolver) {
        this.orderFieldResolver = orderFieldResolver;
    }

    @Override
    public void enrich(final ExprContextBuilder ctx) {
        ctx.defineFunction("orderTuid", result -> result.accept(orderFieldResolver.tuid()));
        ctx.defineFunction("orderRic", result -> result.accept(orderFieldResolver.ric()));
        ctx.defineFunction("orderProductId", result -> result.accept(orderFieldResolver.productId()));
    }
}
