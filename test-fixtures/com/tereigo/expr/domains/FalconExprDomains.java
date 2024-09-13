package com.tereigo.expr.domains;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.domains.algo.AlgoDataProvider;
import com.tereigo.expr.domains.falcon.FalconDataProvider;

public final class FalconExprDomains {

    private FalconExprDomains() {
    }

    public static ExprContextBuilder falcon(final FalconDataProvider falcon) {
        return FalconExprContextBuilder.start().falcon(falcon).build();
    }

    public static ExprContextBuilder algo(final AlgoDataProvider algo) {
        return FalconExprContextBuilder.start().algo(algo).build();
    }

    public static ExprContextBuilder falconAlgo(final FalconDataProvider falcon, final AlgoDataProvider algo) {
        return FalconExprContextBuilder.start().falcon(falcon).algo(algo).build();
    }
}
