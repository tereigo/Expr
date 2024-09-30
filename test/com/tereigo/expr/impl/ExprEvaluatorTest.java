package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.utils.ByteBufferUtils;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprEvaluatorTest extends ExprEvaluatorTestBase {

    @Test
    void conditionTests() {
        ParseError err;
        final RuntimeError runErr;

        assertEquals(1, evaluateLong("true ? 1 : 2"));
        assertEquals(2, evaluateLong("false ? 1 : 2"));
        assertEquals(1, evaluateLong("0 == 0 ? 1 : 2"));
        assertEquals(2, evaluateLong("0 == 1 ? 1 : 2"));
        assertEquals(9, evaluateLong("5 + (1 == 2 ? 3 : 4)"));
        assertEquals(9, evaluateLong("5 + ((1 == 2) ? 3 : 4)"));
        assertEquals(8, evaluateLong("5 + ((1 == 1) ? 3 : 4)"));
        assertFalse(evaluateBool("0 == 1 ? true : false"));
        assertTrue(evaluateBool("1 == 1 ? true : false"));
        // nested ternary operators
        assertEquals(5, evaluateLong("true ? 1 == 1 ? 5 : 6 : 2"));
        assertEquals(1, evaluateLong("true ? 1 : false"));
        // returning different types
        assertFalse(evaluateBool("false ? 1 : false"));
        assertEquals(1, evaluateLong("0 == 0 ? 1 : 2"));
        // with parens
        assertEquals(2, evaluateLong("(0 == 1) ? 1 : 2"));
        assertEquals(2, evaluateLong("(0 == 1) ? (1) : (2)"));
        assertEquals(2, evaluateLong("(0 == 1) ? (1 + 3) : (2 + 0)"));
        assertEquals(2, evaluateLong("(0 == 1) ? (1 + 3) : 2 + 0"));

        err = assertThrows(ParseError.class, () -> evaluate("(0 == 1) ? (1 + 3 : 2 + 0)"));
        assertEquals("Expression parsing error [line 1, pos 19]: Expect ')' after expression in expression '(0 == 1) ? (1 + 3 : 2 + 0)'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("true ? 1 ; 2"));
        assertEquals("Expression parsing error [line 1, pos 10]: Unexpected character in expression 'true ? 1 ; 2'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("true ? 1 . 2"));
        assertEquals("Expression parsing error [line 1, pos 12]: Expect function name after '.' in expression 'true ? 1 . 2'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("true ? 1 , 2"));
        assertEquals("Expression parsing error [line 1, pos 10]: Expect ':' after ternary ('?') operator in expression 'true ? 1 , 2'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("true ? 1 abc 2"));
        assertEquals("Expression parsing error [line 1, pos 10]: Expect ':' after ternary ('?') operator in expression 'true ? 1 abc 2'", err.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("1 ? 1 : 2"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operand must be a boolean in expression '1 ? 1 : 2'", runErr.getMessage());
    }

    @Test
    void simpleLongTest() {
        assertEquals(3, evaluateLong("1+2"));
    }

    @Test
    void longTestWithContext() {
        final ExprContext ctx = ExprContextFactory.globalContext()
                .addLong("qty", () -> 5)
                .getAsExprContext();
        assertEquals(3, evaluateLong("qty - 2", ctx));
        assertEquals(7, evaluateLong("qty + 2", ctx));
        assertEquals(3, evaluateLong("qty + -2", ctx));
        assertEquals(3, evaluateLong("qty +-2", ctx));
        assertEquals(-3, evaluateLong("-qty + 2", ctx));
        assertEquals(-7, evaluateLong("-qty - 2", ctx));
        assertEquals(-7, evaluateLong("-qty + -2", ctx));
        assertEquals(7, evaluateLong("2 + qty", ctx));
        assertEquals(-3, evaluateLong("2 + -qty", ctx));
        assertEquals(-3, evaluateLong("2 +-qty", ctx));
        assertEquals(-3, evaluateLong("2 + (-qty)", ctx));
    }

    @Test
    void longOptimizedTestWithContext() {
        final ExprContext ctx = ExprContextFactory.globalContext()
                .addLong("qty", () -> 5)
                .getAsExprContext();
        assertEquals(3, evaluateLongOptimized(ctx, "qty - 2"));
        assertEquals(7, evaluateLongOptimized(ctx, "qty + 2"));
        assertEquals(3, evaluateLongOptimized(ctx, "qty + -2"));
        assertEquals(3, evaluateLongOptimized(ctx, "qty +-2"));
        assertEquals(-3, evaluateLongOptimized(ctx, "-qty + 2"));
        assertEquals(-7, evaluateLongOptimized(ctx, "-qty - 2"));
        assertEquals(-7, evaluateLongOptimized(ctx, "-qty + -2"));
        assertEquals(7, evaluateLongOptimized(ctx, "2 + qty"));
        assertEquals(-3, evaluateLongOptimized(ctx, "2 + -qty"));
        assertEquals(-3, evaluateLongOptimized(ctx, "2 +-qty"));
        assertEquals(-3, evaluateLongOptimized(ctx, "2 + (-qty)"));
    }

    @Test
    void simpleDoubleTest() {
        assertEquals(-2.0, evaluateDouble("-2.0"), EPS);
        assertEquals(1.0, evaluateDouble("1.0"), EPS);
        assertEquals(3.0, evaluateDouble("(1.0+2.0)"), EPS);
    }

    @Test
    void doubleTestWithContext() {
        final ExprContext ctx = ExprContextFactory.globalContext()
                .addDouble("qty", () -> 5.0)
                .getAsExprContext();
        assertEquals(3.0, evaluateDouble("qty - 2.0", ctx), EPS);
        assertEquals(7.0, evaluateDouble("qty + 2.0", ctx), EPS);
        assertEquals(3.0, evaluateDouble("qty + -2.0", ctx), EPS);
        assertEquals(3.0, evaluateDouble("qty +-2.0", ctx), EPS);
        assertEquals(-3.0, evaluateDouble("-qty + 2.0", ctx), EPS);
        assertEquals(-7.0, evaluateDouble("-qty - 2.0", ctx), EPS);
        assertEquals(-7.0, evaluateDouble("-qty + -2.0", ctx), EPS);
        assertEquals(7.0, evaluateDouble("2.0 + qty", ctx), EPS);
        assertEquals(-3.0, evaluateDouble("2.0 + -qty", ctx), EPS);
        assertEquals(-3.0, evaluateDouble("2.0 +-qty", ctx), EPS);
        assertEquals(-3.0, evaluateDouble("2.0 + (-qty)", ctx), EPS);
    }

    @Test
    void doubleOptimizedTestWithContext() {
        final ExprContext ctx = ExprContextFactory.globalContext()
                .addDouble("qty", () -> 5.0)
                .getAsExprContext();
        assertEquals(3.0, evaluateDoubleOptimized(ctx, "qty - 2.0"), EPS);
        assertEquals(7.0, evaluateDoubleOptimized(ctx, "qty + 2.0"), EPS);
        assertEquals(3.0, evaluateDoubleOptimized(ctx, "qty + -2.0"), EPS);
        assertEquals(3.0, evaluateDoubleOptimized(ctx, "qty +-2.0"), EPS);
        assertEquals(-3.0, evaluateDoubleOptimized(ctx, "-qty + 2.0"), EPS);
        assertEquals(-7.0, evaluateDoubleOptimized(ctx, "-qty - 2.0"), EPS);
        assertEquals(-7.0, evaluateDoubleOptimized(ctx, "-qty + -2.0"), EPS);
        assertEquals(7.0, evaluateDoubleOptimized(ctx, "2.0 + qty"), EPS);
        assertEquals(-3.0, evaluateDoubleOptimized(ctx, "2.0 + -qty"), EPS);
        assertEquals(-3.0, evaluateDoubleOptimized(ctx, "2.0 +-qty"), EPS);
        assertEquals(-3.0, evaluateDoubleOptimized(ctx, "2.0 + (-qty)"), EPS);
    }

    @Test
    void simpleDoubleOptimizedTest() {
        final ExprContext ctx = ExprContextFactory.globalContext().getAsExprContext();
        assertEquals(-2.0, evaluateDoubleOptimized(ctx, "-2.0"), EPS);
        assertEquals(1.0, evaluateDoubleOptimized(ctx, "1.0"), EPS);
        assertEquals(3.0, evaluateDoubleOptimized(ctx, "(1.0+2.0)"), EPS);
    }

    @Test
    void simpleBoolTest() {
        assertTrue(evaluateBool("8 % 3 == 2"));
    }

    @Test
    void rerunningTheSameExpressionDouble() {
        final ASTRoot expr = ExprCompiler.compile("(1.0+2.0)");
        assertEquals(3.0, evaluateDouble(expr), EPS);
        assertEquals(3.0, evaluateDouble(expr), EPS);
        assertEquals(3.0, evaluateDouble(expr), EPS);
    }

    @Test
    void rerunningTheSameExpressionLong() {
        final ASTRoot expr = ExprCompiler.compile("(1+2)-(5)+(8+5)");
        assertEquals(11, evaluateLong(expr));
        assertEquals(11, evaluateLong(expr));
        assertEquals(11, evaluateLong(expr));
    }

    @Test
    void rerunningTheSameExpressionBool() {
        final ASTRoot expr = ExprCompiler.compile("not(true) or not (false) and ((true and not(false)) or 5 != 2)");
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
    }

    @Test
    void rerunningTheSameExpressionString() {
        final ASTRoot expr = ExprCompiler.compile("\"A\" != \"BC\" and \"C\" != \"D\"");
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
    }

    @Test
    void longTests() {
        assertEquals(2, evaluateLong("1+1"));
        assertEquals(2, evaluateLong(" 1  + 1  "));
        assertEquals(3, evaluateLong("1+2"));
        assertEquals(13, evaluateLong("2*5 + 3"));
        assertEquals(13, evaluateLong(" 2 * 5 + 3 "));
        assertEquals(13, evaluateLong("3 + 2 * 5"));
        assertEquals(16, evaluateLong("2* ( 5 + 3) "));
        assertEquals(13, evaluateLong("3 + (2 * 5)"));
        assertEquals(25, evaluateLong("(3 + 2) * 5"));
        assertEquals(3, evaluateLong("3"));
        assertEquals(-3, evaluateLong("-3"));
        assertEquals(3, evaluateLong("(3)"));
        assertEquals(-3, evaluateLong("(-3)"));
        assertEquals(-3, evaluateLong("-(3)"));
        assertEquals(10, evaluateLong("10 / 1"));
        assertEquals(5, evaluateLong("10 / 2"));
        assertEquals(3, evaluateLong("10 / 3"));
        assertEquals(2, evaluateLong("10 / 4"));
        assertEquals(2, evaluateLong("10 / 5"));
        assertEquals(1, evaluateLong("10 / 10"));
        assertEquals(-2, evaluateLong("10 / -4"));
    }

    @Test
    void doubleTests() {
        assertEquals(3.0, evaluateDouble("(1.0+2.0)"), EPS);
        assertEquals(3.0, evaluateDouble("1.0+2.0"), EPS);
        assertEquals(2.0, evaluateDouble(" 1  + 1.0  "), EPS);
        assertEquals(10.8, evaluateDouble(" 2.3 + (5.3 + 3.2)"), EPS);
        assertEquals(25.0, evaluateDouble("(3.0 + 2.0) * 5.0"), EPS);
        assertEquals(0.0, evaluateDouble("(3.0 + 2.0) * 0.0"), EPS);
        assertEquals(-2.0, evaluateDouble("-2.0"), EPS);
        assertEquals(-2.0, evaluateDouble("-(2.0)"), EPS);
        assertEquals(-2.0, evaluateDouble("(-2.0)"), EPS);
        assertEquals(10.0, evaluateDouble("10.0 / 1"), EPS);
        assertEquals(5.0, evaluateDouble("10 / 2.0"), EPS);
        assertEquals(3.3333333, evaluateDouble("10 / 3.0"), EPS);
        assertEquals(2.5, evaluateDouble("10 / 4.0"), EPS);
        assertEquals(2.0, evaluateDouble("10.0 / 5"), EPS);
        assertEquals(1.0, evaluateDouble("10 / 10.0"), EPS);
        assertEquals(-2.5, evaluateDouble("10 / -4.0"), EPS);
        assertEquals(-2.5, evaluateDouble("10 /- 4.0"), EPS);
        assertEquals(-2.5, evaluateDouble("10 /-4.0"), EPS);
    }

    @Test
    void doubleScientificTests() {
        assertEquals(3.0, evaluateDouble("(1.0e0+2.e0)"), EPS);
        assertEquals(3.0, evaluateDouble("1.0e+0+2.0e-0"), EPS);
        assertEquals(2.0e5, evaluateDouble(" 1e5  + 1.0e+5  "), EPS);
        assertEquals(-2.0e-5, evaluateDouble("-2.0e-5"), EPS);
        assertEquals(-2.0e3, evaluateDouble("-(2.0e+3)"), EPS);
        assertEquals(-2.0e3, evaluateDouble("(-2.0e3)"), EPS);
        assertEquals(1.0, evaluateDouble("10.0 / 1e1"), EPS);
        assertEquals(50.0, evaluateDouble("1e2 / 2.0"), EPS);
        assertEquals(50.0, evaluateDouble("1.e2 / 2.0"), EPS);
        assertEquals(50.0, evaluateDouble("1.0e2 / 2.0"), EPS);
        assertEquals(50.0, evaluateDouble("1.0e+02 / 2.0"), EPS);
        assertEquals(50.0, evaluateDouble("1.0e+02 / 2.0e0"), EPS);
        assertEquals(10 / -4.0e5, evaluateDouble("10 / -4.0e5"), EPS);
        assertEquals(10e2 /- 4.0e3, evaluateDouble("10e2 /- 4.0e3"), EPS);
        assertEquals(10e5 /-4.0e6, evaluateDouble("10e5 /-4.0e6"), EPS);
    }

    @Test
    void numberTests() {
        assertEquals(3.0, evaluateDouble("(1+2.0)"), EPS);
        assertEquals(3.0, evaluateDouble("1.0+2"), EPS);
        assertEquals(2.0, evaluateDouble(" 1  + 1.0  "), EPS);
        assertEquals(10.3, evaluateDouble(" 2.3 + (5 + 3)"), EPS);
        assertEquals(25.0, evaluateDouble("(3 + 2) * 5.0"), EPS);
        assertEquals(0.0, evaluateDouble("(3 + 2) * 0.0"), EPS);
        assertEquals(-1.0, evaluateDouble("-2.0 + 1"), EPS);

        RuntimeException runEx; // thjs is supposed to be RuntimeError!
        runEx = assertThrows(RuntimeException.class, () -> evaluateDouble("1 + 1"));
        assertEquals("Result of an unexpected type: Variant type mismatch: LONG, expected: DOUBLE", runEx.getMessage());
        runEx = assertThrows(RuntimeException.class, () -> evaluateDouble("round(1.0)"));
        assertEquals("Result of an unexpected type: Variant type mismatch: LONG, expected: DOUBLE", runEx.getMessage());

        assertEquals(0.0, evaluateNumber("(3 + 2) * 0.0"), EPS);
        assertEquals(2.0, evaluateNumber("1 + 1"), EPS);
        assertEquals(1.0, evaluateNumber("round(1.0)"), EPS);
    }

    @Test
    void edgeNumberTests() {
        assertEquals(9223372036854775806L, evaluateLong("9223372036854775806 * 1"));
        assertEquals(9223372036854775806L, evaluateLong("9223372036854775806 + 0"));
        assertEquals(9223372036854775806.0, evaluateDouble("9223372036854775806 * 1.0"), EPS);
        assertEquals(9223372036854775806.0, evaluateDouble("9223372036854775806 + 0.0"), EPS);
    }

    @Test
    void edgeNumberWrongTests() {
        // TODO:
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // NOTICE: these tests produce incorrect results
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // This is due to the limited precision of Double numbers
        // "9223372036854775806.1" is parsed into the equivalent of "9.223372036854776E18"
        // and then it tries to converts long value to double: 9.223372036854776E18
        // and converts double value to double: 9.223372036854776E18
        // and end up with the identical values
        assertTrue(evaluateBool("9223372036854775806 == 9223372036854775806.1"));
        assertFalse(evaluateBool("9223372036854775806 < 9223372036854775806.1"));
        assertFalse(evaluateBool("9223372036854775806.0 < 9223372036854775806.1"));
    }

    @Test
    void booleanTests() {
        assertTrue(evaluateBool("true"));
        assertTrue(evaluateBool("True"));
        assertTrue(evaluateBool("TRUE"));
        assertFalse(evaluateBool("false"));
        assertFalse(evaluateBool("False"));
        assertFalse(evaluateBool("FALSE"));
        assertTrue(evaluateBool(" ( true ) "));
        assertFalse(evaluateBool("( ( false ))"));
        assertFalse(evaluateBool("!(true)"));
        assertTrue(evaluateBool("not(false)"));
        assertTrue(evaluateBool("not(1 >= 9)"));
        assertTrue(evaluateBool("1 == 1"));
        assertTrue(evaluateBool("(1 == 1)"));
        assertTrue(evaluateBool("1+2 == 4-1"));
        assertFalse(evaluateBool("1+2 != 4-1"));
        assertFalse(evaluateBool("1+3 == 4-1"));
        assertTrue(evaluateBool("1+3 != 4-1"));
        assertTrue(evaluateBool("(1+2 == 4-1)"));
        assertTrue(evaluateBool("(1) == 1"));
        assertTrue(evaluateBool("1 == (1)"));
        assertTrue(evaluateBool("(1) == (1)"));
        assertTrue(evaluateBool("(1+2) == (4-1)"));
        assertTrue(evaluateBool("((1+2) == (4-1))"));
        assertTrue(evaluateBool("(\"A\") == (\"A\")"));
        assertTrue(evaluateBool("(\"A\") == \"A\""));
        assertTrue(evaluateBool("\"A\" == (\"A\")"));
        assertTrue(evaluateBool("\"A\" == \"A\""));
        assertFalse(evaluateBool("\"A\" == \"B\""));
        assertTrue(evaluateBool("\"A\" != \"B\""));
        assertFalse(evaluateBool("\"A\" == \"B\""));
        assertTrue(evaluateBool("\"ABC\" == \"ABC\""));
        assertTrue(evaluateBool("\"ABCE\" != \"ABC\""));
        // logical or/and
        assertTrue(evaluateBool("true or false"));
        assertTrue(evaluateBool("((true) or (false))"));
        assertFalse(evaluateBool("true and false"));
        assertFalse(evaluateBool("((true) and (false))"));
        assertTrue(evaluateBool("1 == 1 or 2 == 2"));
        assertTrue(evaluateBool("1 == 1 or 2 == 3"));
        assertTrue(evaluateBool("(1 == 2) or (2 == 2)"));
        assertFalse(evaluateBool("((1 == 2) or ((2 == 3)))"));
        assertTrue(evaluateBool("1 == 1 or 2 != 2"));
        assertTrue(evaluateBool("1 == 1 or 2 != 3"));
        assertTrue(evaluateBool("(1 != 2) or (2 == 2)"));
        assertTrue(evaluateBool("((1 != 2) or ((2 == 3)))"));
        assertTrue(evaluateBool("1 == 1 or not(2 == 2)"));
        assertFalse(evaluateBool("not(1 == 1) or 2 == 3"));
        assertFalse(evaluateBool("(1 == 2) or not(2 == 2)"));
        assertTrue(evaluateBool("not((1 == 2) or ((2 == 3)))"));
        assertTrue(evaluateBool("1 == 1 and 2 == 2"));
        assertFalse(evaluateBool("1 == 1 and 2 == 3"));
        assertFalse(evaluateBool("(1 == 2) and (2 == 2)"));
        assertFalse(evaluateBool("((1 == 2) and ((2 == 3)))"));
        assertFalse(evaluateBool("not(1 == 1) and 2 == 2"));
        assertTrue(evaluateBool("1 == 1 and not(2 == 3)"));
        assertTrue(evaluateBool("not(1 == 2) and (2 == 2)"));
        assertTrue(evaluateBool("not((1 == 2) and ((2 == 3)))"));
        assertTrue(evaluateBool("not(\"A\" == \"\") and \"B\" == \"B\""));
        assertTrue(evaluateBool("\"A\" == \"A\" or \"B\" == \"B\""));
        assertTrue(evaluateBool("\"A\" == \"A\" or \"B\" == \"C\""));
        assertTrue(evaluateBool("\"A\" == \"B\" or \"B\" == \"B\""));
        assertFalse(evaluateBool("\"A\" == \"B\" or \"B\" == \"C\""));
    }

    @Test
    void unaryNotTests() {
        assertTrue(evaluateBool("not(false)"));
        assertFalse(evaluateBool("not(not(false))"));
        assertTrue(evaluateBool("not(-1 > 1)"));
        assertFalse(evaluateBool("not(not(-1 > 1))"));
        assertTrue(evaluateBool("!(false)"));
        assertTrue(evaluateBool("not((false))"));
        assertTrue(evaluateBool("not(false) and -5 < -4.0"));
        assertFalse(evaluateBool("not(-5 < -4.0)"));
        assertFalse(evaluateBool("!(-5 < -4.0)"));
        assertTrue(evaluateBool("not(!(-5 < -4.0))"));
        assertTrue(evaluateBool("!(not(-5 < -4.0))"));
        assertFalse(evaluateBool("not(-(5) < -4.0)"));
        assertFalse(evaluateBool("!(true)"));
        assertFalse(evaluateBool("not(true)"));
        assertTrue(evaluateBool("not(1 >= 9)"));
        assertFalse(evaluateBool("not('A' == 'A')"));
        assertTrue(evaluateBool("not('A' == 'B')"));
    }

    @Test
    void unaryMinusTests() {
        assertTrue(evaluateBool("-5 < -4"));
        assertEquals(-1, evaluateLong("-1"));

        ParseError err;
        err = assertThrows(ParseError.class, () -> evaluate("--1"));
        assertEquals("Expression parsing error [line 1, pos 2]: Expect expression in expression '--1'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluateBool("--5 < -4"));
        assertEquals("Expression parsing error [line 1, pos 2]: Expect expression in expression '--5 < -4'", err.getMessage());

        assertEquals(7, evaluateLong("5 -- 2"));
        assertEquals(7, evaluateLong("5 - -2"));
        assertEquals(7, evaluateLong("5 --2"));
        assertEquals(7, evaluateLong("5 - - 2"));
        assertEquals(3, evaluateLong("5 +- 2"));
        assertEquals(3, evaluateLong("5 + -2"));
        assertEquals(3, evaluateLong("5 +-2"));
        assertEquals(3, evaluateLong("5 + - 2"));

        assertEquals(-10, evaluateLong("5 *- 2"));
        assertEquals(-10, evaluateLong("5 * -2"));
        assertEquals(-10, evaluateLong("5 *-2"));
        assertEquals(-10, evaluateLong("5 * - 2"));

        assertEquals(-3, evaluateLong("6 /- 2"));
        assertEquals(-3, evaluateLong("6 / -2"));
        assertEquals(-3, evaluateLong("6 /-2"));
        assertEquals(-3, evaluateLong("6 / - 2"));
    }

    @Test
    void stringTests() {
        RuntimeError runErr;
        assertEquals("", evaluateString("\"\""));
        assertEquals("true", evaluateString("\"true\""));
        assertEquals("A", evaluateString("\"A\""));
        assertEquals("A", evaluateString("(\"A\")"));
        assertEquals("'A'", evaluateString("\"'A'\""));
        assertEquals("\"A\"", evaluateString("'\"A\"'"));
        assertTrue(evaluateBool("'A' == 'A'"));
        assertTrue(evaluateBool("\"A\" == 'A'"));
        assertTrue(evaluateBool("\"\" == ''"));
        assertTrue(evaluateBool("'\"A\"' == '\"A\"'"));
        assertTrue(evaluateBool("\"'A'\" == \"'A'\""));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + \"B\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" + \"B\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("(\"A\") + \"B\""));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '(\"A\") + \"B\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("(\"A\") + (\"B\")"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '(\"A\") + (\"B\")'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("((\"A\") + (\"B\"))"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '((\"A\") + (\"B\"))'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\"+\"DE\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '\"ABC\"+\"DE\"'", runErr.getMessage());
    }

    @Test
    void byteBufferTests() {
        final ExprContextBuilder mutCtx = ExprContextFactory.globalContext();
        final RuntimeError runErr;

        mutCtx.addByteBuffer("$tuid", () -> constant("CLIENT1"));
        mutCtx.addByteBuffer("$tuid2", () -> constant("CLIENT2"));

        final ExprContext ctx = mutCtx.getAsExprContext();

        assertTrue(evaluateBool("$tuid == \"CLIENT1\"", ctx));
        assertTrue(evaluateBool("$tuid == $tuid", ctx));
        assertFalse(evaluateBool("$tuid == $tuid2", ctx));
        assertFalse(evaluateBool("$tuid2 == $tuid", ctx));
        assertTrue(evaluateBool("$tuid != $tuid2", ctx));
        assertTrue(evaluateBool("$tuid2 != $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid == \"CLIENT\" + \"1\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 19]: Operands must be numbers in expression '$tuid == \"CLIENT\" + \"1\"'", runErr.getMessage());
        assertTrue(evaluateBool("\"CLIENT1\" == $tuid", ctx));
        assertTrue(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT1\"]", ctx));
        assertFalse(evaluateBool("$tuid != \"CLIENT1\"", ctx));
        assertFalse(evaluateBool("\"CLIENT1\" != $tuid", ctx));
        assertFalse(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", ctx));
        assertFalse(evaluateBool("$tuid == \"CLIENT2\"", ctx));
        assertFalse(evaluateBool("\"CLIENT2\" == $tuid", ctx));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", ctx));
    }

    @Test
    void operatorInTest() {
        assertTrue(evaluateBool("\"A\" in [\"A\"]"));
        assertFalse(evaluateBool("not (\"A\" in [\"A\"])"));
        assertFalse(evaluateBool("\"A\" in [\"B\"]"));
        assertTrue(evaluateBool("\"B\" in [\"A\",\"B\"]"));
        assertTrue(evaluateBool("\"B\" in [\"A\", \"B\", \"A\", \"B\"]"));
        assertTrue(evaluateBool("'A' in ['A']"));
        assertTrue(evaluateBool("1 in [1]"));
        assertTrue(evaluateBool("(1 == 1) and (1 in [1])"));
        assertFalse(evaluateBool("1 in [2,3]"));
        assertTrue(evaluateBool("1 in [2,3,1]"));
        assertTrue(evaluateBool("2 in [2,1,2]"));
        assertTrue(evaluateBool("1.0 in [1.0]"));
        assertFalse(evaluateBool("1.0 in [2.0, 3.0]"));
        assertFalse(evaluateBool("1.0 in [2.0, 3.0, 2.0, 3.0]"));
        assertTrue(evaluateBool("1.0 in [2.0, 3.0, 1.0, 2.0, 3.0]"));
        assertTrue(evaluateBool("1.0 in [2.0, 1.0]"));
        assertTrue(evaluateBool("('AB') == 'ABC' or (1 in [2,3,4] or 56 > 12)"));
        assertTrue(evaluateBool("(1 in [2, 3, 4] or 2.0 in [1.0, 2.0])"));
        assertTrue(evaluateBool("((1 in [2, 3, 4]) or (2.0 in [1.0, 2.0]))"));
        assertTrue(evaluateBool("((1<=2 and 1 in [2, 3, 4]) or (2.0 in [1.0, 2.0]))"));
        assertTrue(evaluateBool("((1==1 and 4 in [1, 2, 3, 4]) and 5.0 == 5.0) and (2.0 in [1.0, 2.0] or \"A\" == \"B\")"));
        assertTrue(evaluateBool("123 in [1.0, 2.0, 3.0, 123.0]"));
        assertTrue(evaluateBool("123.0 in [1, 2, 3, 123]"));
        assertTrue(evaluateBool("PI in [PI]"));
        assertTrue(evaluateBool("PI in [PI, E]"));
        assertTrue(evaluateBool("3 in [2, 3.0]"));
        assertTrue(evaluateBool("3.0 in [2.0, 3]"));
    }

    @Test
    void operatorNotInTest() {
        assertTrue(evaluateBool("1 not in [2]"));
        assertTrue(evaluateBool("(1 not in [2]) == not(1 in [2])"));
        assertFalse(evaluateBool("1 not in [1]"));
        assertTrue(evaluateBool("(1 not in [1]) == not(1 in [1])"));
    }

    @Test
    void operatorWithinTest() {
        assertTrue(evaluateBool("1 within [1, 2]"));
        assertTrue(evaluateBool("1 within [1.0, 2.0]"));
        assertTrue(evaluateBool("1.0 within [1, 2]"));
        assertTrue(evaluateBool("1 within [0, 2]"));
        assertTrue(evaluateBool("(1 == 1) and (1 within [1, 2])"));
        assertFalse(evaluateBool("1 within [2,3]"));
        assertTrue(evaluateBool("1 within [3,1]"));
        assertTrue(evaluateBool("1 within [3,0]"));
        assertTrue(evaluateBool("2 within [2,2]"));
        assertTrue(evaluateBool("1.0 within [1.0, 2.0]"));
        assertTrue(evaluateBool("1.0 within [0.0, 2.0]"));
        assertFalse(evaluateBool("1.0 within [2.0, 3.0]"));
        assertFalse(evaluateBool("1.0 within [3.0, 2.0]"));
        assertTrue(evaluateBool("1.0 within [3.0, 1.0]"));
        assertTrue(evaluateBool("1.0 within [3.0, 0.0]"));
        assertTrue(evaluateBool("(1 within [2, 4] or 2.0 within [1.0, 2.0])"));
        assertTrue(evaluateBool("123 within [1, 123.0]"));
        assertTrue(evaluateBool("123 within [1, 123.1]"));
        assertTrue(evaluateBool("123.0 within [1, 123]"));
        assertFalse(evaluateBool("not(123 within [1, 123.0])"));
        assertFalse(evaluateBool("not(123.0 within [1, 123])"));
        assertTrue(evaluateBool("3 within [PI - 1, PI]"));
        assertTrue(evaluateBool("3 within [E, PI]"));
        assertTrue(evaluateBool("3 within [e, pi]"));
    }

    @Test
    void operatorNotWithinTest() {
        assertTrue(evaluateBool("1 not within [2, 3]"));
        assertTrue(evaluateBool("(1 not within [2, 3]) == not(1 within [2, 3])"));
        assertFalse(evaluateBool("2.5 not within [2, 3]"));
        assertTrue(evaluateBool("(2.5 not within [2, 3]) == not(2.5 within [2, 3])"));
        assertFalse(evaluateBool("2 not within [2, 3]"));
        assertTrue(evaluateBool("(2 not within [2, 3]) == not(2 within [2, 3])"));
    }

    @Test
    void operatorBetweenTest() {
        assertFalse(evaluateBool("1 between [1, 2]"));
        assertTrue(evaluateBool("1 within [0, 2]"));
        assertTrue(evaluateBool("1 within [0.0, 2.0]"));
        assertTrue(evaluateBool("1.0 within [0, 2]"));
        assertTrue(evaluateBool("(1 == 1) and (1 between [0, 2])"));
        assertFalse(evaluateBool("1 between [2,3]"));
        assertFalse(evaluateBool("1 between [3,1]"));
        assertTrue(evaluateBool("1 between [3,0]"));
        assertFalse(evaluateBool("2 between [2,2]"));
        assertFalse(evaluateBool("1.0 between [1.0, 2.0]"));
        assertTrue(evaluateBool("1.0 between [0.0, 2.0]"));
        assertFalse(evaluateBool("1.0 between [2.0, 3.0]"));
        assertFalse(evaluateBool("1.0 between [3.0, 2.0]"));
        assertFalse(evaluateBool("1.0 between [3.0, 1.0]"));
        assertTrue(evaluateBool("1.0 between [3.0, 0.0]"));
        assertTrue(evaluateBool("(1 between [2, 4] or 2.0 between [1.0, 3.0])"));
        assertFalse(evaluateBool("123 between [1, 123.0]"));
        assertTrue(evaluateBool("123 between [1, 123.1]"));
        assertFalse(evaluateBool("123.0 between [1, 123]"));
        assertTrue(evaluateBool("123.0 between [1, 123.01]"));
        assertTrue(evaluateBool("not(123 between [1, 123.0])"));
        assertTrue(evaluateBool("not(123.0 between [1, 123])"));
        assertTrue(evaluateBool("3 between [PI - 1, PI]"));
        assertTrue(evaluateBool("3 between [E, PI]"));
        assertTrue(evaluateBool("3 between [e, pi]"));
    }

    @Test
    void operatorNotBetweenTest() {
        assertTrue(evaluateBool("1 not between [2, 3]"));
        assertTrue(evaluateBool("(1 not between [2, 3]) == not(1 between [2, 3])"));
        assertFalse(evaluateBool("2.5 not between [2, 3]"));
        assertTrue(evaluateBool("(2.5 not between [2, 3]) == not(2.5 between [2, 3])"));
        assertTrue(evaluateBool("2 not between [2, 3]"));
        assertTrue(evaluateBool("(2 not between [2, 3]) == not(2 between [2, 3])"));
    }

    @Test
    void testMalformedExpressions() {
        ParseError err;
        RuntimeError runErr;
        err = assertThrows(ParseError.class, () -> evaluate(""));
        assertEquals("Expression parsing error [line 1, pos 1]: Expect expression in expression ''", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("not(or true)"));
        assertEquals("Expression parsing error [line 1, pos 5]: Expect expression in expression 'not(or true)'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("'B' in ['A', 1]"));
        assertEquals("Expression parsing error [line 1, pos 15]: Different value types in IN operator list: STRING and LONG in expression ''B' in ['A', 1]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 not (in [1])"));
        assertEquals("Expression parsing error [line 1, pos 3]: Malformed expression: parsing ended prematurely in expression '1 not (in [1])'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 (not in [1])"));
        assertEquals("Expression parsing error [line 1, pos 1]: Function name should be an identifier in expression '1 (not in [1])'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 not in ([1])"));
        assertEquals("Expression parsing error [line 1, pos 10]: Expect '[' after IN operator in expression '1 not in ([1])'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("'B' in ['A', 1.0]"));
        assertEquals("Expression parsing error [line 1, pos 17]: Different value types in IN operator list: STRING and DOUBLE in expression ''B' in ['A', 1.0]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in ['A', 1]"));
        assertEquals("Expression parsing error [line 1, pos 13]: Different value types in IN operator list: STRING and LONG in expression '1 in ['A', 1]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1.0 in ['A', 1]"));
        assertEquals("Expression parsing error [line 1, pos 15]: Different value types in IN operator list: STRING and LONG in expression '1.0 in ['A', 1]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1.0 in [1, 'A']"));
        assertEquals("Expression parsing error [line 1, pos 15]: Different value types in IN operator list: LONG and STRING in expression '1.0 in [1, 'A']'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1.0 in [1.0, 'A']"));
        assertEquals("Expression parsing error [line 1, pos 17]: Different value types in IN operator list: DOUBLE and STRING in expression '1.0 in [1.0, 'A']'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1.0 in [1, true]"));
        assertEquals("Expression parsing error [line 1, pos 16]: Different value types in IN operator list: LONG and BOOL in expression '1.0 in [1, true]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [true, 'A']"));
        assertEquals("Expression parsing error [line 1, pos 16]: Different value types in IN operator list: BOOL and STRING in expression '1 in [true, 'A']'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [true, 1]"));
        assertEquals("Expression parsing error [line 1, pos 14]: Different value types in IN operator list: BOOL and LONG in expression '1 in [true, 1]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [true, 1.0]"));
        assertEquals("Expression parsing error [line 1, pos 16]: Different value types in IN operator list: BOOL and DOUBLE in expression '1 in [true, 1.0]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2"));
        assertEquals("Expression parsing error [line 1, pos 7]: Expect ']' after '[' in expression '1 in [2'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2,"));
        assertEquals("Expression parsing error [line 1, pos 8]: Expect expression in expression '1 in [2,'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2,]"));
        assertEquals("Expression parsing error [line 1, pos 9]: Expect expression in expression '1 in [2,]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in []"));
        assertEquals("Expression parsing error [line 1, pos 7]: Expect expression in expression '1 in []'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in 2]"));
        assertEquals("Expression parsing error [line 1, pos 6]: Expect '[' after IN operator in expression '1 in 2]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("(5+2"));
        assertEquals("Expression parsing error [line 1, pos 4]: Expect ')' after expression in expression '(5+2'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("4-5)"));
        assertEquals("Expression parsing error [line 1, pos 4]: Malformed expression: parsing ended prematurely in expression '4-5)'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2, 3"));
        assertEquals("Expression parsing error [line 1, pos 10]: Expect ']' after '[' in expression '1 in [2, 3'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 == 2 3.0"));
        assertEquals("Expression parsing error [line 1, pos 8]: Malformed expression: parsing ended prematurely in expression '1 == 2 3.0'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 == 2 && 3==5"));
        assertEquals("Expression parsing error [line 1, pos 8]: Unexpected character in expression '1 == 2 && 3==5'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 == 2 || 3==5"));
        assertEquals("Expression parsing error [line 1, pos 8]: Unexpected character in expression '1 == 2 || 3==5'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 = 2"));
        assertEquals("Expression parsing error [line 1, pos 3]: Expected '==' comparison not found in expression '1 = 2'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 =A"));
        assertEquals("Expression parsing error [line 1, pos 3]: Expected '==' comparison not found in expression '1 =A'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("\"A"));
        assertEquals("Expression parsing error [line 1, pos 1]: Unterminated string in expression '\"A'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("\"A == \"A\""));
        assertEquals("Expression parsing error [line 1, pos 9]: Unterminated string in expression '\"A == \"A\"'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("\"A\" == \"A"));
        assertEquals("Expression parsing error [line 1, pos 8]: Unterminated string in expression '\"A\" == \"A'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("\"A\" == \"A or 5 == 4"));
        assertEquals("Expression parsing error [line 1, pos 8]: Unterminated string in expression '\"A\" == \"A or 5 == 4'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("\"A == \"A\" or 5 == 4"));
        assertEquals("Expression parsing error [line 1, pos 9]: Unterminated string in expression '\"A == \"A\" or 5 == 4'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("\"A == \"A\" or 5 == 4\""));
        assertEquals("Expression parsing error [line 1, pos 8]: Malformed expression: parsing ended prematurely in expression '\"A == \"A\" or 5 == 4\"'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("abc.1 == 2"));
        assertEquals("Expression parsing error [line 1, pos 5]: Expect function name after '.' in expression 'abc.1 == 2'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("abc.1() == 2"));
        assertEquals("Expression parsing error [line 1, pos 5]: Expect function name after '.' in expression 'abc.1() == 2'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 within []"));
        assertEquals("Expression parsing error [line 1, pos 11]: Expect expression in expression '1 within []'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 within [2]"));
        assertEquals("Expression parsing error [line 1, pos 12]: Expect 2 values separated by ',' in range operator in expression '1 within [2]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 within [2, 3, 4]"));
        assertEquals("Expression parsing error [line 1, pos 15]: Expect ']' after '[' and 2 numbers in expression '1 within [2, 3, 4]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 between []"));
        assertEquals("Expression parsing error [line 1, pos 12]: Expect expression in expression '1 between []'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 between [2]"));
        assertEquals("Expression parsing error [line 1, pos 13]: Expect 2 values separated by ',' in range operator in expression '1 between [2]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 between [2, 3, 4]"));
        assertEquals("Expression parsing error [line 1, pos 16]: Expect ']' after '[' and 2 numbers in expression '1 between [2, 3, 4]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("-not(true)"));
        assertEquals("Expression parsing error [line 1, pos 2]: Expect expression in expression '-not(true)'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("not-true"));
        assertEquals("Expression parsing error [line 1, pos 4]: Operator NOT should be applied to the expression in parens '()' in expression 'not-true'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("-true"));
        assertEquals("Expression parsing error [line 1, pos 2]: Unary minus is applicable to numbers only in expression '-true'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("-'A'"));
        assertEquals("Expression parsing error [line 1, pos 2]: Unary minus is applicable to numbers only in expression '-'A''", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("not(-true)"));
        assertEquals("Expression parsing error [line 1, pos 10]: Unary minus is applicable to numbers only in expression 'not(-true)'", err.getMessage());

        // Evaluation errors
        runErr = assertThrows(RuntimeError.class, () -> evaluate("!(1 + 2)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression '!(1 + 2)'", runErr.getMessage());

        final ExprContextBuilder mutCtx = ExprContextFactory.globalContext();
        mutCtx.addString("$ric", () -> "VOD.L");
        mutCtx.addLong("$productId", () -> 123L);
        mutCtx.addByteBuffer("$tuid", () -> constant("CLIENT1"));
        mutCtx.addFunction("isEven", (result, arg1) -> {
            final long l = arg1.getAsLong();
            result.accept(l % 2 == 0);
        });

        mutCtx.addFunction("func1", (result, arg1) -> {
            final long l = arg1.getAsLong();
            result.accept(l);
        });

        mutCtx.addFunction("func2", (result, arg1, arg2) -> {
            final long l = arg1.getAsLong();
            final double d = arg2.getAsDouble();
            result.accept(l + d);
        });

        mutCtx.addFunction("func5", (result, arg1, arg2, arg3, arg4, arg5) -> {
            final long l = arg1.getAsLong();
            final double d = arg2.getAsDouble();
            final boolean bool = arg3.getAsBoolean();
            final String s = arg4.getAsString();
            final ByteBuffer bb = arg5.getAsByteBuffer();
            result.accept(l > d && bool && !s.isEmpty() && ByteBufferUtils.startsWith(bb, "CLIENT"));
        });

        final ExprContext ctx = mutCtx.getAsExprContext();

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric == 1", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and LONG in expression '$ric == 1'", runErr.getMessage());

        // comparison for IN operator works the same way as for usual "==" operator
        // it means we can compare LONG and DOUBLE
//        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId in [123.0]", ctx));
//        assertEquals("Operands of different types cannot be compared: LONG and DOUBLE", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $tuid == \"CLIENT1CLIENT1\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + $tuid == \"CLIENT1CLIENT1\"'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$primary == \"XLON\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown identifier '$primary' in expression '$primary == \"XLON\"'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ + 1", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown identifier '$' in expression '$ + 1'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("1 == 2 in [2]", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands of different types cannot be compared: BOOL and LONG in expression '1 == 2 in [2]'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("1+2 or 1 == 2", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operand must be a boolean in expression '1+2 or 1 == 2'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("1 == 2 or 1+2", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operand must be a boolean in expression '1 == 2 or 1+2'", runErr.getMessage());

        // This one is ok because only left operand of "==" is evaluated
        assertTrue(evaluateBool("1 == 1 or 1+2"));

        // This one is ok because only left operand of "==" is evaluated
        assertFalse(evaluateBool("1 == 2 and 1+2"));

        runErr = assertThrows(RuntimeError.class, () -> evaluate("1+2 and 1 == 2", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operand must be a boolean in expression '1+2 and 1 == 2'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("123 == $", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Unknown identifier '$' in expression '123 == $'", runErr.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("isEven(", ctx));
        assertEquals("Expression parsing error [line 1, pos 7]: Expect expression in expression 'isEven('", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("isEven)", ctx));
        assertEquals("Expression parsing error [line 1, pos 7]: Malformed expression: parsing ended prematurely in expression 'isEven)'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("isEven(1", ctx));
        assertEquals("Expression parsing error [line 1, pos 8]: Expect ')' after arguments in expression 'isEven(1'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("isEven(1, 2, 3, 4, 5, 6)", ctx));
        assertEquals("Expression parsing error [line 1, pos 23]: Can't have more than 5 arguments in expression 'isEven(1, 2, 3, 4, 5, 6)'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("1.isEven(2, 3, 4, 5, 6)", ctx));
        assertEquals("Expression parsing error [line 1, pos 22]: Can't have more than 4 arguments in expression '1.isEven(2, 3, 4, 5, 6)'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("1(1)", ctx));
        assertEquals("Expression parsing error [line 1, pos 1]: Function name should be an identifier in expression '1(1)'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("\"ABC\"()", ctx));
        assertEquals("Expression parsing error [line 1, pos 1]: Function name should be an identifier in expression '\"ABC\"()'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("-'123'", ctx));
        assertEquals("Expression parsing error [line 1, pos 2]: Unary minus is applicable to numbers only in expression '-'123''", err.getMessage());

        // error: 0 parameters instead of 1
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven()", ctx));
        assertTrue(runErr.getMessage().contains("ClassCastException in function 'isEven'"));
        assertTrue(runErr.getMessage().contains("cannot be cast to com.tereigo.expr.function.Function0"));

        // error: 2 parameters instead of 1
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven(1, 2)", ctx));
        assertTrue(runErr.getMessage().contains("ClassCastException in function 'isEven'"));
        assertTrue(runErr.getMessage().contains("cannot be cast to com.tereigo.expr.function.Function2"));

        // error: func2(10, 1) it expects Double as a second parameter
        runErr = assertThrows(RuntimeError.class, () -> evaluate("func5(func1(100), func2(func1(10), func2(10, 1)), not($enabled), $ric, $tuid)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'func5': RuntimeException in function 'func2': RuntimeException in function 'func2': Variant type mismatch: LONG, expected: DOUBLE in expression 'func5(func1(100), func2(func1(10), func2(10, 1)), not($enabled), $ric, $tuid)'", runErr.getMessage());

        // error is: func2(10) - expected call with 2 args
        runErr = assertThrows(RuntimeError.class, () -> evaluate("func5(func1(100), func2(func1(10), func2(10)), not($enabled), $ric, $tuid)", ctx));
        assertTrue(runErr.getMessage().contains("RuntimeException in function 'func5': RuntimeException in function 'func2': ClassCastException in function 'func2'"));
        assertTrue(runErr.getMessage().contains("cannot be cast to com.tereigo.expr.function.Function1"));

        // error: Expects Long parameter instead of Double
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven(1.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isEven': Variant type mismatch: DOUBLE, expected: LONG in expression 'isEven(1.0)'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("-isEven(1)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a number in expression '-isEven(1)'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven(\"\")", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isEven': Variant type mismatch: STRING, expected: LONG in expression 'isEven(\"\")'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven(true)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isEven': Variant type mismatch: BOOL, expected: LONG in expression 'isEven(true)'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven($ric)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isEven': Variant type mismatch: STRING, expected: LONG in expression 'isEven($ric)'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven($tuid)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isEven': Variant type mismatch: BYTE_BUFFER, expected: LONG in expression 'isEven($tuid)'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("unknownFunction($tuid)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown function 'unknownFunction' in expression 'unknownFunction($tuid)'", runErr.getMessage());

        // running pre-compiled malformed expression
        final ASTRoot root = ExprCompiler.compile("$primary == 123");
        runErr = assertThrows(RuntimeError.class, () -> evaluate(root, ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown identifier '$primary' in expression '$primary == 123'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(root, ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown identifier '$primary' in expression '$primary == 123'", runErr.getMessage());
    }
}