package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.atlas.utils.OrderPrice;
import org.junit.jupiter.api.Test;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprNativeFunctionsTest extends ExprEvaluatorTestBase {

    @Test
    void nativeFunctionsTest() {
        RuntimeError runErr;
        // min
        assertEquals(1, evaluateLong("min(1, 2)"));
        assertEquals(1, evaluateLong("min(2, 1)"));
        assertEquals(1.0, evaluateDouble("min(2.0, 1)"), EPS);
        assertEquals(1.0, evaluateDouble("min(1, 2.0)"), EPS);
        assertEquals(1.0, evaluateDouble("min(2.0, 1.0)"), EPS);
        assertEquals(1.0, evaluateDouble("min(1.0, 2.0)"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("min(1, \"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'min': Operands must be numbers in expression 'min(1, \"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("min(true, 1.0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'min': Operands must be numbers in expression 'min(true, 1.0)'", runErr.getMessage());
        // max
        assertEquals(2, evaluateLong("max(1, 2)"));
        assertEquals(2, evaluateLong("max(2, 1)"));
        assertEquals(2.0, evaluateDouble("max(2.0, 1)"), EPS);
        assertEquals(2.0, evaluateDouble("max(1, 2.0)"), EPS);
        assertEquals(2.0, evaluateDouble("max(2.0, 1.0)"), EPS);
        assertEquals(2.0, evaluateDouble("max(1.0, 2.0)"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("max(1, \"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'max': Operands must be numbers in expression 'max(1, \"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("max(true, 1.0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'max': Operands must be numbers in expression 'max(true, 1.0)'", runErr.getMessage());
        // abs
        assertEquals(0, evaluateLong("abs(0)"));
        assertEquals(1, evaluateLong("abs(1)"));
        assertEquals(1, evaluateLong("abs(-1)"));
        assertEquals(1.0, evaluateDouble("abs(1.0)"), EPS);
        assertEquals(1.0, evaluateDouble("abs(-1.0)"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("abs(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'abs': Operand must be a number in expression 'abs(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("abs(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'abs': Operand must be a number in expression 'abs(true)'", runErr.getMessage());
        // round
        assertEquals(0, evaluateLong("round(0)"));
        assertEquals(1, evaluateLong("round(1)"));
        assertEquals(-1, evaluateLong("round(-1)"));
        assertEquals(0, evaluateLong("round(0.01)"));
        assertEquals(0, evaluateLong("round(0.0)"));
        assertEquals(0, evaluateLong("round(-0.01)"));
        assertEquals(1, evaluateLong("round(0.99)"));
        assertEquals(1, evaluateLong("round(1.0)"));
        assertEquals(1, evaluateLong("round(1.01)"));
        assertEquals(-1, evaluateLong("round(-0.99)"));
        assertEquals(-1, evaluateLong("round(-1.0)"));
        assertEquals(-1, evaluateLong("round(-1.01)"));
        assertEquals(1, evaluateLong("round(1.1)"));
        assertEquals(2, evaluateLong("round(1.5)"));
        assertEquals(2, evaluateLong("round(1.6)"));
        assertEquals(2, evaluateLong("round(1.9)"));
        assertEquals(2, evaluateLong("round(2.1)"));
        assertEquals(-1, evaluateLong("round(-1.1)"));
        assertEquals(-1, evaluateLong("round(-1.5)"));
        assertEquals(-2, evaluateLong("round(-1.6)"));
        assertEquals(-2, evaluateLong("round(-1.9)"));
        assertEquals(-2, evaluateLong("round(-2.1)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("round(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'round': Operand must be a number in expression 'round(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("round(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'round': Operand must be a number in expression 'round(true)'", runErr.getMessage());
        // roundToNearest
        assertEquals(0, evaluateLong("roundToNearest(0)"));
        assertEquals(1, evaluateLong("roundToNearest(1)"));
        assertEquals(-1, evaluateLong("roundToNearest(-1)"));
        assertEquals(0, evaluateLong("roundToNearest(0.01)"));
        assertEquals(0, evaluateLong("roundToNearest(0.0)"));
        assertEquals(0, evaluateLong("roundToNearest(-0.01)"));
        assertEquals(1, evaluateLong("roundToNearest(0.99)"));
        assertEquals(1, evaluateLong("roundToNearest(1.0)"));
        assertEquals(1, evaluateLong("roundToNearest(1.01)"));
        assertEquals(-1, evaluateLong("roundToNearest(-0.99)"));
        assertEquals(-1, evaluateLong("roundToNearest(-1.0)"));
        assertEquals(-1, evaluateLong("roundToNearest(-1.01)"));
        assertEquals(1, evaluateLong("roundToNearest(1.1)"));
        assertEquals(2, evaluateLong("roundToNearest(1.5)"));
        assertEquals(2, evaluateLong("roundToNearest(1.6)"));
        assertEquals(2, evaluateLong("roundToNearest(1.9)"));
        assertEquals(2, evaluateLong("roundToNearest(2.1)"));
        assertEquals(-1, evaluateLong("roundToNearest(-1.1)"));
        assertEquals(-2, evaluateLong("roundToNearest(-1.5)"));
        assertEquals(-2, evaluateLong("roundToNearest(-1.6)"));
        assertEquals(-2, evaluateLong("roundToNearest(-1.9)"));
        assertEquals(-2, evaluateLong("roundToNearest(-2.1)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("roundToNearest(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'roundToNearest': Operand must be a number in expression 'roundToNearest(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("roundToNearest(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'roundToNearest': Operand must be a number in expression 'roundToNearest(true)'", runErr.getMessage());
        // roundUp
        assertEquals(0, evaluateLong("roundUp(0)"));
        assertEquals(1, evaluateLong("roundUp(1)"));
        assertEquals(-1, evaluateLong("roundUp(-1)"));
        assertEquals(1, evaluateLong("roundUp(0.01)"));
        assertEquals(0, evaluateLong("roundUp(0.0)"));
        assertEquals(0, evaluateLong("roundUp(-0.01)"));
        assertEquals(1, evaluateLong("roundUp(0.99)"));
        assertEquals(1, evaluateLong("roundUp(1.0)"));
        assertEquals(2, evaluateLong("roundUp(1.01)"));
        assertEquals(0, evaluateLong("roundUp(-0.99)"));
        assertEquals(-1, evaluateLong("roundUp(-1.0)"));
        assertEquals(-1, evaluateLong("roundUp(-1.01)"));
        assertEquals(2, evaluateLong("roundUp(1.1)"));
        assertEquals(2, evaluateLong("roundUp(1.5)"));
        assertEquals(2, evaluateLong("roundUp(1.6)"));
        assertEquals(2, evaluateLong("roundUp(1.9)"));
        assertEquals(3, evaluateLong("roundUp(2.1)"));
        assertEquals(-1, evaluateLong("roundUp(-1.1)"));
        assertEquals(-1, evaluateLong("roundUp(-1.5)"));
        assertEquals(-1, evaluateLong("roundUp(-1.6)"));
        assertEquals(-1, evaluateLong("roundUp(-1.9)"));
        assertEquals(-2, evaluateLong("roundUp(-2.1)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("roundUp(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'roundUp': Operand must be a number in expression 'roundUp(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("roundUp(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'roundUp': Operand must be a number in expression 'roundUp(true)'", runErr.getMessage());
        // roundDown
        assertEquals(0, evaluateLong("roundDown(0)"));
        assertEquals(1, evaluateLong("roundDown(1)"));
        assertEquals(-1, evaluateLong("roundDown(-1)"));
        assertEquals(0, evaluateLong("roundDown(0.01)"));
        assertEquals(0, evaluateLong("roundDown(0.0)"));
        assertEquals(-1, evaluateLong("roundDown(-0.01)"));
        assertEquals(0, evaluateLong("roundDown(0.99)"));
        assertEquals(1, evaluateLong("roundDown(1.0)"));
        assertEquals(1, evaluateLong("roundDown(1.01)"));
        assertEquals(-1, evaluateLong("roundDown(-0.99)"));
        assertEquals(-1, evaluateLong("roundDown(-1.0)"));
        assertEquals(-2, evaluateLong("roundDown(-1.01)"));
        assertEquals(1, evaluateLong("roundDown(1.1)"));
        assertEquals(1, evaluateLong("roundDown(1.5)"));
        assertEquals(1, evaluateLong("roundDown(1.6)"));
        assertEquals(1, evaluateLong("roundDown(1.9)"));
        assertEquals(2, evaluateLong("roundDown(2.1)"));
        assertEquals(-2, evaluateLong("roundDown(-1.1)"));
        assertEquals(-2, evaluateLong("roundDown(-1.5)"));
        assertEquals(-2, evaluateLong("roundDown(-1.6)"));
        assertEquals(-2, evaluateLong("roundDown(-1.9)"));
        assertEquals(-3, evaluateLong("roundDown(-2.1)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("roundDown(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'roundDown': Operand must be a number in expression 'roundDown(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("roundDown(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'roundDown': Operand must be a number in expression 'roundDown(true)'", runErr.getMessage());
        // toLong
        assertEquals(0, evaluateLong("toLong(0)"));
        assertEquals(1, evaluateLong("toLong(1)"));
        assertEquals(-1, evaluateLong("toLong(-1)"));
        assertEquals(0, evaluateLong("toLong(0.01)"));
        assertEquals(0, evaluateLong("toLong(0.0)"));
        assertEquals(0, evaluateLong("toLong(-0.01)"));
        assertEquals(0, evaluateLong("toLong(0.99)"));
        assertEquals(1, evaluateLong("toLong(1.0)"));
        assertEquals(1, evaluateLong("toLong(1.01)"));
        assertEquals(0, evaluateLong("toLong(-0.99)"));
        assertEquals(-1, evaluateLong("toLong(-1.0)"));
        assertEquals(-1, evaluateLong("toLong(-1.01)"));
        assertEquals(1, evaluateLong("toLong(1.1)"));
        assertEquals(1, evaluateLong("toLong(1.5)"));
        assertEquals(1, evaluateLong("toLong(1.6)"));
        assertEquals(1, evaluateLong("toLong(1.9)"));
        assertEquals(2, evaluateLong("toLong(2.1)"));
        assertEquals(-1, evaluateLong("toLong(-1.1)"));
        assertEquals(-1, evaluateLong("toLong(-1.5)"));
        assertEquals(-1, evaluateLong("toLong(-1.6)"));
        assertEquals(-1, evaluateLong("toLong(-1.9)"));
        assertEquals(-2, evaluateLong("toLong(-2.1)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toLong(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toLong': Operand must be a number in expression 'toLong(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toLong(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toLong': Operand must be a number in expression 'toLong(true)'", runErr.getMessage());
        // toDouble
        assertEquals(0.0, evaluateDouble("toDouble(0)"), EPS);
        assertEquals(1.0, evaluateDouble("toDouble(1)"), EPS);
        assertEquals(-1.0, evaluateDouble("toDouble(-1)"), EPS);
        assertEquals(0.01, evaluateDouble("toDouble(0.01)"), EPS);
        assertEquals(0.0, evaluateDouble("toDouble(0.0)"), EPS);
        assertEquals(-0.01, evaluateDouble("toDouble(-0.01)"), EPS);
        assertEquals(0.99, evaluateDouble("toDouble(0.99)"), EPS);
        assertEquals(1.0, evaluateDouble("toDouble(1.0)"), EPS);
        assertEquals(1.01, evaluateDouble("toDouble(1.01)"), EPS);
        assertEquals(-0.99, evaluateDouble("toDouble(-0.99)"), EPS);
        assertEquals(-1.0, evaluateDouble("toDouble(-1.0)"), EPS);
        assertEquals(-1.01, evaluateDouble("toDouble(-1.01)"), EPS);
        assertEquals(1.1, evaluateDouble("toDouble(1.1)"), EPS);
        assertEquals(1.5, evaluateDouble("toDouble(1.5)"), EPS);
        assertEquals(1.6, evaluateDouble("toDouble(1.6)"), EPS);
        assertEquals(1.9, evaluateDouble("toDouble(1.9)"), EPS);
        assertEquals(2.1, evaluateDouble("toDouble(2.1)"), EPS);
        assertEquals(-1.1, evaluateDouble("toDouble(-1.1)"), EPS);
        assertEquals(-1.5, evaluateDouble("toDouble(-1.5)"), EPS);
        assertEquals(-1.6, evaluateDouble("toDouble(-1.6)"), EPS);
        assertEquals(-1.9, evaluateDouble("toDouble(-1.9)"), EPS);
        assertEquals(-2.1, evaluateDouble("toDouble(-2.1)"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toDouble(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toDouble': Operand must be a number in expression 'toDouble(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toDouble(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toDouble': Operand must be a number in expression 'toDouble(true)'", runErr.getMessage());
        // ltod
        assertEquals(0.0, evaluateDouble("ltod(0)"), EPS);
        assertEquals(1.0, evaluateDouble("ltod(1000000)"), EPS);
        assertEquals(-1.0, evaluateDouble("ltod(-1000000)"), EPS);
        assertEquals(2.0, evaluateDouble("ltod(2000000)"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("ltod(1000000.0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'ltod': Operand must be a LONG number in expression 'ltod(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("ltod(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'ltod': Operand must be a LONG number in expression 'ltod(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("ltod(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'ltod': Operand must be a LONG number in expression 'ltod(true)'", runErr.getMessage());
        // dtol
        assertEquals(0L, evaluateLong("dtol(0.0)"));
        assertEquals(100_000_000L, evaluateLong("dtol(100.0)"));
        assertEquals(-100_000_000L, evaluateLong("dtol(-100.0)"));
        assertEquals(123_456_000L, evaluateLong("dtol(123.456)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol(1000000)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol(1000000)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("dtol(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'dtol': Operand must be a DOUBLE number in expression 'dtol(true)'", runErr.getMessage());
        // isMarketPrice
        assertTrue(evaluateBool("isMarketPrice(" + OrderPrice.NO_LIMIT_PRICE + ")"));
        assertFalse(evaluateBool("isMarketPrice(" + OrderPrice.INVALID_PRICE + ")"));
        assertFalse(evaluateBool("isMarketPrice(1000000)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(1000000.0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isMarketPrice(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isMarketPrice': Operand must be a LONG number in expression 'isMarketPrice(true)'", runErr.getMessage());
        // isLimitPrice
        assertFalse(evaluateBool("isLimitPrice(" + OrderPrice.NO_LIMIT_PRICE + ")"));
        assertFalse(evaluateBool("isLimitPrice(" + OrderPrice.INVALID_PRICE + ")"));
        assertTrue(evaluateBool("isLimitPrice(1000000)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(1000000.0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isLimitPrice(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isLimitPrice': Operand must be a LONG number in expression 'isLimitPrice(true)'", runErr.getMessage());
        // isValidPrice
        assertTrue(evaluateBool("isValidPrice(" + OrderPrice.NO_LIMIT_PRICE + ")"));
        assertFalse(evaluateBool("isValidPrice(" + OrderPrice.INVALID_PRICE + ")"));
        assertTrue(evaluateBool("isValidPrice(1000000)"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice(1000000.0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice(1000000.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isValidPrice(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isValidPrice': Operand must be a LONG number in expression 'isValidPrice(true)'", runErr.getMessage());

        final MutableExprContext ctx = ExprContextFactory.create();
        ctx.defineFunction("PI", result -> result.accept(3.14));

        assertEquals(1.0, evaluateDouble("min(1, PI())", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(1, PI() + PI())", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(1.0, PI())", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(PI(), 1)", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(PI(), 1.0)", ctx), EPS);

        assertEquals(3.14, evaluateDouble("max(1, PI())", ctx), EPS);
        assertEquals(6.28, evaluateDouble("max(1, PI() + PI())", ctx), EPS);
        assertEquals(3.14, evaluateDouble("max(1.0, PI())", ctx), EPS);
        assertEquals(3.14, evaluateDouble("max(PI(), 1)", ctx), EPS);
        assertEquals(3.14, evaluateDouble("max(PI(), 1.0)", ctx), EPS);

        assertEquals(3.14, evaluateDouble("abs(PI())", ctx), EPS);
        assertEquals(3.14, evaluateDouble("abs(-PI())", ctx), EPS);

        assertEquals(3, evaluateLong("round(PI())", ctx));
        assertEquals(3, evaluateLong("roundToNearest(PI())", ctx));
        assertEquals(4, evaluateLong("roundUp(PI())", ctx));
        assertEquals(3, evaluateLong("roundDown(PI())", ctx));
        assertEquals(3, evaluateLong("toLong(PI())", ctx));
        assertEquals(3.0, evaluateDouble("toDouble(toLong(PI()))", ctx), EPS);
        assertEquals(3.0, evaluateDouble("ltod(toLong(PI()) * 1000000)", ctx), EPS);
        assertEquals(3.0, evaluateDouble("ltod(round(PI()) * 1000000)", ctx), EPS);
        assertEquals(3.0, evaluateDouble("ltod(roundToNearest(PI()) * 1000000)", ctx), EPS);
        assertEquals(4.0, evaluateDouble("ltod(roundUp(PI()) * 1000000)", ctx), EPS);
        assertEquals(3.0, evaluateDouble("ltod(roundDown(PI()) * 1000000)", ctx), EPS);
        assertEquals(3.14, evaluateDouble("ltod(toLong(PI() * 1000000))", ctx), EPS);
        assertEquals(3.14, evaluateDouble("ltod(round(PI() * 1000000))", ctx), EPS);
        assertEquals(3.14, evaluateDouble("ltod(roundToNearest(PI() * 1000000))", ctx), EPS);
        assertEquals(3.14, evaluateDouble("ltod(roundUp(PI() * 1000000))", ctx), EPS);
        assertEquals(3.14, evaluateDouble("ltod(roundDown(PI() * 1000000))", ctx), EPS);
        assertEquals(3_140_000L, evaluateLong("dtol(PI())", ctx));
        assertTrue(evaluateBool("isLimitPrice(dtol(PI()))", ctx));
        assertFalse(evaluateBool("isMarketPrice(dtol(PI()))", ctx));
        assertTrue(evaluateBool("isValidPrice(dtol(PI()))", ctx));
        assertEquals(3, evaluateLong("PI().toLong()", ctx));
        assertEquals(3, evaluateLong("PI().round()", ctx));
        assertEquals(4, evaluateLong("PI().roundUp()", ctx));
        assertEquals(3, evaluateLong("3.14.toLong()", ctx));
        assertEquals(5.0, evaluateDouble("5.toDouble()", ctx), EPS);
    }

    @Test
    void stringFunctionsTest() {
        RuntimeError runErr;

        assertTrue(evaluateBool("\"\".isEmpty()"));
        assertFalse(evaluateBool("not(\"\".isEmpty())"));
        assertFalse(evaluateBool("\"A\".isEmpty()"));
        assertTrue(evaluateBool("not(\"A\".isEmpty())"));

        assertFalse(evaluateBool("isEmpty(\"A\")"));
        assertTrue(evaluateBool("not(isEmpty(\"A\"))"));

        assertTrue(evaluateBool("\"\".length() == 0"));
        assertEquals(0, evaluateLong("\"\".length()"));

        assertTrue(evaluateBool("\"ABC\".length() == 3"));
        assertEquals(3, evaluateLong("\"ABC\".length()"));

        assertTrue(evaluateBool("length(\"ABC\") == 3"));
        assertEquals(3, evaluateLong("length(\"ABC\")"));

        assertTrue(evaluateBool("\"ABC\".contains(\"ABC\")"));
        assertFalse(evaluateBool("\"ABC\".contains(\"abc\")"));
        assertFalse(evaluateBool("\"abc\".contains(\"ABC\")"));
        assertTrue(evaluateBool("\"ABC\".contains(\"\")"));
        assertTrue(evaluateBool("\"abc\".contains(\"\")"));
        assertFalse(evaluateBool("\"\".contains(\"abc\")"));
        assertFalse(evaluateBool("\"\".contains(\"ABC\")"));

        assertTrue(evaluateBool("contains(\"ABC\", \"ABC\")"));
        assertFalse(evaluateBool("contains(\"ABC\", \"abc\")"));
        assertFalse(evaluateBool("contains(\"abc\", \"ABC\")"));
        assertTrue(evaluateBool("contains(\"ABC\", \"\")"));
        assertTrue(evaluateBool("contains(\"abc\", \"\")"));
        assertFalse(evaluateBool("contains(\"\", \"abc\")"));
        assertFalse(evaluateBool("contains(\"\", \"ABC\")"));

        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(1)"));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING in expression '\"ABC\".contains(1)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(1.0)"));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING in expression '\"ABC\".contains(1.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(true)"));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING in expression '\"ABC\".contains(true)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("1.contains(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 3]: RuntimeException in function 'contains': Operand must be a STRING in expression '1.contains(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true.contains(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 6]: RuntimeException in function 'contains': Operand must be a STRING in expression 'true.contains(\"A\")'", runErr.getMessage());

        final MutableExprContext ctx = ExprContextFactory.create();
        ctx.defineString("region", () -> "EMEA");
        ctx.defineFunction("algoType", result -> result.accept("Algo1"));

        assertFalse(evaluateBool("algoType().isEmpty()", ctx));
        assertFalse(evaluateBool("region.isEmpty()", ctx));
        assertTrue(evaluateBool("algoType().length() == 5", ctx));
        assertTrue(evaluateBool("region.length() == 4", ctx));

        final MutableExprContext ctx2 = ExprContextFactory.create();
        ctx2.defineByteBuffer("region", () -> constant("EMEA"));
        ctx2.defineFunction("algoType", result -> result.accept(constant("Algo1")));

        assertFalse(evaluateBool("algoType().isEmpty()", ctx2));
        assertFalse(evaluateBool("region.isEmpty()", ctx2));
        assertTrue(evaluateBool("algoType().length() == 5", ctx2));
        assertTrue(evaluateBool("region.length() == 4", ctx2));

        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(region)", ctx2));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING in expression '\"ABC\".contains(region)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(algoType())", ctx2));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING in expression '\"ABC\".contains(algoType())'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("region.contains(\"A\")", ctx2));
        assertEquals("Expression evaluation error [line 1, pos 8]: RuntimeException in function 'contains': Operand must be a STRING in expression 'region.contains(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("algoType().contains(\"A\")", ctx2));
        assertEquals("Expression evaluation error [line 1, pos 12]: RuntimeException in function 'contains': Operand must be a STRING in expression 'algoType().contains(\"A\")'", runErr.getMessage());

        final MutableExprContext ctx3 = ExprContextFactory.create();
        ctx3.defineString("region", () -> "EMEA");
        ctx3.defineFunction("algoType", result -> result.accept(constant("Algo1")));

        assertFalse(evaluateBool("algoType().isEmpty()", ctx3));
        assertFalse(evaluateBool("region.isEmpty()", ctx3));
        assertTrue(evaluateBool("algoType().length() == 5", ctx3));
        assertTrue(evaluateBool("region.length() == 4", ctx3));
    }
}