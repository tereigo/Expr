package com.tereigo.expr.domains.order;

import com.tereigo.expr.ExprContextBuilder;

public interface OrderExprContextCreator {

    OrderExprContextCreator NO_OP = (orderFieldResolver, ctx) -> {
    };

    void enrich(OrderFieldResolver orderFieldResolver, ExprContextBuilder ctx);
}
