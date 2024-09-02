package com.tereigo.expr.algo.customization;

import com.tereigo.expr.domains.algo.AlgoDataProvider;
import com.tereigo.expr.domains.falcon.FalconDataProvider;
import com.tereigo.expr.domains.order.OrderExprContextCreator;
import com.tereigo.expr.falcon.utils.EngineTimeProvider;
import com.tereigo.expr.falcon.utils.RandomDoubleProvider;
import com.tereigo.expr.falcon.utils.ReferenceDataCache;
import com.tereigo.expr.order.TestOrderFieldSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
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
    TestCustomizationReportHandler reportHandler;
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

        FalconDataProvider falcon = new FalconDataProvider(timeProvider, randomProvider, refData, "FALCON_ALGO_NODE1");

        reportHandler = new TestCustomizationReportHandler();
        errorHandler = new TestCustomizationErrorHandler();

        engine = new CustomizationEngine(falcon, algo, OrderExprContextCreator.NO_OP, reportHandler, errorHandler);
    }

    @Test
    void testFromVWAPNode() {
        when(algo.getAlgoType()).thenReturn("VWAP");

        initRules();
        assertEquals(14, engine.getRulesCount());

        vwapNodeTests();
    }

    @Test
    void testFromVWAPNodeUsingObjects() {
        when(algo.getAlgoType()).thenReturn("VWAP");

        initRulesUsingObjects();
        assertEquals(14, engine.getRulesCount());

        vwapNodeTests();
    }

    private void vwapNodeTests() {
        TestOrderFieldSupplier order1 = new TestOrderFieldSupplier(123, 1);
        engine.onNewOrder(order1);
        assertEquals("action1,action5,action6", order1.actions());
        assertEquals("action1,action5,action6", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order2 = new TestOrderFieldSupplier(124, 1);
        engine.onNewOrder(order2);
        assertEquals("action1,action7", order2.actions());
        assertEquals("action1,action7", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order3 = new TestOrderFieldSupplier(125, 1);
        engine.onNewOrder(order3);
        assertEquals("action1,action6", order3.actions());
        assertEquals("action1,action6", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order4 = new TestOrderFieldSupplier(123, 2);
        engine.onNewOrder(order4);
        assertEquals("action2,action3,action8,action9", order4.actions());
        assertEquals("action2,action3,action8,action9", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order5 = new TestOrderFieldSupplier(124, 2);
        engine.onNewOrder(order5);
        assertEquals("action2,action3,action10", order5.actions());
        assertEquals("action2,action3,action10", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order6 = new TestOrderFieldSupplier(125, 2);
        engine.onNewOrder(order6);
        assertEquals("action2,action3,action9", order6.actions());
        assertEquals("action2,action3,action9", reportHandler.reportAppliedRules);

        // no actions for CLIENT3 because refData returns "null" for getTuidByClientId(3)
        TestOrderFieldSupplier order7 = new TestOrderFieldSupplier(123, 3);
        engine.onNewOrder(order7);
        assertEquals("", order7.actions());
        assertEquals("", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order8 = new TestOrderFieldSupplier(124, 3);
        engine.onNewOrder(order8);
        assertEquals("", order8.actions());
        assertEquals("", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order9 = new TestOrderFieldSupplier(125, 3);
        engine.onNewOrder(order9);
        assertEquals("", order9.actions());
        assertEquals("", reportHandler.reportAppliedRules);
    }

    @Test
    void testFromPOVNode() {
        when(algo.getAlgoType()).thenReturn("POV");

        initRules();
        assertEquals(16, engine.getRulesCount());

        povNodeTests();
    }

    @Test
    void testFromPOVNodeUsingObjects() {
        when(algo.getAlgoType()).thenReturn("POV");

        initRulesUsingObjects();
        assertEquals(16, engine.getRulesCount());

        povNodeTests();
    }

    void povNodeTests() {
        TestOrderFieldSupplier order1 = new TestOrderFieldSupplier(123, 1);
        engine.onNewOrder(order1);
        assertEquals("action1,action15,action16,action17", order1.actions());
        assertEquals("action1,action15,action16,action17", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order2 = new TestOrderFieldSupplier(124, 1);
        engine.onNewOrder(order2);
        assertEquals("action1,action15,action18", order2.actions());
        assertEquals("action1,action15,action18", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order3 = new TestOrderFieldSupplier(125, 1);
        engine.onNewOrder(order3);
        assertEquals("action1,action15,action17", order3.actions());
        assertEquals("action1,action15,action17", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order4 = new TestOrderFieldSupplier(123, 2);
        engine.onNewOrder(order4);
        assertEquals("action2,action4,action19,action20,action21", order4.actions());
        assertEquals("action2,action4,action19,action20,action21", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order5 = new TestOrderFieldSupplier(124, 2);
        engine.onNewOrder(order5);
        assertEquals("action2,action4,action19,action22", order5.actions());
        assertEquals("action2,action4,action19,action22", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order6 = new TestOrderFieldSupplier(125, 2);
        engine.onNewOrder(order6);
        assertEquals("action2,action4,action19,action21", order6.actions());
        assertEquals("action2,action4,action19,action21", reportHandler.reportAppliedRules);

        // no actions for CLIENT3 because refData returns "null" for getTuidByClientId(3)
        TestOrderFieldSupplier order7 = new TestOrderFieldSupplier(123, 3);
        engine.onNewOrder(order7);
        assertEquals("", order7.actions());
        assertEquals("", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order8 = new TestOrderFieldSupplier(124, 3);
        engine.onNewOrder(order8);
        assertEquals("", order8.actions());
        assertEquals("", reportHandler.reportAppliedRules);

        TestOrderFieldSupplier order9 = new TestOrderFieldSupplier(125, 3);
        engine.onNewOrder(order9);
        assertEquals("", order9.actions());
        assertEquals("", reportHandler.reportAppliedRules);
    }

    @Test
    void testMalformedNodePredicates() {
        when(algo.getAlgoType()).thenReturn("VWAP");

        engine.onAddRuleMsg(new AddRuleMsg("tru", "tuid== \"CLIENT1\"", true, createAction("action1")));
        assertEquals(1, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("true and", "tuid== \"CLIENT2\"", true, createAction("action2")));
        assertEquals(2, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP", "tuid== \"CLIENT2\"", true, createAction("action3")));
        assertEquals(3, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType=\"POV\"", "tuid== \"CLIENT2\"", true, createAction("action4")));
        assertEquals(4, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodType==\"POV\"", "tuid== \"CLIENT2\"", true, createAction("action5")));
        assertEquals(5, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT1\"", true, createAction("action15")));
        assertEquals(5, errorHandler.errors.size());

        assertEquals(0, engine.getRulesCount());

        when(algo.getAlgoType()).thenReturn("POV");

        engine.onAddRuleMsg(new AddRuleMsg("tru", "tuid== \"CLIENT1\"", true, createAction("action1")));
        assertEquals(6, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("true and", "tuid== \"CLIENT2\"", true, createAction("action2")));
        assertEquals(7, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP", "tuid== \"CLIENT2\"", true, createAction("action3")));
        assertEquals(8, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType=\"POV\"", "tuid== \"CLIENT2\"", true, createAction("action4")));
        assertEquals(9, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodType==\"POV\"", "tuid== \"CLIENT2\"", true, createAction("action5")));
        assertEquals(10, errorHandler.errors.size());
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT1\"", true, createAction("action15")));
        assertEquals(10, errorHandler.errors.size());

        assertEquals(1, engine.getRulesCount());
    }

    @Test
    void testMalformedRulePredicates() {
        when(algo.getAlgoType()).thenReturn("VWAP");

        initRules();
        assertEquals(14, engine.getRulesCount());
        assertEquals(0, errorHandler.errors.size());

        // malformed rules
        // parsing error
        engine.onAddRuleMsg(new AddRuleMsg("true", "tuid= \"CLIENT2\"", true, createAction("action30")));
        assertEquals(14, engine.getRulesCount());
        assertEquals(1, errorHandler.errors.size());
        // parsing error
        engine.onAddRuleMsg(new AddRuleMsg("true", "tuid==\"CLIENT2", true, createAction("action31")));
        assertEquals(14, engine.getRulesCount());
        assertEquals(2, errorHandler.errors.size());
        // "not" applies to non-boolean
        // parsing error
        engine.onAddRuleMsg(new AddRuleMsg("true", "not ric==\"BT.L\"", true, createAction("action33")));
        assertEquals(14, engine.getRulesCount());
        assertEquals(3, errorHandler.errors.size());
        // wrong identifier but it will be known at evaluation only
        // evaluation error
        engine.onAddRuleMsg(new AddRuleMsg("true", "tud==\"CLIENT1\"", true, createAction("action32")));
        assertEquals(15, engine.getRulesCount());
        assertEquals(3, errorHandler.errors.size());

        TestOrderFieldSupplier order1 = new TestOrderFieldSupplier(123, 1);
        engine.onNewOrder(order1);
        assertEquals("action1,action5,action6", order1.actions());
        assertEquals(4, errorHandler.errors.size());

        TestOrderFieldSupplier order2 = new TestOrderFieldSupplier(124, 1);
        engine.onNewOrder(order2);
        assertEquals("action1,action7", order2.actions());
        assertEquals(5, errorHandler.errors.size());
    }

    private void initRules() {
        engine.onAddRuleMsg(new AddRuleMsg("true", "tuid== \"CLIENT1\"", true, createAction("action1")));
        engine.onAddRuleMsg(new AddRuleMsg("true", "tuid== \"CLIENT2\"", true, createAction("action2")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT2\"", true, createAction("action3")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT2\"", true, createAction("action4")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT1\" and ric==\"VOD.L\"", true, createAction("action5")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT1\" and not (ric==\"BT.L\")", true, createAction("action6")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT1\" and ric==\"BT.L\"", true, createAction("action7")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT2\" and ric==\"VOD.L\"", true, createAction("action8")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT2\" and not (ric==\"BT.L\")", true, createAction("action9")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT2\" and ric==\"BT.L\"", true, createAction("action10")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT3\" and ric==\"BT.L\"", true, createAction("action11")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT3\" and ric==\"VOD.L\"", true, createAction("action12")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT3\" and not (ric==\"BT.L\")", true, createAction("action13")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"VWAP\"", "tuid== \"CLIENT3\" and ric==\"TSCO.L\"", true, createAction("action14")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT1\"", true, createAction("action15")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT1\" and ric==\"VOD.L\"", true, createAction("action16")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT1\" and not (ric==\"BT.L\")", true, createAction("action17")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT1\" and ric==\"BT.L\"", true, createAction("action18")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT2\"", true, createAction("action19")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT2\" and ric==\"VOD.L\"", true, createAction("action20")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT2\" and not (ric==\"BT.L\")", true, createAction("action21")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT2\" and ric==\"BT.L\"", true, createAction("action22")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT3\"", true, createAction("action23")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT3\" and ric==\"VOD.L\"", true, createAction("action24")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT3\" and not (ric==\"BT.L\")", true, createAction("action25")));
        engine.onAddRuleMsg(new AddRuleMsg("algoNodeType==\"POV\"", "tuid== \"CLIENT3\" and ric==\"BT.L\"", true, createAction("action26")));
        engine.onAddRuleMsg(new AddRuleMsg("not(falconNodeName.isEmpty()) and not (isEmpty(algoNodeType))", "algoNodeType == \"TWAP\" and ric==\"BT.L\"", true, createAction("action27")));
    }

    private void initRulesUsingObjects() {
        engine.onAddRuleMsg(new AddRuleMsg("true", "order.tuid== \"CLIENT1\"", true, createAction("action1")));
        engine.onAddRuleMsg(new AddRuleMsg("true", "order.tuid== \"CLIENT2\"", true, createAction("action2")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT2\"", true, createAction("action3")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT2\"", true, createAction("action4")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT1\" and order.ric==\"VOD.L\"", true, createAction("action5")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT1\" and not (order.ric==\"BT.L\")", true, createAction("action6")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT1\" and order.ric==\"BT.L\"", true, createAction("action7")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT2\" and order.ric==\"VOD.L\"", true, createAction("action8")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT2\" and not (order.ric==\"BT.L\")", true, createAction("action9")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT2\" and order.ric==\"BT.L\"", true, createAction("action10")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT3\" and order.ric==\"BT.L\"", true, createAction("action11")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT3\" and order.ric==\"VOD.L\"", true, createAction("action12")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT3\" and not (order.ric==\"BT.L\")", true, createAction("action13")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"VWAP\"", "order.tuid== \"CLIENT3\" and order.ric==\"TSCO.L\"", true, createAction("action14")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT1\"", true, createAction("action15")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT1\" and order.ric==\"VOD.L\"", true, createAction("action16")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT1\" and not (order.ric==\"BT.L\")", true, createAction("action17")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT1\" and order.ric==\"BT.L\"", true, createAction("action18")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT2\"", true, createAction("action19")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT2\" and order.ric==\"VOD.L\"", true, createAction("action20")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT2\" and not (order.ric==\"BT.L\")", true, createAction("action21")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT2\" and order.ric==\"BT.L\"", true, createAction("action22")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT3\"", true, createAction("action23")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT3\" and order.ric==\"VOD.L\"", true, createAction("action24")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT3\" and not (order.ric==\"BT.L\")", true, createAction("action25")));
        engine.onAddRuleMsg(new AddRuleMsg("algo.nodeType==\"POV\"", "order.tuid== \"CLIENT3\" and order.ric==\"BT.L\"", true, createAction("action26")));
        engine.onAddRuleMsg(new AddRuleMsg("not(falcon.nodeName.isEmpty()) and not (isEmpty(algo.nodeType))", "algo.nodeType == \"TWAP\" and order.ric==\"BT.L\"", true, createAction("action27")));
    }


    static class TestCustomizationErrorHandler implements CustomizationErrorHandler {
        final List<String> errors = new ArrayList<>();

        @Override
        public void onNodeError(String ruleName, String msg) {
            errors.add(ruleName + ": " + msg);
        }

        @Override
        public void onOrderError(String ruleName, OrderFieldSupplier order, String msg) {
            errors.add(ruleName + ": " + msg);
        }
    }

    static class TestCustomizationReportHandler implements CustomizationReportHandler {
        String appliedRules;
        String reportAppliedRules;

        public void reset() {
            appliedRules = "";
            reportAppliedRules = "";
        }

        @Override
        public void onAppliedRule(OrderFieldSupplier order, String ruleName, CustomizationAction action, long ruleActionResult) {
            if (!appliedRules.isEmpty()) {
                appliedRules += ",";
            }
            appliedRules += ruleName;
        }

        @Override
        public void onOrderReport(OrderFieldSupplier order, CharSequence appliedRules) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < appliedRules.length(); i++) {
                sb.append(appliedRules.charAt(i));
            }
            this.reportAppliedRules = sb.toString();
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

        @Override
        public long apply(final CustomizationParamsList result, Object orderObj) {
            TestOrderFieldSupplier order = (TestOrderFieldSupplier)orderObj;
            order.applyAction(name);
            return 0;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}