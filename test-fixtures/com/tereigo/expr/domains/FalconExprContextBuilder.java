package com.tereigo.expr.domains;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextBuilderFactory;
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

public class FalconExprContextBuilder {

    protected final ExprContextBuilder ctx;

    private FalconExprContextBuilder() {
        // include native context by default
        this.ctx = ExprContextBuilderFactory.globalContext();
    }

    private FalconExprContextBuilder(final ExprContextBuilder ctx) {
        this.ctx = ctx;
    }

    public static FalconExprContextBuilder start() {
        return new FalconExprContextBuilder();
    }

    public static FalconExprContextBuilder start(final ExprContextBuilder ctx) {
        return new FalconExprContextBuilder(ctx);
    }

    public FalconExprContextBuilder falcon(final FalconDataProvider falcon) {
        ctx.enrich(new FalconGlobalExprContextEnricher(falcon));
        final ExprContextBuilder localFalconCtx = ExprContextBuilderFactory.localContext(new FalconLocalExprContextEnricher(falcon));
        ctx.defineExprContext("falcon", localFalconCtx);
        FalconDomain.defineFunctions(ctx);
        return this;
    }

    public FalconExprContextBuilder algo(final AlgoDataProvider algo) {
        ctx.enrich(new AlgoGlobalExprContextEnricher(algo));
        final ExprContextBuilder localAlgoCtx = ExprContextBuilderFactory.localContext(new AlgoLocalExprContextEnricher(algo));
        ctx.defineExprContext("algo", localAlgoCtx);
        return this;
    }

    public FalconExprContextBuilder order(final OrderFieldResolver orderFieldResolver) {
        ctx.enrich(new OrderGlobalExprContextEnricher(orderFieldResolver));
        final ExprContextBuilder localOrderCtx = ExprContextBuilderFactory.localContext(new OrderLocalExprContextEnricher(orderFieldResolver));
        ctx.defineExprContext("order", localOrderCtx);
        return this;
    }

    public FalconExprContextBuilder orderWithShortcuts(final OrderFieldResolver orderFieldResolver) {
        order(orderFieldResolver);
        OrderDomain.defineShortcuts(ctx, orderFieldResolver);
        return this;
    }

    public ExprContextBuilder build() {
        return ctx;
    }

}
