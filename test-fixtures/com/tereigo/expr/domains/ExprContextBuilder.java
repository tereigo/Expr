package com.tereigo.expr.domains;

import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.algo.AlgoDataProvider;
import com.tereigo.expr.domains.algo.AlgoExprContext;
import com.tereigo.expr.domains.algo.AlgoExprContextEnricher;
import com.tereigo.expr.domains.falcon.FalconDataProvider;
import com.tereigo.expr.domains.falcon.FalconDomain;
import com.tereigo.expr.domains.falcon.FalconExprContext;
import com.tereigo.expr.domains.falcon.FalconExprContextEnricher;
import com.tereigo.expr.domains.order.OrderDomain;
import com.tereigo.expr.domains.order.OrderExprContext;
import com.tereigo.expr.domains.order.OrderExprContextEnricher;
import com.tereigo.expr.domains.order.OrderFieldResolver;

public class ExprContextBuilder {

    protected final MutableExprContext ctx;

    protected ExprContextBuilder() {
        // include native context by default
        this.ctx = ExprContextFactory.createNative();
    }

    protected ExprContextBuilder(final MutableExprContext ctx) {
        this.ctx = ctx;
    }

    public static ExprContextBuilder start() {
        return new ExprContextBuilder();
    }

    public static ExprContextBuilder start(final MutableExprContext ctx) {
        return new ExprContextBuilder(ctx);
    }

    public ExprContextBuilder falcon(final FalconDataProvider falcon) {
        ctx.enrich(new FalconExprContextEnricher(falcon));
        final FalconExprContext falconExprContext = new FalconExprContext(falcon);
        ctx.defineExprContext("falcon", () -> falconExprContext);
        FalconDomain.defineShortcuts(ctx);
        return this;
    }

    public ExprContextBuilder algo(final AlgoDataProvider algo) {
        ctx.enrich(new AlgoExprContextEnricher(algo));
        final AlgoExprContext algoExprContext = new AlgoExprContext(algo);
        ctx.defineExprContext("algo", () -> algoExprContext);
        return this;
    }

    public ExprContextBuilder order(final OrderFieldResolver orderFieldResolver) {
        ctx.enrich(new OrderExprContextEnricher(orderFieldResolver));
        final OrderExprContext orderExprContext = new OrderExprContext(orderFieldResolver);
        ctx.defineExprContext("order", () -> orderExprContext);
        return this;
    }

    public ExprContextBuilder orderWithShortcuts(final OrderFieldResolver orderFieldResolver) {
        order(orderFieldResolver);
        OrderDomain.defineShortcuts(ctx, orderFieldResolver);
        return this;
    }

    public MutableExprContext build() {
        return ctx;
    }

}
