package com.tereigo.expr.domains.order;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.domains.falcon.FalconUtils;
import com.tereigo.expr.falcon.utils.ReferenceDataCache;

public final class OrderDomain {

    public static void init(final ReferenceDataCache refData) {
        FalconUtils.init(refData);
    }

    public static void defineShortcuts(final ExprContextBuilder ctx,
                                       final OrderFieldResolver orderFieldResolver) {
        ctx.addLong("productId", orderFieldResolver::productId);
        ctx.addByteBuffer("ric", orderFieldResolver::ric);
        ctx.addByteBuffer("tuid", orderFieldResolver::tuid);
    }
}
