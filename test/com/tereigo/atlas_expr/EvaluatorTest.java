package com.tereigo.atlas_expr;

import org.junit.jupiter.api.Test;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluatorTest extends EvaluatorTestBase {

    @Test
    void simpleLongTest() {
        assertEquals(3, evaluateLong("1+2"));
    }

    @Test
    void simpleDoubleTest() {
        assertEquals(-2.0, evaluateDouble("-2.0"), EPS);
        assertEquals(3.0, evaluateDouble("(1.0+2.0)"), EPS);
    }

    @Test
    void simpleBoolTest() {
        assertTrue(evaluateBool("8 % 3 == 2"));
    }

    @Test
    void rerunningTheSameExpressionDouble() {
        Expr expr = ExprCompiler.compile("(1.0+2.0)");
        assertEquals(3.0, evaluateDouble(expr), EPS);
        assertEquals(3.0, evaluateDouble(expr), EPS);
        assertEquals(3.0, evaluateDouble(expr), EPS);
    }

    @Test
    void rerunningTheSameExpressionLong() {
        Expr expr = ExprCompiler.compile("(1+2)-(5)+(8+5)");
        assertEquals(11, evaluateLong(expr));
        assertEquals(11, evaluateLong(expr));
        assertEquals(11, evaluateLong(expr));
    }

    @Test
    void rerunningTheSameExpressionBool() {
        Expr expr = ExprCompiler.compile("not(true) or not false and ((true and not false) or 5 != 2)");
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
    }

    @Test
    void rerunningTheSameExpressionString() {
        Expr expr = ExprCompiler.compile("\"A\" != \"BC\" and \"C\" != \"D\"");
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
        assertFalse(evaluateBool("!true"));
        assertTrue(evaluateBool("not false"));
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
    void stringTests() {
        RuntimeError runErr;
        assertEquals("", evaluateString("\"\""));
        assertEquals("true", evaluateString("\"true\""));
        assertEquals("A", evaluateString("\"A\""));
        assertEquals("A", evaluateString("(\"A\")"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + \"B\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("(\"A\") + \"B\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("(\"A\") + (\"B\")"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("((\"A\") + (\"B\"))"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"ABC\"+\"DE\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void byteBufferTests() {
        final ExprEnvironmentImpl env = new ExprEnvironmentImpl();
        RuntimeError runErr;

        env.defineByteBuffer("$tuid", () -> constant("CLIENT1"));
        env.defineByteBuffer("$tuid2", () -> constant("CLIENT2"));

        assertTrue(evaluateBool("$tuid == \"CLIENT1\"", env));
        assertTrue(evaluateBool("$tuid == $tuid", env));
        assertFalse(evaluateBool("$tuid == $tuid2", env));
        assertFalse(evaluateBool("$tuid2 == $tuid", env));
        assertTrue(evaluateBool("$tuid != $tuid2", env));
        assertTrue(evaluateBool("$tuid2 != $tuid", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid == \"CLIENT\" + \"1\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        assertTrue(evaluateBool("\"CLIENT1\" == $tuid", env));
        assertTrue(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT1\"]", env));
        assertFalse(evaluateBool("$tuid != \"CLIENT1\"", env));
        assertFalse(evaluateBool("\"CLIENT1\" != $tuid", env));
        assertFalse(evaluateBool("not ($tuid in [\"CLIENT0\", \"CLIENT1\"])", env));
        assertFalse(evaluateBool("$tuid == \"CLIENT2\"", env));
        assertFalse(evaluateBool("\"CLIENT2\" == $tuid", env));
        assertFalse(evaluateBool("$tuid in [\"CLIENT0\", \"CLIENT2\"]", env));
    }

    @Test
    void operatorInTest() {
        assertTrue(evaluateBool("\"A\" in [\"A\"]"));
        assertFalse(evaluateBool("not (\"A\" in [\"A\"])"));
        assertFalse(evaluateBool("\"A\" in [\"B\"]"));
        assertTrue(evaluateBool("\"B\" in [\"A\",\"B\"]"));
        assertTrue(evaluateBool("\"B\" in [\"A\", \"B\", \"A\", \"B\"]"));
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
        assertTrue(evaluateBool("(\"AB\") == \"ABC\" or (1 in [2,3,4] or 56 > 12)"));
        assertTrue(evaluateBool("(1 in [2, 3, 4] or 2.0 in [1.0, 2.0])"));
        assertTrue(evaluateBool("((1 in [2, 3, 4]) or (2.0 in [1.0, 2.0]))"));
        assertTrue(evaluateBool("((1<=2 and 1 in [2, 3, 4]) or (2.0 in [1.0, 2.0]))"));
        assertTrue(evaluateBool("((1==1 and 4 in [1, 2, 3, 4]) and 5.0 == 5.0) and (2.0 in [1.0, 2.0] or \"A\" == \"B\")"));
    }

    @Test
    void testMalformedExpressions() {
        RuntimeError runErr;
        // TODO: decide what type of exception is the most appropriate
        ParseError err = assertThrows(ParseError.class, () -> evaluate(""));
//        assertThrows(IllegalArgumentException.class, () -> evaluate(""));
        assertEquals("[line 1] Error at pos 1: Expect expression", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2, 3.0]"));
        assertEquals("[line 1] Error at pos 13 (']'): Different value types in IN operator list: LONG and DOUBLE", err.getMessage());
        err= assertThrows(ParseError.class, () -> evaluate("\"B\" in [\"A\", 1]"));
        assertEquals("[line 1] Error at pos 15 (']'): Different value types in IN operator list: STRING and LONG", err.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("!1 + 2"));
        assertEquals("Operand must be a boolean", runErr.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("(5+2"));
        assertEquals("[line 1] Error at pos 4: Expect ')' after expression", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("4-5)"));
        assertEquals("[line 1] Error at pos 4 (')'): Malformed expression: parsing ended prematurely", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2, 3"));
        assertEquals("[line 1] Error at pos 10: Expect ']' after '['", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 == 2 3.0"));
        assertEquals("[line 1] Error at pos 8 ('3.0'): Malformed expression: parsing ended prematurely", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 == 2 && 3==5"));
        assertEquals("[line 1] Error at pos 8: Unexpected character", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 == 2 || 3==5"));
        assertEquals("[line 1] Error at pos 8: Unexpected character", err.getMessage());

        final ExprEnvironmentImpl env = new ExprEnvironmentImpl();
        env.defineString("$ric", () -> "VOD.L");
        env.defineLong("$productId", () -> 123L);
        env.defineByteBuffer("$tuid", () -> constant("CLIENT1"));

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric == 1", env));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId in [123.0]", env));
        assertEquals("Operands of different types cannot be compared: LONG and DOUBLE", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $tuid == \"CLIENT1CLIENT1\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$primary == \"XLON\"", env));
        assertEquals("Unknown identifier '$primary'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ + 1", env));
        assertEquals("Unknown identifier '$'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("123 == $", env));
        assertEquals("Unknown identifier '$'", runErr.getMessage());
    }
}