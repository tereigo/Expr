package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.ExprContext;
import com.tereigo.atlas_expr.ExprContextFactory;
import com.tereigo.atlas_expr.MutableExprContext;
import com.tereigo.atlas_expr.variant.MutableVariant;

/*
  Provides access to Algo functions
 */
public class AlgoExprContext implements ExprContext {
    private final MutableExprContext ctx = ExprContextFactory.create();

    public AlgoExprContext(final AlgoDataProvider algo ) {
        ctx.defineFunction("nodeType", result -> result.accept(algo.getAlgoType()));
    }

    @Override
    public MutableVariant get(String name, MutableVariant result) {
        return ctx.get(name, result);
    }

    @Override
    public Object getFunction(String name) {
        return ctx.getFunction(name);
    }
}
