package com.tereigo.expr.domains.order;

import com.tereigo.expr.MutableExprContext;

public interface OrderExprContextCreator {

    void enrich(OrderFieldResolver orderFieldResolver, MutableExprContext ctx);

    OrderExprContextCreator NO_OP = (orderFieldResolver, ctx) -> {};
}
