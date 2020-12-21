package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprEvaluatorTest extends ExprEvaluatorTestBase {

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
        ASTRoot expr = ExprCompiler.compile("(1.0+2.0)");
        assertEquals(3.0, evaluateDouble(expr), EPS);
        assertEquals(3.0, evaluateDouble(expr), EPS);
        assertEquals(3.0, evaluateDouble(expr), EPS);
    }

    @Test
    void rerunningTheSameExpressionLong() {
        ASTRoot expr = ExprCompiler.compile("(1+2)-(5)+(8+5)");
        assertEquals(11, evaluateLong(expr));
        assertEquals(11, evaluateLong(expr));
        assertEquals(11, evaluateLong(expr));
    }

    @Test
    void rerunningTheSameExpressionBool() {
        ASTRoot expr = ExprCompiler.compile("not(true) or not false and ((true and not false) or 5 != 2)");
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
        assertTrue(evaluateBool(expr));
    }

    @Test
    void rerunningTheSameExpressionString() {
        ASTRoot expr = ExprCompiler.compile("\"A\" != \"BC\" and \"C\" != \"D\"");
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
        final MutableExprContext ctx = ExprContextFactory.create();
        RuntimeError runErr;

        ctx.defineByteBuffer("$tuid", () -> constant("CLIENT1"));
        ctx.defineByteBuffer("$tuid2", () -> constant("CLIENT2"));

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
        assertTrue(evaluateBool("123 in [1.0, 2.0, 3.0, 123.0]"));
        assertTrue(evaluateBool("123.0 in [1, 2, 3, 123]"));
    }

    @Test
    void testMalformedExpressions() {
        ParseError err;
        RuntimeError runErr;
        err = assertThrows(ParseError.class, () -> evaluate(""));
         assertEquals("Expression parsing error [line 1, pos 1]: Expect expression in expression ''", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2, 3.0]"));
        assertEquals("Expression parsing error [line 1, pos 13]: Different value types in IN operator list: LONG and DOUBLE in expression '1 in [2, 3.0]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("\"B\" in [\"A\", 1]"));
        assertEquals("Expression parsing error [line 1, pos 15]: Different value types in IN operator list: STRING and LONG in expression '\"B\" in [\"A\", 1]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2"));
        assertEquals("Expression parsing error [line 1, pos 7]: Expect ']' after '[' in expression '1 in [2'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2,"));
        assertEquals("Expression parsing error [line 1, pos 8]: Expect number/string list entry inside '[]' in expression '1 in [2,'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in [2,]"));
        assertEquals("Expression parsing error [line 1, pos 9]: Expect number/string list entry inside '[]' in expression '1 in [2,]'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("1 in []"));
        assertEquals("Expression parsing error [line 1, pos 7]: Expect number/string list entry inside '[]' in expression '1 in []'", err.getMessage());
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
        err = assertThrows(ParseError.class, () -> evaluate("abc.cde == 2"));
        assertEquals("Expression parsing error [line 1, pos 9]: Expect '(' after '.'function_name in expression 'abc.cde == 2'", err.getMessage());

        // Evaluation errors
        runErr = assertThrows(RuntimeError.class, () -> evaluate("!1 + 2"));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression '!1 + 2'", runErr.getMessage());

        final MutableExprContext ctx = ExprContextFactory.create();
        ctx.defineString("$ric", () -> "VOD.L");
        ctx.defineLong("$productId", () -> 123L);
        ctx.defineByteBuffer("$tuid", () -> constant("CLIENT1"));
        ctx.defineFunction("isEven", (result, arg1) -> {
            long l = arg1.getAsLong();
            result.accept(l % 2 == 0);
        });

        ctx.defineFunction("func1", (result, arg1) -> {
            long l = arg1.getAsLong();
            result.accept(l);
        });

        ctx.defineFunction("func2", (result, arg1, arg2) -> {
            long l = arg1.getAsLong();
            double d = arg2.getAsDouble();
            result.accept(l + d);
        });

        ctx.defineFunction("func5", (result, arg1, arg2, arg3, arg4, arg5) -> {
            long l = arg1.getAsLong();
            double d = arg2.getAsDouble();
            boolean bool = arg3.getAsBoolean();
            String s = arg4.getAsString();
            ByteBuffer bb = arg5.getAsByteBuffer();
            result.accept(l > d && bool && !s.isEmpty() && ByteBufferUtils.startWith(bb,"CLIENT"));
        });

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

        // error: 0 parameters instead of 1
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven()", ctx));
        assertTrue(runErr.getMessage().contains("ClassCastException in function 'isEven'"));
        assertTrue(runErr.getMessage().contains("cannot be cast to com.tereigo.atlas_expr.function.Function0"));

        // error: 2 parameters instead of 1
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven(1, 2)", ctx));
        assertTrue(runErr.getMessage().contains("ClassCastException in function 'isEven'"));
        assertTrue(runErr.getMessage().contains("cannot be cast to com.tereigo.atlas_expr.function.Function2"));

        // error: func2(10, 1) it expects Double as a second parameter
        runErr = assertThrows(RuntimeError.class, () -> evaluate("func5(func1(100), func2(func1(10), func2(10, 1)), not $enabled, $ric, $tuid)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'func5': RuntimeException in function 'func2': RuntimeException in function 'func2': Variant type mismatch: LONG, expected: DOUBLE in expression 'func5(func1(100), func2(func1(10), func2(10, 1)), not $enabled, $ric, $tuid)'", runErr.getMessage());

        // error is: func2(10) - expected call with 2 args
        runErr = assertThrows(RuntimeError.class, () -> evaluate("func5(func1(100), func2(func1(10), func2(10)), not $enabled, $ric, $tuid)", ctx));
        assertTrue(runErr.getMessage().contains("RuntimeException in function 'func5': RuntimeException in function 'func2': ClassCastException in function 'func2'"));
        assertTrue(runErr.getMessage().contains("cannot be cast to com.tereigo.atlas_expr.function.Function1"));

        // error: Expects Long parameter instead of Double
        runErr = assertThrows(RuntimeError.class, () -> evaluate("isEven(1.0)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: RuntimeException in function 'isEven': Variant type mismatch: DOUBLE, expected: LONG in expression 'isEven(1.0)'", runErr.getMessage());

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
        ASTRoot root = ExprCompiler.compile("$primary == 123");
        runErr = assertThrows(RuntimeError.class, () -> evaluate(root, ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown identifier '$primary' in expression '$primary == 123'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(root, ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Unknown identifier '$primary' in expression '$primary == 123'", runErr.getMessage());
    }
}