package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.ExprEvaluatorWithContext;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.FalconExprContextBuilder;
import com.tereigo.expr.domains.order.OrderDomain;
import com.tereigo.expr.domains.order.OrderFieldResolverImpl;
import com.tereigo.expr.falcon.utils.ReferenceDataCache;
import com.tereigo.expr.order.TestVwapOrder;
import com.tereigo.expr.utils.ByteBufferUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class VwapOrderExprContextTest {
    @Mock
    ReferenceDataCache refData;

    private OrderFieldResolverImpl orderFieldResolver;
    private ExprContext ctx;
    private final TestVwapOrder order1 = TestVwapOrder.create()
            .withProductId(123).withClientId(1).withVolumeLimit(0.1);
    private final TestVwapOrder order2 = TestVwapOrder.create()
            .withProductId(124).withClientId(2).withVolumeLimit(0.2);
    private final TestVwapOrder order3 = TestVwapOrder.create()
            .withProductId(124).withClientId(3).withVolumeLimit(0.2);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(refData.getTuidByClientId(1)).thenReturn(ByteBufferUtils.constant("CLIENT1"));
        when(refData.getTuidByClientId(2)).thenReturn(ByteBufferUtils.constant("CLIENT2"));

        when(refData.getRicByProductId(123)).thenReturn(ByteBufferUtils.constant("VOD.L"));
        when(refData.getRicByProductId(124)).thenReturn(ByteBufferUtils.constant("BP.L"));

        OrderDomain.init(refData);

        orderFieldResolver = new OrderFieldResolverImpl();
        final MutableExprContext mutCtx = FalconExprContextBuilder.start().orderWithShortcuts(orderFieldResolver).build();
        final VwapOrderExprContextCreator creator = new VwapOrderExprContextCreator();
        creator.enrich(orderFieldResolver, mutCtx);
        ctx = mutCtx.getAsExprContext();

        orderFieldResolver.setOrder(order1);
    }

    @Test
    void vwapDomainTests() {
        assertTrue(evaluateBool("vwap.volumeLimit == 0.1", ctx));
        assertEquals(0.1, evaluateDouble("vwap.volumeLimit", ctx));
        assertTrue(evaluateBool("'ABC'.contains('A') and vwap.volumeLimit == 0.1", ctx));

        assertTrue(evaluateBool("vwap.volumeLimit == 0.1 and vwap.ric == 'VOD.L' and order.ric == 'VOD.L' and ric == 'VOD.L' and vwap.tuid == 'CLIENT1' and order.tuid == 'CLIENT1' and tuid == 'CLIENT1'", ctx));

        orderFieldResolver.setOrder(order2);

        assertTrue(evaluateBool("vwap.volumeLimit == 0.2 and vwap.ric == 'BP.L' and order.ric == 'BP.L' and ric == 'BP.L' and vwap.tuid == 'CLIENT2' and order.tuid == 'CLIENT2' and tuid == 'CLIENT2'", ctx));
    }

    @Test
    void vwapDomainOptimizedTests() {
        assertTrue(evaluateBoolOptimized("vwap.volumeLimit == 0.1", ctx));
        assertEquals(0.1, evaluateDoubleOptimized("vwap.volumeLimit", ctx));
        assertTrue(evaluateBoolOptimized("'ABC'.contains('A') and vwap.volumeLimit == 0.1", ctx));

        assertTrue(evaluateBoolOptimized("vwap.volumeLimit == 0.1 and vwap.ric == 'VOD.L' and order.ric == 'VOD.L' and ric == 'VOD.L' and vwap.tuid == 'CLIENT1' and order.tuid == 'CLIENT1' and tuid == 'CLIENT1'", ctx));

        orderFieldResolver.setOrder(order2);

        assertTrue(evaluateBoolOptimized("vwap.volumeLimit == 0.2 and vwap.ric == 'BP.L' and order.ric == 'BP.L' and ric == 'BP.L' and vwap.tuid == 'CLIENT2' and order.tuid == 'CLIENT2' and tuid == 'CLIENT2'", ctx));
    }

    private boolean evaluateBool(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateBool(ctx);
    }

    private boolean evaluateBoolOptimized(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        return evaluator.evaluateBool();
    }

    private double evaluateDouble(final String text, final ExprContext ctx) {
        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(text);
        return evaluator.evaluateDouble(ctx);
    }

    private double evaluateDoubleOptimized(final String text, final ExprContext ctx) {
        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, text);
        return evaluator.evaluateDouble();
    }
}