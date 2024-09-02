package com.tereigo.expr;

import com.tereigo.expr.domains.falcon.FalconDomain;
import com.tereigo.expr.falcon.utils.OrderPrice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FalconDomainTest extends ExprEvaluatorTestBase {

    @Test
    void falconTests() {
        RuntimeError runErr;

        final MutableExprContext ctx = ExprContextFactory.create();
        FalconDomain.defineShortcuts(ctx);

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
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol(1000000)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol(1000000)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol(\"A\")", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol(true)'", runErr.getMessage());
        // isMarketPrice
        assertTrue(evaluateBool("isMarketPrice(" + OrderPrice.market() + ")", ctx));
        assertFalse(evaluateBool("isMarketPrice(" + OrderPrice.invalid() + ")", ctx));
        assertFalse(evaluateBool("isMarketPrice(1000000)", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(1000000.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(\"A\")", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(true)'", runErr.getMessage());
        // isLimitPrice
        assertFalse(evaluateBool("isLimitPrice(" + OrderPrice.market() + ")", ctx));
        assertFalse(evaluateBool("isLimitPrice(" + OrderPrice.market() + ")", ctx));
        assertTrue(evaluateBool("isLimitPrice(1000000)", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(1000000.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(\"A\")", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(true)'", runErr.getMessage());
        // isValidPrice
        assertTrue(evaluateBool("isValidPrice(" + OrderPrice.market() + ")", ctx));
        assertFalse(evaluateBool("isValidPrice(" + OrderPrice.invalid() + ")", ctx));
        assertTrue(evaluateBool("isValidPrice(1000000)", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice(1000000.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice(\"A\")", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice(\"A\")'", runErr.getMessage());
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

}