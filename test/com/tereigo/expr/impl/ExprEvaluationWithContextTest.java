package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextBuilderFactory;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.order.SimpleOrderFieldSupplier;
import com.tereigo.expr.order.TestOrder;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.utils.ByteBufferUtils.parseString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprEvaluationWithContextTest extends ExprEvaluatorTestBase {

    private static ExprContext createTestObjExprContext(final String nodeName, final ByteBuffer algoType) {
        return ExprContextBuilderFactory.localContext()
                .addString("nodeName", () -> nodeName)
                .addByteBuffer("algoType", () -> algoType)
                .getAsExprContext();
    }

    @Test
    void contextTestsWithSuppliers() {
        final ExprContext ctx = ExprContextBuilderFactory.globalContext()
                .addDouble("$PI", () -> 3.14)
                .addLong("$productId", () -> 123L)
                .addString("$ric", () -> "VOD.L")
                .addString("$nodeAlgoType", () -> "Vwap")
                .addBool("$enabled", () -> true)
                .addByteBuffer("$tuid", () -> constant("CLIENT1"))
                .getAsExprContext();

        runExpressionWithContextTests(ctx);
    }

    @Test
    void contextTestsAfterOptimizationForDebug() {
        final ExprContext testCtx = createTestObjExprContext("VWAP1", constant("Vwap"));

        final ExprContext ctx = ExprContextBuilderFactory.globalContext()
                .addDouble("$PI", () -> 3.14)
                .addLong("$productId", () -> 123L)
                .addString("$ric", () -> "VOD.L")
                .addString("$nodeAlgoType", () -> "Vwap")
                .addBool("$enabled", () -> true)
                .addByteBuffer("$tuid", () -> constant("CLIENT1"))
                .addExprContext("test", testCtx)
                .getAsExprContext();

        {
            final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, "1.0+$PI");
            assertEquals(4.14, evaluator.evaluateDouble(), EPS);
        }

        {
            final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, "not($enabled)");
            assertFalse(evaluator.evaluateBool());
        }

        {
            final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, "test.nodeName == 'VWAP1' and 'ABC'.contains('A') and $productId == 123 and $ric == 'VOD.L'");
            assertTrue(evaluator.evaluateBool());
        }
    }

    @Test
    void contextTestsAfterOptimization() {
        final ExprContext ctx = ExprContextBuilderFactory.globalContext()
                .addDouble("$PI", () -> 3.14)
                .addLong("$productId", () -> 123L)
                .addString("$ric", () -> "VOD.L")
                .addString("$nodeAlgoType", () -> "Vwap")
                .addBool("$enabled", () -> true)
                .addByteBuffer("$tuid", () -> constant("CLIENT1"))
                .getAsExprContext();

        runOptimizedExpressionWithContextTests(ctx);
    }

    @Test
    void contextTestsWithSuppliersForOrder() {
        final TestOrder order = new TestOrder("VOD.L", 123L, true, constant("CLIENT1"));
        final ExprContext ctx = ExprContextBuilderFactory.globalContext()
                .addDouble("$PI", () -> 3.14)
                .addString("$nodeAlgoType", () -> "Vwap")
                .addLong("$productId", order::productId)
                .addString("$ric", order::ric)
                .addBool("$enabled", order::enabled)
                .addByteBuffer("$tuid", order::tuid)
                .getAsExprContext();

        runExpressionWithContextTests(ctx);
    }

    @Test
    void contextTestsWithOrderSupplier() {
        final SimpleOrderFieldSupplier orderSupplier = new SimpleOrderFieldSupplier();
        final TestOrder order1 = new TestOrder("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);
        final ExprContextBuilder mutCtx = ExprContextBuilderFactory.globalContext();
        mutCtx.addDouble("$PI", () -> 3.14);
        mutCtx.addString("$nodeAlgoType", () -> "Vwap");
        mutCtx.addLong("$productId", orderSupplier::productId);
        mutCtx.addString("$ric", orderSupplier::ric);
        mutCtx.addBool("$enabled", orderSupplier::enabled);
        mutCtx.addByteBuffer("$tuid", orderSupplier::tuid);

        final ExprContext ctx = mutCtx.getAsExprContext();

        runExpressionWithContextTests(ctx);

        // Change the order
        final TestOrder order2 = new TestOrder("BT.L", 456L, false, constant("CLIENT2"));
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
        final ParseError err;

        final SimpleOrderFieldSupplier orderSupplier = new SimpleOrderFieldSupplier();
        final TestOrder order1 = new TestOrder("VOD.L", 123L, true, constant("CLIENT1"));
        orderSupplier.setOrder(order1);

        final ExprContextBuilder globalCtx = ExprContextBuilderFactory.globalContext();
        globalCtx.addDouble("$PI", () -> 3.14);
        globalCtx.addString("$nodeAlgoType", () -> "Vwap");
        globalCtx.addString("$region", () -> "EMEA");
        globalCtx.addByteBuffer("$falconEnv", () -> constant("PROD"));
        globalCtx.addLong("$timeNs", System::nanoTime);

        globalCtx.addAlias("$nodeAlgoType", "nodeAlgoType");
        globalCtx.addAlias("$region", "region");
        globalCtx.addAlias("$falconEnv", "falconEnv");
        globalCtx.addAlias("$timeNs", "timeNs");

        globalCtx.addLong("$productId", orderSupplier::productId);
        globalCtx.addString("$ric", orderSupplier::ric);
        globalCtx.addBool("$enabled", orderSupplier::enabled);
        globalCtx.addByteBuffer("$tuid", orderSupplier::tuid);

        globalCtx.addAlias("$productId", "productId");
        globalCtx.addAlias("$ric", "ric");
        globalCtx.addAlias("$enabled", "enabled");
        globalCtx.addAlias("$tuid", "tuid");
        // let's define an alias on alias
        globalCtx.addAlias("tuid", "clientID");

        RuntimeException runEx = assertThrows(RuntimeException.class, () -> globalCtx.addAlias("$tuid", "$tuid"));
        assertEquals("Identical name and alias: '$tuid'", runEx.getMessage());

        runEx = assertThrows(RuntimeException.class, () -> globalCtx.addAlias("unknown", "coolAlias"));
        assertEquals("Unknown identifier 'unknown' for alias 'coolAlias'", runEx.getMessage());

        final ExprContext ctx = globalCtx.getAsExprContext();

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

        assertTrue(evaluateBool("$productId == 123 and $ric == 'VOD.L'", ctx));
        assertTrue(evaluateBool("$nodeAlgoType != $ric", ctx));

        assertTrue(evaluateBool("$tuid == 'CLIENT1'", ctx));
        assertTrue(evaluateBool("'CLIENT1' == $tuid", ctx));
        assertTrue(evaluateBool("$tuid in ['CLIENT0', 'CLIENT1']", ctx));
        assertFalse(evaluateBool("$tuid != 'CLIENT1'", ctx));
        assertFalse(evaluateBool("'CLIENT1' != $tuid", ctx));
        assertFalse(evaluateBool("not ($tuid in ['CLIENT0', 'CLIENT1'])", ctx));
        assertFalse(evaluateBool("$tuid == 'CLIENT2'", ctx));
        assertFalse(evaluateBool("'CLIENT2' == $tuid", ctx));
        assertFalse(evaluateBool("$tuid in ['CLIENT0', 'CLIENT2']", ctx));

        // Change the order
        final TestOrder order2 = new TestOrder("BT.L", 456L, false, constant("CLIENT2"));
        orderSupplier.setOrder(order2);

        // and execute with the same context
        // we should see the fields of the new order
        assertTrue(evaluateBool("$productId == 456 and $ric == \"BT.L\"", ctx));
        assertTrue(evaluateBool("$nodeAlgoType != $ric", ctx));

        assertFalse(evaluateBool("$tuid == 'CLIENT1'", ctx));
        assertFalse(evaluateBool("'CLIENT1' == $tuid", ctx));
        assertFalse(evaluateBool("$tuid in ['CLIENT0', 'CLIENT1']", ctx));
        assertTrue(evaluateBool("$tuid != 'CLIENT1'", ctx));
        assertTrue(evaluateBool("'CLIENT' != $tuid", ctx));
        assertTrue(evaluateBool("not ($tuid in ['CLIENT0', 'CLIENT1'])", ctx));
        assertTrue(evaluateBool("$tuid == 'CLIENT2'", ctx));
        assertTrue(evaluateBool("'CLIENT2' == $tuid", ctx));
        assertTrue(evaluateBool("$tuid in ['CLIENT0', 'CLIENT2']", ctx));
    }

    private void runExpressionWithContextTests(final ExprContext ctx) {
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

    private void runOptimizedExpressionWithContextTests(final ExprContext ctx) {
        assertEquals(4.14, evaluateDoubleOptimized(ctx, "1.0+$PI"), EPS);
        assertEquals(6.28, evaluateDoubleOptimized(ctx, " $PI  + $PI  "), EPS);
        assertEquals(0.0, evaluateDoubleOptimized(ctx, "($PI  + PI) * 0.0"), EPS);
        assertEquals(-3.14, evaluateDoubleOptimized(ctx, "-$PI"), EPS);
        assertEquals(-3.14, evaluateDoubleOptimized(ctx, "(-$PI)"), EPS);
        assertEquals(-3.14, evaluateDoubleOptimized(ctx, "-($PI)"), EPS);
        assertFalse(evaluateBoolOptimized(ctx, "not($enabled)"));
        assertTrue(evaluateBoolOptimized(ctx, "$PI == $PI"));
        assertTrue(evaluateBoolOptimized(ctx, "$productId == 123 and $ric == \"VOD.L\""));
        assertTrue(evaluateBoolOptimized(ctx, "$productId == 123 and $ric == 'VOD.L'"));
        assertTrue(evaluateBoolOptimized(ctx, "$productId == 567 or $enabled"));
        assertFalse(evaluateBoolOptimized(ctx, "$productId == 567 and $enabled"));
        assertFalse(evaluateBoolOptimized(ctx, "$nodeAlgoType == $ric"));
        assertFalse(evaluateBoolOptimized(ctx, "$nodeAlgoType == \"123\""));
        assertEquals(124, evaluateLongOptimized(ctx, "$productId + 1"));
        assertEquals(100, evaluateLongOptimized(ctx, "$productId - 23"));
        assertTrue(evaluateBoolOptimized(ctx, "$tuid == \"CLIENT1\""));
        assertTrue(evaluateBoolOptimized(ctx, "\"CLIENT1\" == $tuid"));
        assertTrue(evaluateBoolOptimized(ctx, "$tuid in [\"CLIENT0\", \"CLIENT1\"]"));
        assertFalse(evaluateBoolOptimized(ctx, "$tuid != \"CLIENT1\""));
        assertFalse(evaluateBoolOptimized(ctx, "\"CLIENT1\" != $tuid"));
        assertFalse(evaluateBoolOptimized(ctx, "'CLIENT1' != $tuid"));
        assertFalse(evaluateBoolOptimized(ctx, "not ($tuid in [\"CLIENT0\", \"CLIENT1\"])"));
        assertFalse(evaluateBoolOptimized(ctx, "$tuid == \"CLIENT2\""));
        assertFalse(evaluateBoolOptimized(ctx, "\"CLIENT2\" == $tuid"));
        assertFalse(evaluateBoolOptimized(ctx, "$tuid in [\"CLIENT0\", \"CLIENT2\"]"));
        assertFalse(evaluateBoolOptimized(ctx, "$tuid in ['CLIENT0', 'CLIENT2']"));
        assertEquals(constant("CLIENT1"), evaluateByteBufferOptimized(ctx, "$tuid"));
        assertEquals("CLIENT1", parseString(evaluateByteBufferOptimized(ctx, "$tuid")));
    }
}