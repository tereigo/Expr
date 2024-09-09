package com.tereigo.expr;

import org.junit.jupiter.api.Test;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
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
        runErr = assertThrows(RuntimeError.class, () -> evaluate("roundDown('A')"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'roundDown': Operand must be a number in expression 'roundDown('A')'", runErr.getMessage());
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
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toLong('A')"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toLong': Operand must be a number in expression 'toLong('A')'", runErr.getMessage());
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
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toDouble('A')"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toDouble': Operand must be a number in expression 'toDouble('A')'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toDouble(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toDouble': Operand must be a number in expression 'toDouble(true)'", runErr.getMessage());

        final MutableExprContext ctx = ExprContextFactory.createGlobalContext();

        assertEquals(1.0, evaluateDouble("min(1, PI)", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(1, PI + PI)", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(1.0, PI)", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(PI, 1)", ctx), EPS);
        assertEquals(1.0, evaluateDouble("min(PI, 1.0)", ctx), EPS);

        assertEquals(3.141592653589793, evaluateDouble("max(1, PI)", ctx), EPS);
        assertEquals(3.141592653589793 * 2, evaluateDouble("max(1, PI + PI)", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("max(1.0, PI)", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("max(PI, 1)", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("max(PI, 1.0)", ctx), EPS);

        assertEquals(3.141592653589793, evaluateDouble("abs(PI)", ctx), EPS);
        assertEquals(3.141592653589793, evaluateDouble("abs(-PI)", ctx), EPS);

        assertEquals(3.141592653589793, evaluateDouble("pi", ctx), EPS);

        assertEquals(2.718281828459045, evaluateDouble("E", ctx), EPS);
        assertEquals(2.718281828459045, evaluateDouble("e", ctx), EPS);

        assertEquals(3, evaluateLong("round(PI)", ctx));
        assertEquals(3, evaluateLong("roundToNearest(PI)", ctx));
        assertEquals(4, evaluateLong("roundUp(PI)", ctx));
        assertEquals(3, evaluateLong("roundDown(PI)", ctx));
        assertEquals(3, evaluateLong("toLong(PI)", ctx));
        assertEquals(3.0, evaluateDouble("toDouble(toLong(PI))", ctx), EPS);
        assertEquals(3, evaluateLong("PI.toLong()", ctx));
        assertEquals(3, evaluateLong("PI.round()", ctx));
        assertEquals(4, evaluateLong("PI.roundUp()", ctx));
        assertEquals(3, evaluateLong("3.14.toLong()", ctx));
        assertEquals(5.0, evaluateDouble("5.toDouble()", ctx), EPS);
        assertEquals(5.0, evaluateDouble("5.0.toDouble()", ctx), EPS);
    }

    @Test
    void stringFunctionsTest() {
        RuntimeError runErr;

        assertTrue(evaluateBool("\"\".isEmpty()"));
        assertFalse(evaluateBool("not(\"\".isEmpty())"));
        assertFalse(evaluateBool("\"A\".isEmpty()"));
        assertTrue(evaluateBool("not(\"A\".isEmpty())"));
        assertTrue(evaluateBool("not('A'.isEmpty())"));

        assertFalse(evaluateBool("isEmpty(\"A\")"));
        assertTrue(evaluateBool("not(isEmpty(\"A\"))"));

        assertTrue(evaluateBool("\"\".length() == 0"));
        assertEquals(0, evaluateLong("\"\".length()"));

        assertTrue(evaluateBool("\"ABC\".length() == 3"));
        assertEquals(3, evaluateLong("\"ABC\".length()"));

        assertTrue(evaluateBool("length(\"ABC\") == 3"));
        assertTrue(evaluateBool("length('ABC') == 3"));
        assertEquals(3, evaluateLong("length(\"ABC\")"));

        assertTrue(evaluateBool("\"ABC\".contains(\"ABC\")"));
        assertFalse(evaluateBool("\"ABC\".contains(\"abc\")"));
        assertFalse(evaluateBool("\"abc\".contains(\"ABC\")"));
        assertTrue(evaluateBool("\"ABC\".contains(\"\")"));
        assertTrue(evaluateBool("\"abc\".contains(\"\")"));
        assertFalse(evaluateBool("\"\".contains(\"abc\")"));
        assertFalse(evaluateBool("\"\".contains(\"ABC\")"));

        assertTrue(evaluateBool("contains('ABC', 'ABC')"));
        assertTrue(evaluateBool("contains(\"ABC\", \"ABC\")"));
        assertFalse(evaluateBool("contains(\"ABC\", \"abc\")"));
        assertFalse(evaluateBool("contains(\"abc\", \"ABC\")"));
        assertTrue(evaluateBool("contains(\"ABC\", \"\")"));
        assertTrue(evaluateBool("contains(\"abc\", \"\")"));
        assertFalse(evaluateBool("contains(\"\", \"abc\")"));
        assertFalse(evaluateBool("contains(\"\", \"ABC\")"));

        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(1)"));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING or BYTE_BUFFER in expression '\"ABC\".contains(1)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(1.0)"));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING or BYTE_BUFFER in expression '\"ABC\".contains(1.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(true)"));
        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING or BYTE_BUFFER in expression '\"ABC\".contains(true)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("1.contains(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 3]: RuntimeException in function 'contains': Operand must be a STRING or BYTE_BUFFER in expression '1.contains(\"A\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true.contains(\"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 6]: RuntimeException in function 'contains': Operand must be a STRING or BYTE_BUFFER in expression 'true.contains(\"A\")'", runErr.getMessage());

        final MutableExprContext ctx = ExprContextFactory.createGlobalContext();
        ctx.defineString("region", () -> "EMEA");
        ctx.defineFunction("algoType", result -> result.accept("Algo1"));

        assertFalse(evaluateBool("algoType().isEmpty()", ctx));
        assertFalse(evaluateBool("region.isEmpty()", ctx));
        assertTrue(evaluateBool("algoType().length() == 5", ctx));
        assertTrue(evaluateBool("region.length() == 4", ctx));

        final MutableExprContext ctx2 = ExprContextFactory.createGlobalContext();
        ctx2.defineByteBuffer("region", () -> constant("EMEA"));
        ctx2.defineFunction("algoType", result -> result.accept(constant("Algo1")));

        assertFalse(evaluateBool("algoType().isEmpty()", ctx2));
        assertFalse(evaluateBool("region.isEmpty()", ctx2));
        assertTrue(evaluateBool("algoType().length() == 5", ctx2));
        assertTrue(evaluateBool("region.length() == 4", ctx2));

        // TODO: implement ByteBufferUtils.contains() for all combinations
//        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(region)", ctx2));
//        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING in expression '\"ABC\".contains(region)'", runErr.getMessage());
//        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\".contains(algoType())", ctx2));
//        assertEquals("Expression evaluation error [line 1, pos 7]: RuntimeException in function 'contains': Operand must be a STRING in expression '\"ABC\".contains(algoType())'", runErr.getMessage());
//        runErr = assertThrows(RuntimeError.class, () -> evaluate("region.contains(\"A\")", ctx2));
//        assertEquals("Expression evaluation error [line 1, pos 8]: RuntimeException in function 'contains': Operand must be a STRING in expression 'region.contains(\"A\")'", runErr.getMessage());
//        runErr = assertThrows(RuntimeError.class, () -> evaluate("algoType().contains(\"A\")", ctx2));
//        assertEquals("Expression evaluation error [line 1, pos 12]: RuntimeException in function 'contains': Operand must be a STRING in expression 'algoType().contains(\"A\")'", runErr.getMessage());

        final MutableExprContext ctx3 = ExprContextFactory.createGlobalContext();
        ctx3.defineString("region", () -> "EMEA");
        ctx3.defineByteBuffer("country", () -> constant("Italy"));
        ctx3.defineFunction("algoType", result -> result.accept(constant("Algo1")));

        assertFalse(evaluateBool("algoType().isEmpty()", ctx3));
        assertFalse(evaluateBool("algoType.isEmpty()", ctx3));
        assertFalse(evaluateBool("region.isEmpty()", ctx3));
        assertFalse(evaluateBool("country().isEmpty()", ctx3));
        assertFalse(evaluateBool("country.isEmpty()", ctx3));
        assertTrue(evaluateBool("algoType().length() == 5", ctx3));
        assertTrue(evaluateBool("algoType.length() == 5", ctx3));
        assertTrue(evaluateBool("region.length() == 4", ctx3));
        assertTrue(evaluateBool("region.length == 4", ctx3));
        assertTrue(evaluateBool("length(region) == 4", ctx3));
        assertTrue(evaluateBool("country.length() == 5", ctx3));
        assertTrue(evaluateBool("country.length == 5", ctx3));
        assertTrue(evaluateBool("length(country) == 5", ctx3));
        assertTrue(evaluateBool("region.contains('A')", ctx3));
        assertTrue(evaluateBool("contains(region, 'A')", ctx3));
        // TODO: implement
        //assertFalse(evaluateBool("country.contains('ly')", ctx3));
        // TODO: implement
        //assertFalse(evaluateBool("contains(country, 'ly')", ctx3));

        // Try the same with optimized AST
        assertFalse(evaluateBoolOptimized(ctx3, "algoType().isEmpty()"));
        assertFalse(evaluateBoolOptimized(ctx3, "algoType.isEmpty()"));
        assertFalse(evaluateBoolOptimized(ctx3, "region.isEmpty()"));
        assertFalse(evaluateBoolOptimized(ctx3, "country().isEmpty()"));
        assertFalse(evaluateBoolOptimized(ctx3, "country.isEmpty()"));
        assertTrue(evaluateBoolOptimized(ctx3, "algoType().length() == 5"));
        assertTrue(evaluateBoolOptimized(ctx3, "algoType.length() == 5"));
        assertTrue(evaluateBoolOptimized(ctx3, "region.length() == 4"));
        assertTrue(evaluateBoolOptimized(ctx3, "region.length == 4"));
        assertTrue(evaluateBoolOptimized(ctx3, "length(region) == 4"));
        assertTrue(evaluateBoolOptimized(ctx3, "country.length() == 5"));
        assertTrue(evaluateBoolOptimized(ctx3, "country.length == 5"));
        assertTrue(evaluateBoolOptimized(ctx3, "length(country) == 5"));
        assertTrue(evaluateBoolOptimized(ctx3, "region.contains('A')"));
        assertTrue(evaluateBoolOptimized(ctx3, "contains(region, 'A')"));
        // TODO: implement
        //assertFalse(evaluateBoolOptimized("country.contains('ly')", ctx3));
        // TODO: implement
        //assertFalse(evaluateBoolOptimized("contains(country, 'ly')", ctx3));
    }
}