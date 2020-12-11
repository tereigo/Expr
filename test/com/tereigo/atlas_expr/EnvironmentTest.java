package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.order.Order;
import com.tereigo.atlas_expr.order.OrderFieldSupplier;
import org.junit.jupiter.api.Test;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvironmentTest extends EvaluatorTestBase {

    @Test
    void environmentTestsWithSuppliers() {
        final ExprEnvironmentImpl env = new ExprEnvironmentImpl();
        env.defineDouble("PI", () -> 3.14);
        env.defineDouble("$PI", () -> 3.14);
        env.defineLong("$productId", () -> 123L);
        env.defineString("$ric", () -> "VOD.L");
        env.defineString("$nodeAlgoType", () -> "Axis");
        env.defineBool("$enabled", () -> true);
        env.defineByteBuffer("$tuid", () -> constant("CLIENT1"));

        runEnvironmentTests(env);
    }

    @Test
    void environmentTestsWithSuppliersForOrder() {
        Order order = new Order("VOD.L", 123L, true, constant("CLIENT1"));
        final ExprEnvironmentImpl env = new ExprEnvironmentImpl();
        env.defineDouble("PI", () -> 3.14);
        env.defineDouble("$PI", () -> 3.14);
        env.defineString("$nodeAlgoType", () -> "Axis");
        env.defineLong("$productId", order::productId);
        env.defineString("$ric", order::ric);
        env.defineBool("$enabled", order::enabled);
        env.defineByteBuffer("$tuid", order::tuid);

        runEnvironmentTests(env);
    }

    @Test
    void environmentTestsWithOrderSupplier() {
        OrderFieldSupplier orderSupplier = new OrderFieldSupplier();
        Order order1 = new Order("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);
        final ExprEnvironmentImpl env = new ExprEnvironmentImpl();
        env.defineDouble("PI", () -> 3.14);
        env.defineDouble("$PI", () -> 3.14);
        env.defineString("$nodeAlgoType", () -> "Axis");
        env.defineLong("$productId", orderSupplier::productId);
        env.defineString("$ric", orderSupplier::ric);
        env.defineBool("$enabled", orderSupplier::enabled);
        env.defineByteBuffer("$tuid", orderSupplier::tuid);

        runEnvironmentTests(env);

        // Change the order
        Order order2 = new Order("BT.L", 456L, false, constant("CLIENT2"));
        orderSupplier.setOrder(order2);

        // and execute with the same environment
        // we should see the fields of the new order
        assertTrue(evaluateBool("$productId == 456 and $ric == \"BT.L\"", env));
        assertTrue(evaluateBool("$nodeAlgoType != $ric", env));

        assertFalse(evaluateBool("$tuid == \"CLIENT1\"", env));
        assertFalse(evaluateBool("\"CLIENT1\" == $tuid", env));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT1\"]", env));
        assertTrue(evaluateBool("$tuid != \"CLIENT1\"", env));
        assertTrue(evaluateBool("\"CLIENT1\" != $tuid", env));
        assertTrue(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", env));
        assertTrue(evaluateBool("$tuid == \"CLIENT2\"", env));
        assertTrue(evaluateBool("\"CLIENT2\" == $tuid", env));
        assertTrue(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", env));
    }

    private void runEnvironmentTests(ExprEnvironmentImpl env) {
        assertEquals(4.14, evaluateDouble("1.0+$PI", env), EPS);
        assertEquals(6.28, evaluateDouble(" $PI  + $PI  ", env), EPS);
        assertEquals(0.0, evaluateDouble("($PI  + PI) * 0.0", env), EPS);
        assertEquals(-3.14, evaluateDouble("-$PI", env), EPS);
        assertEquals(-3.14, evaluateDouble("(-$PI)", env), EPS);
        assertEquals(-3.14, evaluateDouble("-($PI)", env), EPS);
        assertTrue(evaluateBool("$PI == PI", env));
        assertTrue(evaluateBool("$productId == 123 and $ric == \"VOD.L\"", env));
        assertTrue(evaluateBool("$productId == 567 or $enabled", env));
        assertFalse(evaluateBool("$productId == 567 and $enabled", env));
        assertFalse(evaluateBool("$nodeAlgoType == $ric", env));
        assertFalse(evaluateBool("$nodeAlgoType == \"123\"", env));
        assertEquals(124, evaluateLong("$productId + 1", env));
        assertEquals(100, evaluateLong("$productId - 23", env));
        assertTrue(evaluateBool("$tuid == \"CLIENT1\"", env));
        assertTrue(evaluateBool("\"CLIENT1\" == $tuid", env));
        assertTrue(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT1\"]", env));
        assertFalse(evaluateBool("$tuid != \"CLIENT1\"", env));
        assertFalse(evaluateBool("\"CLIENT1\" != $tuid", env));
        assertFalse(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", env));
        assertFalse(evaluateBool("$tuid == \"CLIENT2\"", env));
        assertFalse(evaluateBool("\"CLIENT2\" == $tuid", env));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", env));
    }
}