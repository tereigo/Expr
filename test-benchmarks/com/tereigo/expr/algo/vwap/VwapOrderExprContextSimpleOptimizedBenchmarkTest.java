package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.FalconExprContextBuilder;
import com.tereigo.expr.domains.order.OrderDomain;
import com.tereigo.expr.domains.order.OrderFieldResolverImpl;
import com.tereigo.expr.falcon.utils.ReferenceDataCacheImpl;
import com.tereigo.expr.order.TestVwapOrder;
import com.tereigo.expr.utils.ByteBufferUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

// This class is just for debugging purposes
class VwapOrderExprContextSimpleOptimizedBenchmarkTest {

    ExprContext ctx;
    ExprEvaluator evaluator;

    @BeforeEach
    void setUp() {
        final ReferenceDataCacheImpl refData = new ReferenceDataCacheImpl();

        final OrderFieldResolverImpl orderFieldResolver;

        final TestVwapOrder order1 = TestVwapOrder.create()
                .withProductId(123).withClientId(1).withVolumeLimit(0.1);
        final TestVwapOrder order2 = TestVwapOrder.create()
                .withProductId(124).withClientId(2).withVolumeLimit(0.2);
        final TestVwapOrder order3 = TestVwapOrder.create()
                .withProductId(124).withClientId(3).withVolumeLimit(0.2);

        refData.addTuid(1, ByteBufferUtils.constant("CLIENT1"));
        refData.addTuid(2, ByteBufferUtils.constant("CLIENT2"));

        refData.addRic(123, ByteBufferUtils.constant("VOD.L"));
        refData.addRic(124, ByteBufferUtils.constant("BP.L"));

        OrderDomain.init(refData);

        orderFieldResolver = new OrderFieldResolverImpl();
        final MutableExprContext mutCtx = FalconExprContextBuilder.start().orderWithShortcuts(orderFieldResolver).build();
        final VwapOrderExprContextCreator creator = new VwapOrderExprContextCreator();
        creator.enrich(orderFieldResolver, mutCtx);

        ctx = mutCtx.getAsExprContext();

        orderFieldResolver.setOrder(order1);

        // 232000
//            evaluator = ExprEvaluatorFactory.create("true");
        // 14300
//            evaluator = ExprEvaluatorFactory.create(ctx, "vwap.volumeLimit == 0.1");
        // 13000
//            evaluator = ExprEvaluatorFactory.create(ctx, "(vwap.volumeLimit == 0.1)");
        // 16600
//            evaluator = ExprEvaluatorFactory.create(ctx, "ric in ['BT.L', 'VOD.L', 'TSCO.L']");
        // 15400
//            evaluator = ExprEvaluatorFactory.create(ctx, "(ric in ['BT.L', 'VOD.L', 'TSCO.L'])");
        // 7366 +- 262
//            evaluator = ExprEvaluatorFactory.create(ctx, "(vwap.volumeLimit == 0.1) and (ric in ['BT.L', 'VOD.L', 'TSCO.L'])");
        // 7152 +- 427
//            evaluator = ExprEvaluatorFactory.create(ctx, "vwap.volumeLimit == 0.1 and ric in ['BT.L', 'VOD.L', 'TSCO.L']");
        // 7184 / 7288 / 7746 / 7222 / 7414
//          evaluator = ExprEvaluatorFactory.create(ctx, "(vwap.volumeLimit == 0.1) and (vwap.ric == 'VOD.L') and (order.ric == 'VOD.L') and (ric == 'VOD.L') and (vwap.tuid == 'CLIENT1') and (order.tuid == 'CLIENT1') and (tuid == 'CLIENT1') and (ric in ['BT.L', 'VOD.L', 'TSCO.L'])");
        // optimized: 5066 / 4790 / 5367 / 5069 / 4957
        evaluator = ExprEvaluatorFactory.create(ctx, "(vwap.volumeLimit == 0.1) and (vwap.ric == 'VOD.L') and (order.ric == 'VOD.L') and (ric == 'VOD.L') and (vwap.tuid == 'CLIENT1') and (order.tuid == 'CLIENT1') and (tuid == 'CLIENT1') and (ric in ['BT.L', 'VOD.L', 'TSCO.L'])");
    }

    @Test
    public void benchmarkSimpleExpression() {
        final long start = System.nanoTime();
        for (int i = 0; i < 10_000_000; i++) {
            evaluator.evaluateBool();
        }
        final long end = System.nanoTime();
        System.out.println("Test took " + TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS) + " ms");
    }
}