package com.tereigo.atlas_expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArithmeticTest extends EvaluatorTestBase {

    @Test
    void additionTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true + \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(2, evaluateLong("1 + 1"));
        assertEquals(2.0, evaluateDouble("1 + 1.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 + true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 + \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertEquals(2.0, evaluateDouble("1.0 + 1.0"), EPS);
        assertEquals(2.0, evaluateDouble("1.0 + 1"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 + true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 + \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" + \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + \"B\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" + 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" + true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" + 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" + 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void additionWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled + $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(124, evaluateLong("$productId + 1", env));
        assertEquals(246, evaluateLong("$productId + $productId", env));
        assertEquals(124.0, evaluateDouble("$productId + 1.0", env), EPS);
        assertEquals(126.14, evaluateDouble("$productId + $PI", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId + $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertEquals(4.14, evaluateDouble("$PI + 1", env), EPS);
        assertEquals(126.14, evaluateDouble("$PI + $productId", env), EPS);
        assertEquals(4.14, evaluateDouble("$PI + 1.0", env), EPS);
        assertEquals(6.28, evaluateDouble("$PI + $PI", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI + $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric + $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid + $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void subtractionTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true - \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(2, evaluateLong("3 - 1"));
        assertEquals(2.0, evaluateDouble("3 - 1.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 - true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 - \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertEquals(2.0, evaluateDouble("3.0 - 1.0"), EPS);
        assertEquals(2.0, evaluateDouble("3.0 - 1"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 - true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 - \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" - \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - \"B\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" - 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" - true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" - 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" - 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void subtractionWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled - $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(122, evaluateLong("$productId - 1", env));
        assertEquals(0, evaluateLong("$productId - $productId", env));
        assertEquals(122.0, evaluateDouble("$productId - 1.0", env), EPS);
        assertEquals(119.86, evaluateDouble("$productId - $PI", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId - $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertEquals(2.14, evaluateDouble("$PI - 1", env), EPS);
        assertEquals(-119.86, evaluateDouble("$PI - $productId", env), EPS);
        assertEquals(2.14, evaluateDouble("$PI - 1.0", env), EPS);
        assertEquals(0.0, evaluateDouble("$PI - $PI", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI - $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric - $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid - $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void multiplicationTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true * \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(3, evaluateLong("3 * 1"));
        assertEquals(3.0, evaluateDouble("3 * 1.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 * true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 * \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertEquals(3.0, evaluateDouble("3.0 * 1.0"), EPS);
        assertEquals(3.0, evaluateDouble("3.0 * 1"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 * true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 * \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" * \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * \"B\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" * 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" * true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" * 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" * 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void multiplicationWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled * $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(123, evaluateLong("$productId * 1", env));
        assertEquals(15129, evaluateLong("$productId * $productId", env));
        assertEquals(123.0, evaluateDouble("$productId * 1.0", env), EPS);
        assertEquals(386.22, evaluateDouble("$productId * $PI", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId * $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertEquals(3.14, evaluateDouble("$PI * 1", env), EPS);
        assertEquals(386.22, evaluateDouble("$PI * $productId", env), EPS);
        assertEquals(3.14, evaluateDouble("$PI * 1.0", env), EPS);
        assertEquals(9.8596, evaluateDouble("$PI * $PI", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI * $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric * $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid * $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void divisionTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true / \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(3, evaluateLong("3 / 1"));
        assertEquals(3, evaluateLong("10 / 3"));
        assertEquals(3.333333, evaluateDouble("10.0 / 3"), EPS);
        assertEquals(3.333333, evaluateDouble("10 / 3.0"), EPS);
        assertEquals(3.0, evaluateDouble("3 / 1.0"), EPS);
        ArithmeticException ex = assertThrows(ArithmeticException.class, () -> evaluateLong("3 / 0"));
        assertEquals("/ by zero", ex.getMessage());
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("3 / 0.0"), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("-3 / -0.0"), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-3 / 0.0"), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("3 / -0.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 / true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 / \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
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
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 / \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" / \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / \"B\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" / 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" / true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" / 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" / 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void divisionWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled / $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertEquals(123, evaluateLong("$productId / 1", env));
        assertEquals(1, evaluateLong("$productId / $productId", env));
        assertEquals(123.0, evaluateDouble("$productId / 1.0", env), EPS);
        assertEquals(39.17197452, evaluateDouble("$productId / $PI", env), EPS);
        ArithmeticException ex = assertThrows(ArithmeticException.class, () -> evaluateLong("$productId / 0", env));
        assertEquals("/ by zero", ex.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId / $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertEquals(3.14, evaluateDouble("$PI / 1", env), EPS);
        assertEquals(0.025528455, evaluateDouble("$PI / $productId", env), EPS);
        assertEquals(3.14, evaluateDouble("$PI / 1.0", env), EPS);
        assertEquals(1.0, evaluateDouble("$PI / $PI", env), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("$PI / 0", env), EPS);
        assertEquals(Double.POSITIVE_INFINITY, evaluateDouble("$PI / -0", env), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-$PI / 0", env), EPS);
        assertEquals(Double.NEGATIVE_INFINITY, evaluateDouble("-$PI / -0", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / \"\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI / $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric / $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid / $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void modulusTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % true"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % 0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % 0.0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("true % \"A\""));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        // long
        assertEquals(0, evaluateLong("3 % 1"));
        assertEquals(1, evaluateLong("3 % 2"));
        assertEquals(1, evaluateLong("10 % 3"));
        assertEquals(0, evaluateLong("10 % 2"));
        assertEquals(2, evaluateLong("10 % 4"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("10.0 % 3"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("10 % 3.0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("3 % 1.0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        ArithmeticException ex = assertThrows(ArithmeticException.class, () -> evaluateLong("3 % 0"));
        assertEquals("/ by zero", ex.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 % true"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0 % \"\""));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        // double
        runErr = assertThrows(RuntimeError.class, () -> evaluate("3.0 % 1.0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("10.0 % 3.0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("3.0 % 1"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 % true"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("0.0 % \"\""));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"\" % \"\""));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % \"B\""));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % 0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0\" % 0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % true"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"true\" % true"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"A\" % 0.0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("\"0.0\" % 0.0"));
        assertEquals("Operands must be long numbers", runErr.getMessage());
    }

    @Test
    void modulusWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();
        RuntimeError runErr;

        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % true", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $enabled", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % 0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $productId", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % 0.0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $PI", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % \"A\"", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $ric", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$enabled % $tuid", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        // long
        assertEquals(0, evaluateLong("$productId % 1", env));
        assertEquals(1, evaluateLong("$productId % 2", env));
        assertEquals(0, evaluateLong("$productId % 3", env));
        assertEquals(0, evaluateLong("$productId % $productId", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % 1.0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $PI", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        ArithmeticException ex = assertThrows(ArithmeticException.class, () -> evaluateLong("$productId % 0", env));
        assertEquals("/ by zero", ex.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % true", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $enabled", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % \"\"", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $ric", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$productId % $tuid", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        // double
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % 1", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $productId", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % 1.0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $PI", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % true", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $enabled", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % \"\"", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $ric", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$PI % $tuid", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % \"A\"", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $ric", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % true", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $enabled", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % 0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $productId", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % 0.0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $PI", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$ric % $tuid", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % \"A\"", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $ric", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % true", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $enabled", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % 0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $productId", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % 0.0", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $PI", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("$tuid % $tuid", env));
        assertEquals("Operands must be long numbers", runErr.getMessage());
    }

    @Test
    void unaryNotTests() {
        RuntimeError runErr;
        assertFalse(evaluateBool("not true"));
        assertTrue(evaluateBool("not FALSE"));
        runErr = assertThrows(RuntimeError.class, () -> evaluate("not 0"));
        assertEquals("Operand must be a boolean", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("not 0.0"));
        assertEquals("Operand must be a boolean", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluate("not \"\""));
        assertEquals("Operand must be a boolean", runErr.getMessage());
    }

    @Test
    void unaryNotWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        assertFalse(evaluateBool("not $enabled", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not $productId", env));
        assertEquals("Operand must be a boolean", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not $PI", env));
        assertEquals("Operand must be a boolean", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not $ric", env));
        assertEquals("Operand must be a boolean", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("not $tuid", env));
        assertEquals("Operand must be a boolean", runErr.getMessage());
    }

    @Test
    void unaryMinusTests() {
        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluate("-true"));
        assertEquals("Operand must be a number", runErr.getMessage());
        assertEquals(-1, evaluateLong("-1"));
        assertEquals(1, evaluateLong("--1"));
        assertEquals(1, evaluateLong("-(-1)"));
        assertEquals(0, evaluateLong("-0"));
        assertEquals(-1.0, evaluateDouble("-1.0"), EPS);
        assertEquals(1.0, evaluateDouble("--1.0"), EPS);
        assertEquals(1.0, evaluateDouble("-(-1.0)"), EPS);
        assertEquals(0.0, evaluateDouble("-0.0"), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluate("-\"A\""));
        assertEquals("Operand must be a number", runErr.getMessage());
    }

    @Test
    void unaryMinusWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("-$enabled", env));
        assertEquals("Operand must be a number", runErr.getMessage());
        assertEquals(-123, evaluateLong("-$productId", env));
        assertEquals(-3.14, evaluateDouble("-$PI", env), EPS);
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("-$ric", env));
        assertEquals("Operand must be a number", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("-$tuid", env));
        assertEquals("Operand must be a number", runErr.getMessage());
    }
}