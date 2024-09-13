package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprArithmeticTest extends ExprEvaluatorTestBase {

    @Test
    void additionTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + true"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + 0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true + 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true + 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true + \"A\"'", runErr.getMessage());
        // long
        assertEquals(2, evaluateLong("1 + 1"));
        assertEquals(2.0, evaluateDouble("1 + 1.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 + true"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 + \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 + \"\"'", runErr.getMessage());
        // double
        assertEquals(2.0, evaluateDouble("1.0 + 1.0"), EPS);
        assertEquals(2.0, evaluateDouble("1.0 + 1"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 + true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 + \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 + \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" + \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" + \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + \"B\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" + \"B\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" + 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" + 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" + 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" + true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" + 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" + 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" + 0.0'", runErr.getMessage());
    }

    @Test
    void additionWithContextTests() {
        final ExprContext ctx = createContext();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled + $tuid'", runErr.getMessage());
        // long
        assertEquals(124, evaluateLong("$productId + 1", ctx));
        assertEquals(246, evaluateLong("$productId + $productId", ctx));
        assertEquals(124.0, evaluateDouble("$productId + 1.0", ctx), EPS);
        assertEquals(126.14, evaluateDouble("$productId + $PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId + $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId + \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId + $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId + $tuid'", runErr.getMessage());
        // double
        assertEquals(4.14, evaluateDouble("$PI + 1", ctx), EPS);
        assertEquals(126.14, evaluateDouble("$PI + $productId", ctx), EPS);
        assertEquals(4.14, evaluateDouble("$PI + 1.0", ctx), EPS);
        assertEquals(6.28, evaluateDouble("$PI + $PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI + $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI + \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI + $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI + $tuid'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric + $tuid'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid + $tuid'", runErr.getMessage());
    }

    @Test
    void subtractionTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - true"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - 0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true - 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true - 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true - \"A\"'", runErr.getMessage());
        // long
        assertEquals(2, evaluateLong("3 - 1"));
        assertEquals(2.0, evaluateDouble("3 - 1.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 - true"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 - \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 - \"\"'", runErr.getMessage());
        // double
        assertEquals(2.0, evaluateDouble("3.0 - 1.0"), EPS);
        assertEquals(2.0, evaluateDouble("3.0 - 1"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 - true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 - \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 - \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" - \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" - \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - \"B\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" - \"B\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" - 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" - 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" - 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" - true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" - 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" - 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" - 0.0'", runErr.getMessage());
    }

    @Test
    void subtractionWithContextTests() {
        final ExprContext ctx = createContext();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled - $tuid'", runErr.getMessage());
        // long
        assertEquals(122, evaluateLong("$productId - 1", ctx));
        assertEquals(0, evaluateLong("$productId - $productId", ctx));
        assertEquals(122.0, evaluateDouble("$productId - 1.0", ctx), EPS);
        assertEquals(119.86, evaluateDouble("$productId - $PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId - $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId - \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId - $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId - $tuid'", runErr.getMessage());
        // double
        assertEquals(2.14, evaluateDouble("$PI - 1", ctx), EPS);
        assertEquals(-119.86, evaluateDouble("$PI - $productId", ctx), EPS);
        assertEquals(2.14, evaluateDouble("$PI - 1.0", ctx), EPS);
        assertEquals(0.0, evaluateDouble("$PI - $PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI - $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI - \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI - $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI - $tuid'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric - $tuid'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid - $tuid'", runErr.getMessage());
    }

    @Test
    void multiplicationTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * true"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * 0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true * 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true * 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true * \"A\"'", runErr.getMessage());
        // long
        assertEquals(3, evaluateLong("3 * 1"));
        assertEquals(3.0, evaluateDouble("3 * 1.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 * true"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 * \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 * \"\"'", runErr.getMessage());
        // double
        assertEquals(3.0, evaluateDouble("3.0 * 1.0"), EPS);
        assertEquals(3.0, evaluateDouble("3.0 * 1"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 * true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 * \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 * \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" * \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" * \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * \"B\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" * \"B\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" * 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" * 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" * 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" * true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" * 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" * 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" * 0.0'", runErr.getMessage());
    }

    @Test
    void multiplicationWithContextTests() {
        final ExprContext ctx = createContext();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled * $tuid'", runErr.getMessage());
        // long
        assertEquals(123, evaluateLong("$productId * 1", ctx));
        assertEquals(15129, evaluateLong("$productId * $productId", ctx));
        assertEquals(123.0, evaluateDouble("$productId * 1.0", ctx), EPS);
        assertEquals(386.22, evaluateDouble("$productId * $PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId * $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId * \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId * $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId * $tuid'", runErr.getMessage());
        // double
        assertEquals(3.14, evaluateDouble("$PI * 1", ctx), EPS);
        assertEquals(386.22, evaluateDouble("$PI * $productId", ctx), EPS);
        assertEquals(3.14, evaluateDouble("$PI * 1.0", ctx), EPS);
        assertEquals(9.8596, evaluateDouble("$PI * $PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI * $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI * \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI * $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI * $tuid'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric * $tuid'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid * $tuid'", runErr.getMessage());
    }

    @Test
    void divisionTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / true"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / 0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true / 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true / 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true / \"A\"'", runErr.getMessage());
        // long
        assertEquals(3, evaluateLong("3 / 1"));
        assertEquals(3, evaluateLong("10 / 3"));
        assertEquals(3.333333, evaluateDouble("10.0 / 3"), EPS);
        assertEquals(3.333333, evaluateDouble("10 / 3.0"), EPS);
        assertEquals(3.0, evaluateDouble("3 / 1.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluateLong("3 / 0"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Division by zero in expression '3 / 0'", runErr.getMessage());
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("3 / 0.0"), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("-3 / -0.0"), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-3 / 0.0"), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("3 / -0.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 / true"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 / \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 / \"\"'", runErr.getMessage());
        // double
        assertEquals(3.0, evaluateDouble("3.0 / 1.0"), EPS);
        assertEquals(3.333333, evaluateDouble("10.0 / 3.0"), EPS);
        assertEquals(3.0, evaluateDouble("3.0 / 1"), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("3.0 / -0"), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-3.0 / 0"), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("3.0 / 0"), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("3.0 / 0.0"), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("-3.0 / -0.0"), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-3.0 / 0.0"), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("3.0 / -0.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 / true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 / \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 / \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" / \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" / \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / \"B\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" / \"B\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" / 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" / 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" / 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" / true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" / 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" / 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" / 0.0'", runErr.getMessage());
    }

    @Test
    void divisionWithContextTests() {
        final ExprContext ctx = createContext();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled / $tuid'", runErr.getMessage());
        // long
        assertEquals(123, evaluateLong("$productId / 1", ctx));
        assertEquals(1, evaluateLong("$productId / $productId", ctx));
        assertEquals(123.0, evaluateDouble("$productId / 1.0", ctx), EPS);
        assertEquals(39.17197452, evaluateDouble("$productId / $PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluateLong("$productId / 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Division by zero in expression '$productId / 0'", runErr.getMessage());

        final ExprContextBuilder mutCtx2 = ExprContextFactory.globalContext();
        mutCtx2.addLong("$zero", () -> 0L);
        final ExprContext ctx2 = mutCtx2.getAsExprContext();
        runErr = assertThrows(RuntimeError.class, () -> evaluateLong("123 / $zero", ctx2));
        assertEquals("Expression evaluation error [line 1, pos 5]: Division by zero in expression '123 / $zero'", runErr.getMessage());

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId / $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId / \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId / $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId / $tuid'", runErr.getMessage());
        // double
        assertEquals(3.14, evaluateDouble("$PI / 1", ctx), EPS);
        assertEquals(0.025528455, evaluateDouble("$PI / $productId", ctx), EPS);
        assertEquals(3.14, evaluateDouble("$PI / 1.0", ctx), EPS);
        assertEquals(1.0, evaluateDouble("$PI / $PI", ctx), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("$PI / 0", ctx), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("$PI / -0", ctx), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-$PI / 0", ctx), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-$PI / -0", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI / $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI / \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI / $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI / $tuid'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric / $tuid'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid / $tuid'", runErr.getMessage());
    }

    @Test
    void modulusTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % true"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression 'true % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % 0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression 'true % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression 'true % 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression 'true % \"A\"'", runErr.getMessage());
        // long
        assertEquals(0, evaluateLong("3 % 1"));
        assertEquals(1, evaluateLong("3 % 2"));
        assertEquals(1, evaluateLong("10 % 3"));
        assertEquals(0, evaluateLong("10 % 2"));
        assertEquals(2, evaluateLong("10 % 4"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("10.0 % 3"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '10.0 % 3'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("10 % 3.0"));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be long numbers in expression '10 % 3.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("3 % 1.0"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be long numbers in expression '3 % 1.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateLong("3 % 0"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Division by zero in expression '3 % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 % true"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be long numbers in expression '0 % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 % \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be long numbers in expression '0 % \"\"'", runErr.getMessage());
        // double
        runErr = assertThrows(RuntimeError.class, () -> evaluate("3.0 % 1.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '3.0 % 1.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("10.0 % 3.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '10.0 % 3.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("3.0 % 1"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '3.0 % 1'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 % true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '0.0 % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 % \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '0.0 % \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" % \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be long numbers in expression '\"\" % \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % \"B\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '\"A\" % \"B\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '\"A\" % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" % 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '\"0\" % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '\"A\" % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" % true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be long numbers in expression '\"true\" % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '\"A\" % 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" % 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '\"0.0\" % 0.0'", runErr.getMessage());
    }

    @Test
    void modulusWithContextTests() {
        final ExprContext ctx = createContext();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be long numbers in expression '$enabled % $tuid'", runErr.getMessage());
        // long
        assertEquals(0, evaluateLong("$productId % 1", ctx));
        assertEquals(1, evaluateLong("$productId % 2", ctx));
        assertEquals(0, evaluateLong("$productId % 3", ctx));
        assertEquals(0, evaluateLong("$productId % $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % 1.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be long numbers in expression '$productId % 1.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be long numbers in expression '$productId % $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Division by zero in expression '$productId % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be long numbers in expression '$productId % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be long numbers in expression '$productId % $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be long numbers in expression '$productId % \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be long numbers in expression '$productId % $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be long numbers in expression '$productId % $tuid'", runErr.getMessage());
        // double
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % 1", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % 1'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % 1.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % 1.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % \"\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be long numbers in expression '$PI % $tuid'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be long numbers in expression '$ric % $tuid'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % $enabled'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be long numbers in expression '$tuid % $tuid'", runErr.getMessage());
    }

    @Test
    void unaryNotTests() {
        ParseError err;
        RuntimeError runErr;
        assertFalse(evaluateBool("not(true)"));
        assertTrue(evaluateBool("not(FALSE)"));

        // Parsing errors
        err = assertThrows(ParseError.class, () -> evaluate("not 0"));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not 0'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("not 0.0"));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not 0.0'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluate("not \"\""));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not \"\"'", err.getMessage());

        // Evaluation errors
        runErr = assertThrows(RuntimeError.class, () -> evaluate("not(0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not(0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("not(0.0)"));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not(0.0)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("not(\"\")"));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not(\"\")'", runErr.getMessage());
    }

    @Test
    void unaryNotWithContextTests() {
        final ExprContext ctx = createContext();

        ParseError err;
        RuntimeError runErr;
        assertFalse(evaluateBool("not($enabled)", ctx));

        // Parsing errors
        err = assertThrows(ParseError.class, () -> evaluateBool("not $productId", ctx));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not $productId'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluateBool("not $PI", ctx));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not $PI'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluateBool("not $ric", ctx));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not $ric'", err.getMessage());
        err = assertThrows(ParseError.class, () -> evaluateBool("not $tuid", ctx));
        assertEquals("Expression parsing error [line 1, pos 5]: Operator NOT should be applied to the expression in parens '()' in expression 'not $tuid'", err.getMessage());

        // Evaluation errors
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not($productId)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not($productId)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not($PI)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not($PI)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not($ric)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not($ric)'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not($tuid)", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a boolean in expression 'not($tuid)'", runErr.getMessage());
    }

    @Test
    void unaryMinusTests() {
        ParseError err;
        assertEquals(-1, evaluateLong("-1"));
        assertEquals(1, evaluateLong("-(-1)"));
        assertEquals(0, evaluateLong("-0"));
        assertEquals(-1.0, evaluateDouble("-1.0"), EPS);
        assertEquals(1.0, evaluateDouble("-(-1.0)"), EPS);
        assertEquals(0.0, evaluateDouble("-0.0"), EPS);
        assertEquals(7, evaluateLong("5 - (-2)"));
        assertEquals(7, evaluateLong("5 - ((-2))"));
        assertEquals(7, evaluateLong("5 - (((-2)))"));
        assertEquals(3, evaluateLong("5 - (-(-2))"));
        assertEquals(-3.141592653589793, evaluateDouble("-pi"), EPS);

        err = assertThrows(ParseError.class, () -> evaluate("-true"));
        assertEquals("Expression parsing error [line 1, pos 2]: Unary minus is applicable to numbers only in expression '-true'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("5 -- 2"));
        assertEquals("Expression parsing error [line 1, pos 6]: Double minus syntax ('--') is not supported as erroneous in expression '5 -- 2'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("5 - -2"));
        assertEquals("Expression parsing error [line 1, pos 6]: Double minus syntax ('--') is not supported as erroneous in expression '5 - -2'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("5 (--2)"));
        assertEquals("Expression parsing error [line 1, pos 1]: Function name should be an identifier in expression '5 (--2)'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("-'A'"));
        assertEquals("Expression parsing error [line 1, pos 2]: Unary minus is applicable to numbers only in expression '-'A''", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluate("--1"));
        assertEquals("Expression parsing error [line 1, pos 2]: Expect expression in expression '--1'", err.getMessage());

        err = assertThrows(ParseError.class, () -> evaluateDouble("--1.0"));
        assertEquals("Expression parsing error [line 1, pos 2]: Expect expression in expression '--1.0'", err.getMessage());
    }

    @Test
    void unaryMinusWithContextTests() {
        final ExprContext ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("-$enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a number in expression '-$enabled'", runErr.getMessage());
        assertEquals(-123, evaluateLong("-$productId", ctx));
        assertEquals(-3.14, evaluateDouble("-$PI", ctx), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("-$ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a number in expression '-$ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("-$tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 1]: Operand must be a number in expression '-$tuid'", runErr.getMessage());
    }
}