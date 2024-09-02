package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.ExprContextBuilder;
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
    private MutableExprContext ctx = ExprContextFactory.create();
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
        ctx = ExprContextBuilder.start().orderWithShortcuts(orderFieldResolver).build();
        VwapOrderExprContextCreator creator = new VwapOrderExprContextCreator();
        creator.enrich(orderFieldResolver, ctx);

        orderFieldResolver.setOrder(order1);
    }

    @Test
    void vwapDomainTests() {
        assertTrue(evaluateBool("vwap.volumeLimit == 0.1", ctx));
        assertEquals(0.1, evaluateDouble("vwap.volumeLimit", ctx));

        assertTrue(evaluateBool("vwap.volumeLimit == 0.1 and vwap.ric == 'VOD.L' and order.ric == 'VOD.L' and ric == 'VOD.L' and vwap.tuid == 'CLIENT1' and order.tuid == 'CLIENT1' and tuid == 'CLIENT1'", ctx));

        orderFieldResolver.setOrder(order2);

        assertTrue(evaluateBool("vwap.volumeLimit == 0.2 and vwap.ric == 'BP.L' and order.ric == 'BP.L' and ric == 'BP.L' and vwap.tuid == 'CLIENT2' and order.tuid == 'CLIENT2' and tuid == 'CLIENT2'", ctx));
    }

    private boolean evaluateBool(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateBool(ctx);
    }

    private double evaluateDouble(String text, ExprContext ctx) {
        ExprEvaluator evaluator = new ExprEvaluator(text);
        return evaluator.evaluateDouble(ctx);
    }
}