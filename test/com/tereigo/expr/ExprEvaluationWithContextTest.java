package com.tereigo.expr;

import com.tereigo.expr.order.SimpleOrderFieldSupplier;
import com.tereigo.expr.order.TestOrder;
import org.junit.jupiter.api.Test;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.utils.ByteBufferUtils.parseString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprEvaluationWithContextTest extends ExprEvaluatorTestBase {

    @Test
    void contextTestsWithSuppliers() {
        final MutableExprContext ctx = ExprContextFactory.create();
        ctx.defineDouble("PI", () -> 3.14);
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineLong("$productId", () -> 123L);
        ctx.defineString("$ric", () -> "VOD.L");
        ctx.defineString("$nodeAlgoType", () -> "Vwap");
        ctx.defineBool("$enabled", () -> true);
        ctx.defineByteBuffer("$tuid", () -> constant("CLIENT1"));

        runExpressionWithContextTests(ctx);
    }

    @Test
    void contextTestsWithSuppliersForOrder() {
        TestOrder order = new TestOrder("VOD.L", 123L, true, constant("CLIENT1"));
        final MutableExprContext ctx = ExprContextFactory.create();
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineString("$nodeAlgoType", () -> "Vwap");
        ctx.defineLong("$productId", order::productId);
        ctx.defineString("$ric", order::ric);
        ctx.defineBool("$enabled", order::enabled);
        ctx.defineByteBuffer("$tuid", order::tuid);

        runExpressionWithContextTests(ctx);
    }

    @Test
    void contextTestsWithOrderSupplier() {
        SimpleOrderFieldSupplier orderSupplier = new SimpleOrderFieldSupplier();
        TestOrder order1 = new TestOrder("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);
        final MutableExprContext ctx = ExprContextFactory.create();
        ctx.defineDouble("PI", () -> 3.14);
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineString("$nodeAlgoType", () -> "Vwap");
        ctx.defineLong("$productId", orderSupplier::productId);
        ctx.defineString("$ric", orderSupplier::ric);
        ctx.defineBool("$enabled", orderSupplier::enabled);
        ctx.defineByteBuffer("$tuid", orderSupplier::tuid);

        runExpressionWithContextTests(ctx);

        // Change the order
        TestOrder order2 = new TestOrder("BT.L", 456L, false, constant("CLIENT2"));
        orderSupplier.setOrder(order2);

        // and execute with the same context
        // we should see the fields of the new order
        assertTrue(evaluateBool("$productId == 456 and $ric == \"BT.L\"", ctx));
        assertTrue(evaluateBool("$nodeAlgoType != $ric", ctx));

        assertFalse(evaluateBool("$tuid == \"CLIENT1\"", ctx));
        assertFalse(evaluateBool("\"CLIENT1\" == $tuid", ctx));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT1\"]", ctx));
        assertTrue(evaluateBool("$tuid != \"CLIENT1\"", ctx));
        assertTrue(evaluateBool("\"CLIENT1\" != $tuid", ctx));
        assertTrue(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", ctx));
        assertTrue(evaluateBool("$tuid == \"CLIENT2\"", ctx));
        assertTrue(evaluateBool("\"CLIENT2\" == $tuid", ctx));
        assertTrue(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", ctx));
        assertEquals(constant("CLIENT2"), evaluateByteBuffer("$tuid", ctx));
        assertEquals("CLIENT2", parseString(evaluateByteBuffer("$tuid", ctx)));
    }

    @Test
    void globalAndLocalContextTests() {
        RuntimeError runErr;
        ParseError err;

        SimpleOrderFieldSupplier orderSupplier = new SimpleOrderFieldSupplier();
        TestOrder order1 = new TestOrder("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);

        final MutableExprContext globalCtx = ExprContextFactory.create();
        globalCtx.defineDouble("PI", () -> 3.14);
        globalCtx.defineDouble("$PI", () -> 3.14);
        globalCtx.defineString("$nodeAlgoType", () -> "Vwap");
        globalCtx.defineString("$region", () -> "EMEA");
        globalCtx.defineByteBuffer("$falconEnv", () -> constant("PROD"));
        globalCtx.defineLong("$timeNs", System::nanoTime);

        globalCtx.addAlias("$nodeAlgoType", "nodeAlgoType");
        globalCtx.addAlias("$region", "region");
        globalCtx.addAlias("$falconEnv", "falconEnv");
        globalCtx.addAlias("$timeNs", "timeNs");

        final MutableExprContext orderCtx = ExprContextFactory.create();
        orderCtx.defineLong("$productId", orderSupplier::productId);
        orderCtx.defineString("$ric", orderSupplier::ric);
        orderCtx.defineBool("$enabled", orderSupplier::enabled);
        orderCtx.defineByteBuffer("$tuid", orderSupplier::tuid);

        orderCtx.addAlias("$productId", "productId");
        orderCtx.addAlias("$ric", "ric");
        orderCtx.addAlias("$enabled", "enabled");
        orderCtx.addAlias("$tuid", "tuid");
        // let's define an alias on alias
        orderCtx.addAlias("tuid", "clientID");

        RuntimeException runEx = assertThrows(RuntimeException.class, () -> orderCtx.addAlias("$tuid", "$tuid"));
        assertEquals("Identical name and alias: '$tuid'", runEx.getMessage());

        runEx = assertThrows(RuntimeException.class, () -> orderCtx.addAlias("unknown", "coolAlias"));
        assertEquals("Unknown identifier 'unknown' for alias 'coolAlias'", runEx.getMessage());

        final ExprContextChained ctx = new ExprContextChained(globalCtx);
        ctx.add(orderCtx);

        runExpressionWithContextTests(ctx);

        assertTrue(evaluateBool("$timeNs > $productId", ctx));
        assertTrue(evaluateBool("$region == \"EMEA\" and $falconEnv == \"PROD\"", ctx));
        assertTrue(evaluateBool("$nodeAlgoType == \"Vwap\" and $ric == \"VOD.L\"", ctx));
        assertTrue(evaluateBool("$falconEnv == \"PROD\" and $tuid == \"CLIENT1\"", ctx));
        // using aliases
        assertTrue(evaluateBool("timeNs > productId", ctx));
        assertTrue(evaluateBool("region == \"EMEA\" and falconEnv == \"PROD\"", ctx));
        assertTrue(evaluateBool("nodeAlgoType == \"Vwap\" and ric == \"VOD.L\"", ctx));
        assertTrue(evaluateBool("falconEnv == \"PROD\" and tuid == \"CLIENT1\"", ctx));
        // using alias on alias
        assertTrue(evaluateBool("falconEnv == \"PROD\" and clientID == \"CLIENT1\"", ctx));

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$curTime > 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown identifier '$curTime' in expression '$curTime > 0'", runErr.getMessage());

        // this is a parsing error
        err = assertThrows(ParseError.class, () -> evaluate("not $productId == 123", ctx));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not $productId == 123'", err.getMessage());

        // but it's still possible to get evaluation error as well
        runErr = assertThrows(RuntimeError.class, () -> evaluate("not($productId) == 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not($productId) == 123'", runErr.getMessage());

        // Change the order
        TestOrder order2 = new TestOrder("BT.L", 456L, false, constant("CLIENT2"));
        orderSupplier.setOrder(order2);

        // and execute with the same context
        // we should see the fields of the new order
        assertTrue(evaluateBool("$productId == 456 and $ric == \"BT.L\"", ctx));
        assertTrue(evaluateBool("$nodeAlgoType != $ric", ctx));

        assertFalse(evaluateBool("$tuid == \"CLIENT1\"", ctx));
        assertFalse(evaluateBool("\"CLIENT1\" == $tuid", ctx));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT1\"]", ctx));
        assertTrue(evaluateBool("$tuid != \"CLIENT1\"", ctx));
        assertTrue(evaluateBool("\"CLIENT1\" != $tuid", ctx));
        assertTrue(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", ctx));
        assertTrue(evaluateBool("$tuid == \"CLIENT2\"", ctx));
        assertTrue(evaluateBool("\"CLIENT2\" == $tuid", ctx));
        assertTrue(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", ctx));
    }

    private void runExpressionWithContextTests(ExprContext ctx) {
        assertEquals(4.14, evaluateDouble("1.0+$PI", ctx), EPS);
        assertEquals(6.28, evaluateDouble(" $PI  + $PI  ", ctx), EPS);
        assertEquals(0.0, evaluateDouble("($PI  + PI) * 0.0", ctx), EPS);
        assertEquals(-3.14, evaluateDouble("-$PI", ctx), EPS);
        assertEquals(-3.14, evaluateDouble("(-$PI)", ctx), EPS);
        assertEquals(-3.14, evaluateDouble("-($PI)", ctx), EPS);
        assertFalse(evaluateBool("not($enabled)", ctx));
        assertTrue(evaluateBool("$PI == $PI", ctx));
        assertTrue(evaluateBool("$productId == 123 and $ric == \"VOD.L\"", ctx));
        assertTrue(evaluateBool("$productId == 123 and $ric == 'VOD.L'", ctx));
        assertTrue(evaluateBool("$productId == 567 or $enabled", ctx));
        assertFalse(evaluateBool("$productId == 567 and $enabled", ctx));
        assertFalse(evaluateBool("$nodeAlgoType == $ric", ctx));
        assertFalse(evaluateBool("$nodeAlgoType == \"123\"", ctx));
        assertEquals(124, evaluateLong("$productId + 1", ctx));
        assertEquals(100, evaluateLong("$productId - 23", ctx));
        assertTrue(evaluateBool("$tuid == \"CLIENT1\"", ctx));
        assertTrue(evaluateBool("\"CLIENT1\" == $tuid", ctx));
        assertTrue(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT1\"]", ctx));
        assertFalse(evaluateBool("$tuid != \"CLIENT1\"", ctx));
        assertFalse(evaluateBool("\"CLIENT1\" != $tuid", ctx));
        assertFalse(evaluateBool("'CLIENT1' != $tuid", ctx));
        assertFalse(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", ctx));
        assertFalse(evaluateBool("$tuid == \"CLIENT2\"", ctx));
        assertFalse(evaluateBool("\"CLIENT2\" == $tuid", ctx));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", ctx));
        assertFalse(evaluateBool("$tuid in ['CLIENT0', 'CLIENT2']", ctx));
        assertEquals(constant("CLIENT1"), evaluateByteBuffer("$tuid", ctx));
        assertEquals("CLIENT1", parseString(evaluateByteBuffer("$tuid", ctx)));
    }
}