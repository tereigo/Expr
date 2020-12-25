package com.tereigo.atlas_expr.algo.customization;

import com.google.common.collect.Lists;
import com.tereigo.atlas_expr.atlas.AddRuleMsg;
import com.tereigo.atlas_expr.atlas.AlgoDataProvider;
import com.tereigo.atlas_expr.atlas.AlgoExprContextEnricher;
import com.tereigo.atlas_expr.atlas.AtlasDataProvider;
import com.tereigo.atlas_expr.atlas.AtlasExprContextEnricher;
import com.tereigo.atlas_expr.atlas.CustomizationAction;
import com.tereigo.atlas_expr.atlas.Order;
import com.tereigo.atlas_expr.atlas.utils.EngineTimeProvider;
import com.tereigo.atlas_expr.atlas.utils.RandomDoubleProvider;
import com.tereigo.atlas_expr.atlas.utils.ReferenceDataCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CustomizationEngineTest {
    @Mock
    EngineTimeProvider timeProvider;
    @Mock
    RandomDoubleProvider randomProvider;
    @Mock
    ReferenceDataCache refData;
    @Mock
    AlgoDataProvider algo;

    CustomizationEngine engine;
    TestCustomizationErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(refData.getTuidByClientId(1)).thenReturn(constant("CLIENT1"));
        when(refData.getTuidByClientId(2)).thenReturn(constant("CLIENT2"));
        when(refData.getTuidByClientId(3)).thenReturn(null);

        when(refData.getRicByProductId(123)).thenReturn(constant("VOD.L"));
        when(refData.getRicByProductId(124)).thenReturn(constant("BT.L"));
        when(refData.getRicByProductId(125)).thenReturn(constant("TSCO.L"));

        AtlasDataProvider atlas = new AtlasDataProvider(timeProvider, randomProvider, refData, "ATLAS_ALGO_NODE1");

        engine = new CustomizationEngine();
        errorHandler = new TestCustomizationErrorHandler();
        AtlasExprContextEnricher atlasEnricher = new AtlasExprContextEnricher(atlas);
        AlgoExprContextEnricher algoEnricher = new AlgoExprContextEnricher(algo);
        engine.init(refData, atlasEnricher, algoEnricher, errorHandler);
    }

    @Test
    void testFromAxisNode() {
        when(algo.getAlgoType()).thenReturn("AXIS");

        initRules();
        assertEquals(13, engine.getRulesCount());

        Order order1 = new Order(123, 1);
        engine.onNewOrder(order1);
        assertEquals("action1,action5,action6", order1.actions());

        Order order2 = new Order(124, 1);
        engine.onNewOrder(order2);
        assertEquals("action1,action7", order2.actions());

        Order order3 = new Order(125, 1);
        engine.onNewOrder(order3);
        assertEquals("action1,action6", order3.actions());

        Order order4 = new Order(123, 2);
        engine.onNewOrder(order4);
        assertEquals("action2,action3,action8,action9", order4.actions());

        Order order5 = new Order(124, 2);
        engine.onNewOrder(order5);
        assertEquals("action2,action3,action10", order5.actions());

        Order order6 = new Order(125, 2);
        engine.onNewOrder(order6);
        assertEquals("action2,action3,action9", order6.actions());

        // no actions for CLIENT3 because refData returns "null" for getTuidByClientId(3)
        Order order7 = new Order(123, 3);
        engine.onNewOrder(order7);
        assertEquals("", order7.actions());

        Order order8 = new Order(124, 3);
        engine.onNewOrder(order8);
        assertEquals("", order8.actions());

        Order order9 = new Order(125, 3);
        engine.onNewOrder(order9);
        assertEquals("", order9.actions());
    }

    @Test
    void testFromMicrotraderNode() {
        when(algo.getAlgoType()).thenReturn("MICROTRADER");

        initRules();
        assertEquals(15, engine.getRulesCount());

        Order order1 = new Order(123, 1);
        engine.onNewOrder(order1);
        assertEquals("action1,action15,action16,action17", order1.actions());

        Order order2 = new Order(124, 1);
        engine.onNewOrder(order2);
        assertEquals("action1,action15,action18", order2.actions());

        Order order3 = new Order(125, 1);
        engine.onNewOrder(order3);
        assertEquals("action1,action15,action17", order3.actions());

        Order order4 = new Order(123, 2);
        engine.onNewOrder(order4);
        assertEquals("action2,action4,action19,action20,action21", order4.actions());

        Order order5 = new Order(124, 2);
        engine.onNewOrder(order5);
        assertEquals("action2,action4,action19,action22", order5.actions());

        Order order6 = new Order(125, 2);
        engine.onNewOrder(order6);
        assertEquals("action2,action4,action19,action21", order6.actions());

        // no actions for CLIENT3 because refData returns "null" for getTuidByClientId(3)
        Order order7 = new Order(123, 3);
        engine.onNewOrder(order7);
        assertEquals("", order7.actions());

        Order order8 = new Order(124, 3);
        engine.onNewOrder(order8);
        assertEquals("", order8.actions());

        Order order9 = new Order(125, 3);
        engine.onNewOrder(order9);
        assertEquals("", order9.actions());
    }

    @Test
    void testMalformedNodePredicates() {
        when(algo.getAlgoType()).thenReturn("AXIS");

        engine.onAddRuleMsg(new AddRuleMsg("tru", "$tuid== \"CLIENT1\"", true, createAction("action1")));
        assertEquals(1, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("true and", "$tuid== \"CLIENT2\"", true, createAction("action2")));
        assertEquals(2, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS", "$tuid== \"CLIENT2\"", true, createAction("action3")));
        assertEquals(3, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType=\"MICROTRADER\"", "$tuid== \"CLIENT2\"", true, createAction("action4")));
        assertEquals(4, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodType==\"MICROTRADER\"", "$tuid== \"CLIENT2\"", true, createAction("action5")));
        assertEquals(5, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT1\"", true, createAction("action15")));
        assertEquals(5, errorHandler.errors.size());

        assertEquals(0, engine.getRulesCount());

        when(algo.getAlgoType()).thenReturn("MICROTRADER");

        engine.onAddRuleMsg(new AddRuleMsg("tru", "$tuid== \"CLIENT1\"", true, createAction("action1")));
        assertEquals(6, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("true and", "$tuid== \"CLIENT2\"", true, createAction("action2")));
        assertEquals(7, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS", "$tuid== \"CLIENT2\"", true, createAction("action3")));
        assertEquals(8, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType=\"MICROTRADER\"", "$tuid== \"CLIENT2\"", true, createAction("action4")));
        assertEquals(9, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodType==\"MICROTRADER\"", "$tuid== \"CLIENT2\"", true, createAction("action5")));
        assertEquals(10, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT1\"", true, createAction("action15")));
        assertEquals(10, errorHandler.errors.size());

        assertEquals(1, engine.getRulesCount());
    }

    @Test
    void testMalformedRulePredicates() {
        when(algo.getAlgoType()).thenReturn("AXIS");

        initRules();
        assertEquals(13, engine.getRulesCount());
        assertEquals(0, errorHandler.errors.size());

        // malformed rules
        engine.onAddRuleMsg(new AddRuleMsg("true", "$tuid= \"CLIENT2\"", true, createAction("action30")));
        assertEquals(13, engine.getRulesCount());
        assertEquals(1, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("true", "$tuid==\"CLIENT2", true, createAction("action31")));
        assertEquals(13, engine.getRulesCount());
        assertEquals(2, errorHandler.errors.size());
        // wrong identifier but it will be known at evaluation only
        engine.onAddRuleMsg(new AddRuleMsg("true", "$tud==\"CLIENT1\"", true, createAction("action32")));
        assertEquals(14, engine.getRulesCount());
        assertEquals(2, errorHandler.errors.size());
        // "not" applies to non-boolean
        engine.onAddRuleMsg(new AddRuleMsg("true", "not $ric==\"BT.L\"", true, createAction("action33")));
        assertEquals(15, engine.getRulesCount());
        assertEquals(2, errorHandler.errors.size());

        Order order1 = new Order(123, 1);
        engine.onNewOrder(order1);
        assertEquals("action1,action5,action6", order1.actions());
        assertEquals(4, errorHandler.errors.size());

        Order order2 = new Order(124, 1);
        engine.onNewOrder(order2);
        assertEquals("action1,action7", order2.actions());
        assertEquals(6, errorHandler.errors.size());
    }

    private void initRules() {
        engine.onAddRuleMsg(new AddRuleMsg("true", "$tuid== \"CLIENT1\"", true, createAction("action1")));
        engine.onAddRuleMsg(new AddRuleMsg("true", "$tuid== \"CLIENT2\"", true, createAction("action2")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT2\"", true, createAction("action3")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT2\"", true, createAction("action4")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT1\" and $ric==\"VOD.L\"", true, createAction("action5")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT1\" and not ($ric==\"BT.L\")", true, createAction("action6")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT1\" and $ric==\"BT.L\"", true, createAction("action7")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT2\" and $ric==\"VOD.L\"", true, createAction("action8")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT2\" and not ($ric==\"BT.L\")", true, createAction("action9")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT2\" and $ric==\"BT.L\"", true, createAction("action10")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT3\" and $ric==\"BT.L\"", true, createAction("action11")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT3\" and $ric==\"VOD.L\"", true, createAction("action12")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT3\" and not ($ric==\"BT.L\")", true, createAction("action13")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"AXIS\"", "$tuid== \"CLIENT3\" and $ric==\"TSCO.L\"", true, createAction("action14")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT1\"", true, createAction("action15")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT1\" and $ric==\"VOD.L\"", true, createAction("action16")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT1\" and not ($ric==\"BT.L\")", true, createAction("action17")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT1\" and $ric==\"BT.L\"", true, createAction("action18")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT2\"", true, createAction("action19")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT2\" and $ric==\"VOD.L\"", true, createAction("action20")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT2\" and not ($ric==\"BT.L\")", true, createAction("action21")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT2\" and $ric==\"BT.L\"", true, createAction("action22")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT3\"", true, createAction("action23")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT3\" and $ric==\"VOD.L\"", true, createAction("action24")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT3\" and not ($ric==\"BT.L\")", true, createAction("action25")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"MICROTRADER\"", "$tuid== \"CLIENT3\" and $ric==\"BT.L\"", true, createAction("action26")));
    }

    private void initRules2() {
        engine.onAddRuleMsg(new AddRuleMsg("true", "order.tuid== \"CLIENT1\"", true, createAction("action1")));
        engine.onAddRuleMsg(new AddRuleMsg("true", "order.tuid== \"CLIENT2\"", true, createAction("action2")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT2\"", true, createAction("action3")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT2\"", true, createAction("action4")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT1\" and order.ric==\"VOD.L\"", true, createAction("action5")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT1\" and not order.ric==\"BT.L\"", true, createAction("action6")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT1\" and order.ric==\"BT.L\"", true, createAction("action7")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT2\" and order.ric==\"VOD.L\"", true, createAction("action8")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT2\" and not order.ric==\"BT.L\"", true, createAction("action9")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT2\" and order.ric==\"BT.L\"", true, createAction("action10")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT3\" and order.ric==\"BT.L\"", true, createAction("action11")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT3\" and order.ric==\"VOD.L\"", true, createAction("action12")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT3\" and not order.ric==\"BT.L\"", true, createAction("action13")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"AXIS\"", "order.tuid== \"CLIENT3\" and order.ric==\"TSCO.L\"", true, createAction("action14")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT1\"", true, createAction("action15")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT1\" and order.ric==\"VOD.L\"", true, createAction("action16")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT1\" and not order.ric==\"BT.L\"", true, createAction("action17")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT1\" and order.ric==\"BT.L\"", true, createAction("action18")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT2\"", true, createAction("action19")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT2\" and order.ric==\"VOD.L\"", true, createAction("action20")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT2\" and not order.ric==\"BT.L\"", true, createAction("action21")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT2\" and order.ric==\"BT.L\"", true, createAction("action22")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT3\"", true, createAction("action23")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT3\" and order.ric==\"VOD.L\"", true, createAction("action24")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT3\" and not order.ric==\"BT.L\"", true, createAction("action25")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"MICROTRADER\"", "order.tuid== \"CLIENT3\" and order.ric==\"BT.L\"", true, createAction("action26")));
    }

    static class TestCustomizationErrorHandler implements CustomizationErrorHandler {
        final List<String> errors = Lists.newArrayList();

        @Override
        public void onError(String context, String msg) {
            errors.add(context + ": " + msg);
        }
    }
    
    private TestCustomizationAction createAction(String name) {
        return new TestCustomizationAction(name);    
    }
    
    static class TestCustomizationAction implements CustomizationAction {
        final String name;
        
        TestCustomizationAction(String name) {
            this.name = name;
        }

//        @Override
//        public void apply(Order order) {
//            // ???
//        }

        @Override
        public String toString() {
            return name;
        }
    }
}