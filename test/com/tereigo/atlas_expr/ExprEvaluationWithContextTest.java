package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils;
import com.tereigo.atlas_expr.order.Order;
import com.tereigo.atlas_expr.order.OrderFieldSupplier;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.parseString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprEvaluationWithContextTest extends EvaluatorTestBase {

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

    @Test
    void functionTests() {
        OrderFieldSupplier orderSupplier = new OrderFieldSupplier();
        Order order1 = new Order("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);

        final ExprContextImpl globalCtx = new ExprContextImpl();
        globalCtx.defineFunction("PI", result -> result.accept(3.14));
        globalCtx.defineFunction("nodeAlgoType", result -> result.accept("Axis"));
        globalCtx.defineFunction("region", result -> result.accept("EMEA"));
        // this one generates garbage!
        globalCtx.defineFunction("atlasEnv", result -> result.accept(constant("PROD")));
        globalCtx.defineFunction("timeNs", result -> result.accept(System.nanoTime()));

        globalCtx.defineFunction("isEven", (result, arg1) -> {
            long l = arg1.getAsLong();
            result.accept(l % 2 == 0);
        });

        globalCtx.defineFunction("func0", result -> result.accept("func0"));

        globalCtx.defineFunction("func1", (result, arg1) -> {
            long l = arg1.getAsLong();
            result.accept(l);
        });

        globalCtx.defineFunction("func2", (result, arg1, arg2) -> {
            long l = arg1.getAsLong();
            // we accept both Double and Long as a second parameter
            double d = arg2.exprType() == ExprType.DOUBLE ? arg2.getAsDouble() : arg2.getAsLong();
            result.accept(l + d);
        });

        globalCtx.defineFunction("func3", (result, arg1, arg2, arg3) -> {
            long l = arg1.getAsLong();
            double d = arg2.getAsDouble();
            boolean bool = arg3.getAsBoolean();
            result.accept(l > d && bool);
        });

        globalCtx.defineFunction("func4", (result, arg1, arg2, arg3, arg4) -> {
            long l = arg1.getAsLong();
            double d = arg2.getAsDouble();
            boolean bool = arg3.getAsBoolean();
            // we accept both String and ByteBuffer for 4th arg
            if (arg4.exprType() == ExprType.STRING) {
                String s = arg4.getAsString();
                result.accept(l > d && bool && !s.isEmpty());
            } else {
                ByteBuffer bb = arg4.getAsByteBuffer();
                result.accept(l > d && bool && !ByteBufferUtils.isEmpty(bb));
            }
        });

        globalCtx.defineFunction("func5", (result, arg1, arg2, arg3, arg4, arg5) -> {
            long l = arg1.getAsLong();
            double d = arg2.getAsDouble();
            boolean bool = arg3.getAsBoolean();
            String s = arg4.getAsString();
            ByteBuffer bb = arg5.getAsByteBuffer();
            result.accept(l > d && bool && !s.isEmpty() && ByteBufferUtils.startWith(bb,"CLIENT"));
        });

        final ExprContextImpl orderCtx = new ExprContextImpl();
        orderCtx.defineLong("$productId", orderSupplier::productId);
        orderCtx.defineString("$ric", orderSupplier::ric);
        orderCtx.defineBool("$enabled", orderSupplier::enabled);
        orderCtx.defineByteBuffer("$tuid", orderSupplier::tuid);

        final ExprContextChained ctx = new ExprContextChained(globalCtx);
        ctx.add(orderCtx);

        assertTrue(evaluateBool("timeNs() > $productId", ctx));
        assertTrue(evaluateBool("3.14 == PI()", ctx));
        assertTrue(evaluateBool("region() == \"EMEA\" and atlasEnv() == \"PROD\"", ctx));
        assertTrue(evaluateBool("nodeAlgoType() == \"Axis\" and $ric == \"VOD.L\"", ctx));
        assertTrue(evaluateBool("atlasEnv() == \"PROD\" and $tuid == \"CLIENT1\"", ctx));
        assertFalse(evaluateBool("isEven($productId)", ctx));
        assertTrue(evaluateBool("not isEven($productId)", ctx));

        // Change the order
        Order order2 = new Order("BT.L", 456L, false, constant("CLIENT2"));
        orderSupplier.setOrder(order2);

        // and execute with the same context
        // we should see the fields of the new order
        assertTrue(evaluateBool("atlasEnv() == \"PROD\" and $tuid == \"CLIENT2\"", ctx));

        // Testing various functions
        assertTrue(evaluateBool("isEven(0)", ctx));
        assertFalse(evaluateBool("isEven(1)", ctx));
        assertTrue(evaluateBool("isEven(2)", ctx));
        assertTrue(evaluateBool("isEven($productId)", ctx));
        assertFalse(evaluateBool("not isEven($productId)", ctx));

        assertEquals("func0", evaluateString("func0()", ctx));
        assertTrue(evaluateBool("func0() == \"func0\"", ctx));

        assertEquals(1, evaluateLong("func1(1)", ctx));
        assertEquals(2, evaluateLong("func1(2)", ctx));
        assertTrue(evaluateBool("func1(1) == 1", ctx));
        assertTrue(evaluateBool("func1(2) == 2", ctx));
        assertTrue(evaluateBool("1 == func1(1)", ctx));
        assertTrue(evaluateBool("2 == func1(2)", ctx));

        assertEquals(456, evaluateLong("func1($productId)", ctx));
        assertTrue(evaluateBool("func1($productId) == 456", ctx));
        assertTrue(evaluateBool("456 == func1($productId)", ctx));

        assertEquals(3.0, evaluateDouble("func2(1, 2.0)", ctx), EPS);
        assertEquals(5.0, evaluateDouble("func2(2, 3.0)", ctx), EPS);
        assertTrue(evaluateBool("func2(1, 2.0) == 3", ctx));
        assertTrue(evaluateBool("func2(1, 2.0) == 3.0", ctx));
        assertTrue(evaluateBool("func2(1, 2.0) < PI()", ctx));
        assertTrue(evaluateBool("func2(2, 2.0) == 4.0", ctx));
        assertTrue(evaluateBool("3.0 == func2(1, 2.0)", ctx));
        assertTrue(evaluateBool("5 == func2(2, 3.0)", ctx));

        assertEquals(459.14, evaluateDouble("func2($productId, PI())", ctx), EPS);

        assertTrue(evaluateBool("func3(10, 1.0, true)", ctx));
        assertFalse(evaluateBool("func3(10, 1.0, false)", ctx));
        assertFalse(evaluateBool("func3(1, 10.0, true)", ctx));
        assertFalse(evaluateBool("func3(1, 10.0, false)", ctx));
        assertTrue(evaluateBool("func3(10, 1.0, true) == true", ctx));

        assertFalse(evaluateBool("func3($productId, PI(), $enabled)", ctx));
        assertTrue(evaluateBool("func3($productId, PI(), not $enabled)", ctx));

        assertTrue(evaluateBool("func4(10, 1.0, true, \"A\")", ctx));
        assertFalse(evaluateBool("func4(10, 1.0, true, \"\")", ctx));
        assertFalse(evaluateBool("func4(10, 1.0, false, \"A\")", ctx));
        assertFalse(evaluateBool("func4(1, 10.0, true, \"A\")", ctx));
        assertFalse(evaluateBool("func4(1, 10.0, false, \"A\")", ctx));
        assertFalse(evaluateBool("func4(1, 10.0, true, \"\")", ctx));
        assertTrue(evaluateBool("func4(10, 1.0, true, \"A\") == true", ctx));

        assertFalse(evaluateBool("func4($productId, PI(), $enabled, $ric)", ctx));
        assertFalse(evaluateBool("func4($productId, PI(), $enabled, $tuid)", ctx));
        assertTrue(evaluateBool("func4($productId, PI(), not $enabled, $ric)", ctx));
        assertTrue(evaluateBool("func4($productId, PI(), not $enabled, $tuid)", ctx));
        assertFalse(evaluateBool("func4($productId, PI(), $enabled, \"\")", ctx));
        assertFalse(evaluateBool("func4($productId, PI(), $enabled, \"\")", ctx));
        assertFalse(evaluateBool("func4($productId, PI(), not $enabled, \"\")", ctx));
        assertFalse(evaluateBool("func4($productId, PI(), not $enabled, \"\")", ctx));

        assertTrue(evaluateBool("func5(10, 1.0, true, \"A\", $tuid)", ctx));
        assertFalse(evaluateBool("func5(10, 1.0, true, \"\", $tuid)", ctx));
        assertFalse(evaluateBool("func5(10, 1.0, false, \"A\", $tuid)", ctx));
        assertFalse(evaluateBool("func5(1, 10.0, true, \"A\", $tuid)", ctx));
        assertFalse(evaluateBool("func5(1, 10.0, false, \"A\", $tuid)", ctx));
        assertFalse(evaluateBool("func5(1, 10.0, true, \"\", $tuid)", ctx));
        assertTrue(evaluateBool("func5(10, 1.0, true, \"A\", $tuid) == true", ctx));

        assertFalse(evaluateBool("func5($productId, PI(), $enabled, $ric, $tuid)", ctx));
        assertTrue(evaluateBool("func5($productId, PI(), not $enabled, $ric, $tuid)", ctx));
        assertFalse(evaluateBool("func5($productId, PI(), $enabled, \"\", $tuid)", ctx));
        assertFalse(evaluateBool("func5($productId, PI(), $enabled, \"\", $tuid)", ctx));
        assertFalse(evaluateBool("func5($productId, PI(), not $enabled, \"\", $tuid)", ctx));
        assertFalse(evaluateBool("func5($productId, PI(), not $enabled, \"\", $tuid)", ctx));
        assertTrue(evaluateBool("func5(func1(100), func2(func1(10), PI()), not $enabled, $ric, $tuid)", ctx));
        assertTrue(evaluateBool("func5(func1(100), func2(func1(10), func2(10, 1.0)), not $enabled, $ric, $tuid)", ctx));
        assertTrue(evaluateBool("func5(func1(100), func2(func1(10), func2(10, 1)), not $enabled, $ric, $tuid)", ctx));
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
    }
}