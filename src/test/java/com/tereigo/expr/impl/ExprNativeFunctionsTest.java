package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import org.junit.jupiter.api.Test;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExprNativeFunctionsTest extends ExprEvaluatorTestBase {

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
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toDouble': Variant type mismatch: STRING, expected: LONG or DOUBLE in expression 'toDouble('A')'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("toDouble(true)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'toDouble': Variant type mismatch: BOOL, expected: LONG or DOUBLE in expression 'toDouble(true)'", runErr.getMessage());

        final ExprContext ctx = ExprContextFactory.globalContext().getAsExprContext();

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
    void mathFunctionsTest() {
        assertEquals(Math.sin(0.5), evaluateDouble("sin(0.5)"), EPS);
        assertEquals(Math.cos(0.5), evaluateDouble("cos(0.5)"), EPS);
        assertEquals(Math.tan(0.5), evaluateDouble("tan(0.5)"), EPS);
        assertEquals(Math.asin(0.5), evaluateDouble("asin(0.5)"), EPS);
        assertEquals(Math.acos(0.5), evaluateDouble("acos(0.5)"), EPS);
        assertEquals(Math.atan(0.5), evaluateDouble("atan(0.5)"), EPS);
        assertEquals(Math.sinh(0.5), evaluateDouble("sinh(0.5)"), EPS);
        assertEquals(Math.cosh(0.5), evaluateDouble("cosh(0.5)"), EPS);
        assertEquals(Math.tanh(0.5), evaluateDouble("tanh(0.5)"), EPS);
        assertEquals(Math.hypot(3.0, 4.0), evaluateDouble("hypot(3.0, 4.0)"), EPS);
        assertEquals(Math.toRadians(180.0), evaluateDouble("toRadians(180.0)"), EPS);
        assertEquals(Math.toDegrees(Math.PI), evaluateDouble("toDegrees(PI)"), EPS);
        assertEquals(Math.exp(2.0), evaluateDouble("exp(2.0)"), EPS);
        assertEquals(Math.expm1(2.0), evaluateDouble("expm1(2.0)"), EPS);
        assertEquals(Math.log(2.0), evaluateDouble("log(2.0)"), EPS);
        assertEquals(Math.log10(2.0), evaluateDouble("log10(2.0)"), EPS);
        assertEquals(Math.log1p(2.0), evaluateDouble("log1p(2.0)"), EPS);
        assertEquals(Math.sqrt(2.0), evaluateDouble("sqrt(2.0)"), EPS);
        assertEquals(Math.cbrt(2.0), evaluateDouble("cbrt(2.0)"), EPS);
        assertEquals(Math.ceil(2.1), evaluateDouble("ceil(2.1)"), EPS);
        assertEquals(Math.floor(2.9), evaluateDouble("floor(2.9)"), EPS);
        assertEquals(Math.rint(2.5), evaluateDouble("rint(2.5)"), EPS);
        assertEquals(Math.atan2(3.0, 4.0), evaluateDouble("atan2(3.0, 4.0)"), EPS);
        assertEquals(Math.pow(2.0, 10.0), evaluateDouble("pow(2.0, 10.0)"), EPS);
        assertEquals(Math.signum(-2.0), evaluateDouble("signum(-2.0)"), EPS);
        assertEquals(Math.signum(2.0), evaluateDouble("signum(2.0)"), EPS);
        assertEquals(Math.getExponent(8.0), evaluateLong("getExponent(8.0)"));
        assertEquals(Math.scalb(2.0, 3), evaluateDouble("scalb(2.0, 3)"), EPS);

        final double random = evaluateDouble("random()");
        assertTrue(random >= 0.0 && random < 1.0);

        assertEquals(50.0, evaluateDouble("percentOf(5, 1000)"), EPS);
        assertEquals(50.0, evaluateDouble("5.pctOf(1000)"), EPS);
        assertEquals(50.0, evaluateDouble("pctOf(5, 1000)"), EPS);
    }

    @Test
    void stringEqualsFunctionsTest() {
        // equals(): all 4 combinations of STRING/BYTE_BUFFER operands
        assertTrue(evaluateBool("equals(\"ABC\", \"ABC\")"));
        assertFalse(evaluateBool("equals(\"ABC\", \"abc\")"));
        assertTrue(evaluateBool("\"ABC\".equals(\"ABC\")"));
        assertTrue(evaluateBool("equals('ABC', bufABC())", byteBufferCtx()));
        assertFalse(evaluateBool("equals('abc', bufABC())", byteBufferCtx()));
        assertTrue(evaluateBool("equals(bufABC(), 'ABC')", byteBufferCtx()));
        assertFalse(evaluateBool("equals(bufABC(), 'abc')", byteBufferCtx()));
        assertTrue(evaluateBool("equals(bufABC(), bufABC())", byteBufferCtx()));

        RuntimeError runErr = assertThrows(RuntimeError.class, () -> evaluate("equals(1, \"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'equals': Operands must be a STRING or BYTE_BUFFER in expression 'equals(1, \"A\")'", runErr.getMessage());

        // equalsIgnoreCase(): all 4 combinations
        assertTrue(evaluateBool("equalsIgnoreCase(\"ABC\", \"abc\")"));
        assertFalse(evaluateBool("equalsIgnoreCase(\"ABC\", \"abd\")"));
        assertTrue(evaluateBool("equalsIgnoreCase('abc', bufABC())", byteBufferCtx()));
        assertTrue(evaluateBool("equalsIgnoreCase(bufABC(), 'abc')", byteBufferCtx()));
        assertTrue(evaluateBool("equalsIgnoreCase(bufABC(), bufABC())", byteBufferCtx()));

        runErr = assertThrows(RuntimeError.class, () -> evaluate("equalsIgnoreCase(1, \"A\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'equalsIgnoreCase': Operand must be a STRING or BYTE_BUFFER in expression 'equalsIgnoreCase(1, \"A\")'", runErr.getMessage());
    }

    @Test
    void stringStartsEndsWithFunctionsTest() {
        final ExprContext ctx = byteBufferCtx();

        // startsWith() / startsWithIgnoreCase(): all 4 combinations
        assertTrue(evaluateBool("startsWith(\"ABC\", \"AB\")"));
        assertFalse(evaluateBool("startsWith(\"ABC\", \"ab\")"));
        assertTrue(evaluateBool("startsWith('ABC', bufAB())", ctx));
        assertTrue(evaluateBool("startsWith(bufABC(), 'AB')", ctx));
        assertTrue(evaluateBool("startsWith(bufABC(), bufAB())", ctx));

        assertTrue(evaluateBool("startsWithIgnoreCase(\"ABC\", \"ab\")"));
        assertTrue(evaluateBool("startsWithIgnoreCase('ABC', bufAB())", ctx));
        assertTrue(evaluateBool("startsWithIgnoreCase(bufABC(), 'ab')", ctx));
        assertTrue(evaluateBool("startsWithIgnoreCase(bufABC(), bufAB())", ctx));

        // endsWith() / endsWithIgnoreCase(): all 4 combinations
        assertTrue(evaluateBool("endsWith(\"ABC\", \"BC\")"));
        assertFalse(evaluateBool("endsWith(\"ABC\", \"bc\")"));
        assertTrue(evaluateBool("endsWith('ABC', bufBC())", ctx));
        assertTrue(evaluateBool("endsWith(bufABC(), 'BC')", ctx));
        assertTrue(evaluateBool("endsWith(bufABC(), bufBC())", ctx));

        assertTrue(evaluateBool("endsWithIgnoreCase(\"ABC\", \"bc\")"));
        assertTrue(evaluateBool("endsWithIgnoreCase('ABC', bufBC())", ctx));
        assertTrue(evaluateBool("endsWithIgnoreCase(bufABC(), 'bc')", ctx));
        assertTrue(evaluateBool("endsWithIgnoreCase(bufABC(), bufBC())", ctx));
    }

    @Test
    void stringIndexOfContainsIgnoreCaseFunctionsTest() {
        final ExprContext ctx = byteBufferCtx();

        // indexOf() / indexOfIgnoreCase(): all 4 combinations
        assertEquals(1, evaluateLong("indexOf(\"ABC\", \"BC\")"));
        assertEquals(1, evaluateLong("indexOf('ABC', bufBC())", ctx));
        assertEquals(1, evaluateLong("indexOf(bufABC(), 'BC')", ctx));
        assertEquals(1, evaluateLong("indexOf(bufABC(), bufBC())", ctx));

        assertEquals(1, evaluateLong("indexOfIgnoreCase(\"ABC\", \"bc\")"));
        assertEquals(1, evaluateLong("indexOfIgnoreCase('ABC', bufBC())", ctx));
        assertEquals(1, evaluateLong("indexOfIgnoreCase(bufABC(), 'bc')", ctx));
        assertEquals(1, evaluateLong("indexOfIgnoreCase(bufABC(), bufBC())", ctx));

        // containsIgnoreCase(): all 4 combinations (contains() with STRING/STRING and STRING/BYTE_BUFFER etc already tested elsewhere)
        assertTrue(evaluateBool("containsIgnoreCase(\"ABC\", \"bc\")"));
        assertTrue(evaluateBool("containsIgnoreCase('ABC', bufBC())", ctx));
        assertTrue(evaluateBool("containsIgnoreCase(bufABC(), 'bc')", ctx));
        assertTrue(evaluateBool("containsIgnoreCase(bufABC(), bufBC())", ctx));

        final RuntimeError runErr = assertThrows(RuntimeError.class, () -> evaluate("length(1)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'length': Operand must be a STRING or BYTE_BUFFER in expression 'length(1)'", runErr.getMessage());
    }

    @Test
    void stringFunctionsTypeErrorsTest() {
        final ExprContext ctx = byteBufferCtx();

        assertThrows(RuntimeError.class, () -> evaluate("isEmpty(1)"));

        // contains(): all combinations that report a type error, plus the previously-uncovered ByteBuffer/ByteBuffer success case
        assertTrue(evaluateBool("contains(bufABC(), bufBC())", ctx));
        assertThrows(RuntimeError.class, () -> evaluate("contains(1, \"A\")"));
        assertThrows(RuntimeError.class, () -> evaluate("contains(\"A\", 1)"));
        assertThrows(RuntimeError.class, () -> evaluateBool("contains(bufABC(), 1)", ctx));

        assertThrows(RuntimeError.class, () -> evaluate("containsIgnoreCase(1, \"A\")"));
        assertThrows(RuntimeError.class, () -> evaluate("startsWith(1, \"A\")"));
        assertThrows(RuntimeError.class, () -> evaluate("startsWithIgnoreCase(1, \"A\")"));
        assertThrows(RuntimeError.class, () -> evaluate("endsWith(1, \"A\")"));
        assertThrows(RuntimeError.class, () -> evaluate("endsWithIgnoreCase(1, \"A\")"));

        assertThrows(RuntimeError.class, () -> evaluate("indexOf(1, \"A\")"));
        assertThrows(RuntimeError.class, () -> evaluate("indexOf(\"A\", 1)"));
        assertThrows(RuntimeError.class, () -> evaluateBool("indexOf(bufABC(), 1) >= 0", ctx));
        assertThrows(RuntimeError.class, () -> evaluate("indexOfIgnoreCase(1, \"A\")"));
    }

    private static ExprContext byteBufferCtx() {
        final ExprContextBuilder ctx = ExprContextFactory.globalContext();
        ctx.addFunction("bufABC", result -> result.accept(constant("ABC")));
        ctx.addFunction("bufAB", result -> result.accept(constant("AB")));
        ctx.addFunction("bufBC", result -> result.accept(constant("BC")));
        return ctx.getAsExprContext();
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

        final ExprContextBuilder mutCtx = ExprContextFactory.globalContext();
        mutCtx.addString("region", () -> "EMEA");
        mutCtx.addFunction("algoType", result -> result.accept("Algo1"));

        final ExprContext ctx = mutCtx.getAsExprContext();

        assertFalse(evaluateBool("algoType().isEmpty()", ctx));
        assertFalse(evaluateBool("region.isEmpty()", ctx));
        assertTrue(evaluateBool("algoType().length() == 5", ctx));
        assertTrue(evaluateBool("region.length() == 4", ctx));

        final ExprContextBuilder mutCtx2 = ExprContextFactory.globalContext();
        mutCtx2.addByteBuffer("region", () -> constant("EMEA"));
        mutCtx2.addFunction("algoType", result -> result.accept(constant("Algo1")));

        final ExprContext ctx2 = mutCtx2.getAsExprContext();

        assertFalse(evaluateBool("algoType().isEmpty()", ctx2));
        assertFalse(evaluateBool("region.isEmpty()", ctx2));
        assertTrue(evaluateBool("algoType().length() == 5", ctx2));
        assertTrue(evaluateBool("region.length() == 4", ctx2));
        assertFalse(evaluateBool("\"ABC\".contains(region)", ctx2));
        assertFalse(evaluateBool("\"ABC\".contains(algoType())", ctx2));
        assertTrue(evaluateBool("region.contains(\"A\")", ctx2));
        assertTrue(evaluateBool("algoType().contains(\"A\")", ctx2));
        assertFalse(evaluateBool("'ABC'.contains(region)", ctx2));
        assertFalse(evaluateBool("'ABC'.contains(algoType())", ctx2));
        assertFalse(evaluateBool("'ABC'.contains(algoType)", ctx2));
        assertTrue(evaluateBool("region.contains('A')", ctx2));
        assertTrue(evaluateBool("algoType().contains('A')", ctx2));
        assertTrue(evaluateBool("algoType.contains('A')", ctx2));

        final ExprContextBuilder mutCtx3 = ExprContextFactory.globalContext();
        mutCtx3.addString("region", () -> "EMEA");
        mutCtx3.addByteBuffer("country", () -> constant("Italy"));
        mutCtx3.addFunction("algoType", result -> result.accept(constant("Algo1")));

        final ExprContext ctx3 = mutCtx3.getAsExprContext();

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
        assertTrue(evaluateBool("country.contains('ly')", ctx3));
        assertTrue(evaluateBool("contains(country, 'ly')", ctx3));

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
        assertTrue(evaluateBoolOptimized(ctx3, "country.contains('ly')"));
        assertTrue(evaluateBoolOptimized(ctx3, "contains(country, 'ly')"));
    }
}