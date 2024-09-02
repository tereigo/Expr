package com.tereigo.expr.domains.order;

import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.falcon.FalconUtils;
import com.tereigo.expr.falcon.utils.ReferenceDataCache;

public final class OrderDomain {

    public static void init(final ReferenceDataCache refData) {
        FalconUtils.init(refData);
    }

    public static void defineShortcuts(final MutableExprContext ctx,
                                       final OrderFieldResolver orderFieldResolver) {
        ctx.defineLong("productId", orderFieldResolver::productId);
        ctx.defineByteBuffer("ric", orderFieldResolver::ric);
        ctx.defineByteBuffer("tuid", orderFieldResolver::tuid);
    }
}
