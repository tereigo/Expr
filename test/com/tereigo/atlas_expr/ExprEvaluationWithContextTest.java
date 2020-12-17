package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.order.Order;
import com.tereigo.atlas_expr.order.OrderFieldSupplier;
import org.junit.jupiter.api.Test;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.parseString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprEvaluationWithContextTest extends ExprEvaluatorTestBase {

    @Test
    void contextTestsWithSuppliers() {
        final ExprContextImpl ctx = new ExprContextImpl();
        ctx.defineDouble("PI", () -> 3.14);
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineLong("$productId", () -> 123L);
        ctx.defineString("$ric", () -> "VOD.L");
        ctx.defineString("$nodeAlgoType", () -> "Axis");
        ctx.defineBool("$enabled", () -> true);
        ctx.defineByteBuffer("$tuid", () -> constant("CLIENT1"));

        runExpressionWithContextTests(ctx);
    }

    @Test
    void contextTestsWithSuppliersForOrder() {
        Order order = new Order("VOD.L", 123L, true, constant("CLIENT1"));
        final ExprContextImpl ctx = new ExprContextImpl();
        ctx.defineDouble("PI", () -> 3.14);
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineString("$nodeAlgoType", () -> "Axis");
        ctx.defineLong("$productId", order::productId);
        ctx.defineString("$ric", order::ric);
        ctx.defineBool("$enabled", order::enabled);
        ctx.defineByteBuffer("$tuid", order::tuid);

        runExpressionWithContextTests(ctx);
    }

    @Test
    void contextTestsWithOrderSupplier() {
        OrderFieldSupplier orderSupplier = new OrderFieldSupplier();
        Order order1 = new Order("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);
        final ExprContextImpl ctx = new ExprContextImpl();
        ctx.defineDouble("PI", () -> 3.14);
        ctx.defineDouble("$PI", () -> 3.14);
        ctx.defineString("$nodeAlgoType", () -> "Axis");
        ctx.defineLong("$productId", orderSupplier::productId);
        ctx.defineString("$ric", orderSupplier::ric);
        ctx.defineBool("$enabled", orderSupplier::enabled);
        ctx.defineByteBuffer("$tuid", orderSupplier::tuid);

        runExpressionWithContextTests(ctx);

        // Change the order
        Order order2 = new Order("BT.L", 456L, false, constant("CLIENT2"));
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
        OrderFieldSupplier orderSupplier = new OrderFieldSupplier();
        Order order1 = new Order("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);

        final ExprContextImpl globalCtx = new ExprContextImpl();
        globalCtx.defineDouble("PI", () -> 3.14);
        globalCtx.defineDouble("$PI", () -> 3.14);
        globalCtx.defineString("$nodeAlgoType", () -> "Axis");
        globalCtx.defineString("$region", () -> "EMEA");
        globalCtx.defineByteBuffer("$atlasEnv", () -> constant("PROD"));
        globalCtx.defineLong("$timeNs", System::nanoTime);

        final ExprContextImpl orderCtx = new ExprContextImpl();
        orderCtx.defineLong("$productId", orderSupplier::productId);
        orderCtx.defineString("$ric", orderSupplier::ric);
        orderCtx.defineBool("$enabled", orderSupplier::enabled);
        orderCtx.defineByteBuffer("$tuid", orderSupplier::tuid);

        final ExprContextChained ctx = new ExprContextChained(globalCtx);
        ctx.add(orderCtx);

        runExpressionWithContextTests(ctx);

        assertTrue(evaluateBool("$timeNs > $productId", ctx));
        assertTrue(evaluateBool("$region == \"EMEA\" and $atlasEnv == \"PROD\"", ctx));
        assertTrue(evaluateBool("$nodeAlgoType == \"Axis\" and $ric == \"VOD.L\"", ctx));
        assertTrue(evaluateBool("$atlasEnv == \"PROD\" and $tuid == \"CLIENT1\"", ctx));

        RuntimeError runErr = assertThrows(RuntimeError.class, () -> evaluate("$curTime > 0", ctx));
        assertEquals("Unknown identifier '$curTime'", runErr.getMessage());

        // Change the order
        Order order2 = new Order("BT.L", 456L, false, constant("CLIENT2"));
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
        assertTrue(evaluateBool("$PI == PI", ctx));
        assertTrue(evaluateBool("$productId == 123 and $ric == \"VOD.L\"", ctx));
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
        assertFalse(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", ctx));
        assertFalse(evaluateBool("$tuid == \"CLIENT2\"", ctx));
        assertFalse(evaluateBool("\"CLIENT2\" == $tuid", ctx));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", ctx));
        assertEquals(constant("CLIENT1"), evaluateByteBuffer("$tuid", ctx));
        assertEquals("CLIENT1", parseString(evaluateByteBuffer("$tuid", ctx)));
    }
}