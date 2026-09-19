package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.domains.falcon.FalconDomain;
import com.tereigo.expr.falcon.utils.OrderPrice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FalconDomainTest extends ExprEvaluatorTestBase {
    private boolean optimized;

    @Test
    void falconTests() {

        final ExprContextBuilder ctx = ExprContextFactory.globalContext();
        FalconDomain.defineFunctions(ctx);

        optimized = false;
        runFalconTests(ctx.getAsExprContext());
    }

    @Test
    void falconOptimizedTests() {

        final ExprContextBuilder ctx = ExprContextFactory.globalContext();
        FalconDomain.defineFunctions(ctx);

        optimized = true;
        runFalconTests(ctx.getAsExprContext());
    }

    private void runFalconTests(final ExprContext ctx) {
        RuntimeError runErr;
        // ltod
        assertEquals(0.0, evaluateDouble("ltod(0)", ctx), EPS);
        assertEquals(1.0, evaluateDouble("ltod(1000000)", ctx), EPS);
        assertEquals(-1.0, evaluateDouble("ltod(-1000000)", ctx), EPS);
        assertEquals(2.0, evaluateDouble("ltod(2000000)", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("ltod(1000000.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'ltod': Operand must be a LONG number in expression 'ltod(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("ltod(\"A\")", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'ltod': Operand must be a LONG number in expression 'ltod(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("ltod(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'ltod': Operand must be a LONG number in expression 'ltod(true)'", runErr.getMessage());
        // dtol
        assertEquals(0L, evaluateLong("dtol(0.0)", ctx));
        assertEquals(100_000_000L, evaluateLong("dtol(100.0)", ctx));
        assertEquals(-100_000_000L, evaluateLong("dtol(-100.0)", ctx));
        assertEquals(123_456_000L, evaluateLong("dtol(123.456)", ctx));
        assertEquals(10_000_000L, evaluateLong("dtol(10)", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol('A')", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol('A')'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol(true)'", runErr.getMessage());
        // isMarketPrice
        assertTrue(evaluateBool("isMarketPrice(" + OrderPrice.market() + ")", ctx));
        assertFalse(evaluateBool("isMarketPrice(" + OrderPrice.invalid() + ")", ctx));
        assertFalse(evaluateBool("isMarketPrice(1000000)", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(1000000.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice('A')", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice('A')'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(true)'", runErr.getMessage());
        // isLimitPrice
        assertFalse(evaluateBool("isLimitPrice(" + OrderPrice.market() + ")", ctx));
        assertFalse(evaluateBool("isLimitPrice(" + OrderPrice.market() + ")", ctx));
        assertTrue(evaluateBool("isLimitPrice(1000000)", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(1000000.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice('A')", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice('A')'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(true)'", runErr.getMessage());
        // isValidPrice
        assertTrue(evaluateBool("isValidPrice(" + OrderPrice.market() + ")", ctx));
        assertFalse(evaluateBool("isValidPrice(" + OrderPrice.invalid() + ")", ctx));
        assertTrue(evaluateBool("isValidPrice(1000000)", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice(1000000.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice('A')", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice('A')'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice(true)'", runErr.getMessage());

        assertEquals(3.0, evaluateDouble("ltod(toLong(PI) * 1000000)", ctx), EPS);
        assertEquals(3.0, evaluateDouble("ltod(round(PI) * 1000000)", ctx), EPS);
        assertEquals(3.0, evaluateDouble("ltod(roundToNearest(PI) * 1000000)", ctx), EPS);
        assertEquals(4.0, evaluateDouble("ltod(roundUp(PI) * 1000000)", ctx), EPS);
        assertEquals(3.0, evaluateDouble("ltod(roundDown(PI) * 1000000)", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("ltod(toLong(PI * 1000000))", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("ltod(round(PI * 1000000))", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("ltod(roundToNearest(PI * 1000000))", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("ltod(roundUp(PI * 1000000))", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("ltod(roundDown(PI * 1000000))", ctx), EPS);
        assertEquals(3_141_592L, evaluateLong("dtol(PI)", ctx));
        assertTrue(evaluateBool("isLimitPrice(dtol(PI))", ctx));
        assertFalse(evaluateBool("isMarketPrice(dtol(PI))", ctx));
        assertTrue(evaluateBool("isValidPrice(dtol(PI))", ctx));
    }

    protected boolean evaluateBool(final String text, final ExprContext ctx) {
        return optimized ? super.evaluateBoolOptimized(ctx, text) : super.evaluateBool(text, ctx);
    }

    protected String evaluateString(final String text, final ExprContext ctx) {
        return optimized ? super.evaluateStringOptimized(ctx, text) : super.evaluateString(text, ctx);
    }

    protected long evaluateLong(final String text, final ExprContext ctx) {
        return optimized ? super.evaluateLongOptimized(ctx, text) : super.evaluateLong(text, ctx);
    }

    protected double evaluateDouble(final String text, final ExprContext ctx) {
        return optimized ? super.evaluateDoubleOptimized(ctx, text) : super.evaluateDouble(text, ctx);
    }

    protected void evaluate(final String text, final ExprContext ctx) {
        if (optimized) {
            super.evaluateOptimized(ctx, text);
        } else {
            super.evaluate(text, ctx);
        }
    }
}