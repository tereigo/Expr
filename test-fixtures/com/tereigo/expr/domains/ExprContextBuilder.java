package com.tereigo.expr.domains;

import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.algo.AlgoDataProvider;
import com.tereigo.expr.domains.algo.AlgoGlobalExprContextEnricher;
import com.tereigo.expr.domains.algo.AlgoLocalExprContextEnricher;
import com.tereigo.expr.domains.falcon.FalconDataProvider;
import com.tereigo.expr.domains.falcon.FalconDomain;
import com.tereigo.expr.domains.falcon.FalconGlobalExprContextEnricher;
import com.tereigo.expr.domains.falcon.FalconLocalExprContextEnricher;
import com.tereigo.expr.domains.order.OrderDomain;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.domains.order.OrderGlobalExprContextEnricher;
import com.tereigo.expr.domains.order.OrderLocalExprContextEnricher;

public class ExprContextBuilder {

    protected final MutableExprContext ctx;

    private ExprContextBuilder() {
        // include native context by default
        this.ctx = ExprContextFactory.createGlobalContext();
    }

    private ExprContextBuilder(final MutableExprContext ctx) {
        this.ctx = ctx;
    }

    public static ExprContextBuilder start() {
        return new ExprContextBuilder();
    }

    public static ExprContextBuilder start(final MutableExprContext ctx) {
        return new ExprContextBuilder(ctx);
    }

    public ExprContextBuilder falcon(final FalconDataProvider falcon) {
        ctx.enrich(new FalconGlobalExprContextEnricher(falcon));
        final MutableExprContext localFalconCtx = ExprContextFactory.createLocalContext(new FalconLocalExprContextEnricher(falcon));
        ctx.defineExprContext("falcon", localFalconCtx);
        FalconDomain.defineFunctions(ctx);
        return this;
    }

    public ExprContextBuilder algo(final AlgoDataProvider algo) {
        ctx.enrich(new AlgoGlobalExprContextEnricher(algo));
        final MutableExprContext localAlgoCtx = ExprContextFactory.createLocalContext(new AlgoLocalExprContextEnricher(algo));
        ctx.defineExprContext("algo", localAlgoCtx);
        return this;
    }

    public ExprContextBuilder order(final OrderFieldResolver orderFieldResolver) {
        ctx.enrich(new OrderGlobalExprContextEnricher(orderFieldResolver));
        final MutableExprContext localOrderCtx = ExprContextFactory.createLocalContext(new OrderLocalExprContextEnricher(orderFieldResolver));
        ctx.defineExprContext("order", localOrderCtx);
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
