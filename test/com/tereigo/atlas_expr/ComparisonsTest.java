package com.tereigo.atlas_expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparisonsTest extends EvaluatorTestBase {

    @Test
    void equalityTests() {
        RuntimeError runErr;
        assertTrue(evaluateBool("true == true"));
        assertTrue(evaluateBool("False == False"));
        assertFalse(evaluateBool("true == false"));
        assertFalse(evaluateBool("FALSE == true"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == 0"));
        assertEquals("Operands of different types cannot be compared: BOOL and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == 0.0"));
        assertEquals("Operands of different types cannot be compared: BOOL and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == \"A\""));
        assertEquals("Operands of different types cannot be compared: BOOL and STRING", runErr.getMessage());
        // long
        assertTrue(evaluateBool("0 == 0"));
        assertTrue(evaluateBool("123 == 123"));
        assertFalse(evaluateBool("0 == 1"));
        assertTrue(evaluateBool("0 == 0.0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 == true"));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 == \"\""));
        assertEquals("Operands of different types cannot be compared: LONG and STRING", runErr.getMessage());
        // double
        assertTrue(evaluateBool("0.0 == 0.0"));
        assertTrue(evaluateBool("123.0 == 123.0"));
        assertFalse(evaluateBool("0.0 == 1.0"));
        assertTrue(evaluateBool("0.0 == 0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 == true"));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 == \"\""));
        assertEquals("Operands of different types cannot be compared: DOUBLE and STRING", runErr.getMessage());
        // String
        assertTrue(evaluateBool("\"\" == \"\""));
        assertTrue(evaluateBool("\"A\" == \"A\""));
        assertFalse(evaluateBool("\"A\" == \"B\""));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" == 0"));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" == 0"));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" == true"));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" == true"));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" == 0.0"));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" == 0.0"));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
    }

    @Test
    void equalityWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        assertTrue(evaluateBool("$enabled == true", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == $tuid", env));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == false", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == false", env));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == false", env));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == false", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled == $ric", env));
        assertEquals("Operands of different types cannot be compared: BOOL and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled == $tuid", env));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId == 123", env));
        assertTrue(evaluateBool("$productId == 123.0", env));
        assertFalse(evaluateBool("$productId == $PI", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == $ric", env));
        assertEquals("Operands of different types cannot be compared: LONG and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 == $tuid", env));
        assertEquals("Operands of different types cannot be compared: LONG and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid == 0", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == $enabled", env));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI == 3.14", env));
        assertFalse(evaluateBool("$PI == 3", env));
        assertFalse(evaluateBool("$PI == $productId", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == $ric", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 == $tuid", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 0.0", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == $enabled", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        // String
        assertTrue(evaluateBool("$ric == \"VOD.L\"", env));
        assertFalse(evaluateBool("$ric == $tuid", env));
        assertTrue(evaluateBool("\"CLIENT1\" == $tuid", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $productId", env));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == 123", env));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == 0.0", env));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $PI", env));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == true", env));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $enabled", env));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        // ByteBuffer
        assertFalse(evaluateBool("\"\" == $tuid", env));
        assertFalse(evaluateBool("$tuid == \"A\"", env));
        assertTrue(evaluateBool("$tuid == \"CLIENT1\"", env));
        assertTrue(evaluateBool("$tuid in [\"CLIENT1\"]", env));
        assertTrue(evaluateBool("$ric == \"VOD.L\" and $ric != $tuid", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $productId", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 123", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 0.0", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $PI", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == true", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $enabled", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
    }

    @Test
    void nonEqualityTests() {
        RuntimeError runErr;
        assertFalse(evaluateBool("TRUE != True"));
        assertFalse(evaluateBool("FALSE != False"));
        assertTrue(evaluateBool("true != false"));
        assertTrue(evaluateBool("FALSE != true"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != 0"));
        assertEquals("Operands of different types cannot be compared: BOOL and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != 0.0"));
        assertEquals("Operands of different types cannot be compared: BOOL and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != \"A\""));
        assertEquals("Operands of different types cannot be compared: BOOL and STRING", runErr.getMessage());
        // long
        assertFalse(evaluateBool("0 != 0"));
        assertFalse(evaluateBool("123 != 123"));
        assertTrue(evaluateBool("0 != 1"));
        assertFalse(evaluateBool("0 != 0.0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 != true"));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 != \"\""));
        assertEquals("Operands of different types cannot be compared: LONG and STRING", runErr.getMessage());
        // double
        assertFalse(evaluateBool("0.0 != 0.0"));
        assertFalse(evaluateBool("123.0 != 123.0"));
        assertTrue(evaluateBool("0.0 != 1.0"));
        assertFalse(evaluateBool("0.0 != 0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 != true"));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 != \"\""));
        assertEquals("Operands of different types cannot be compared: DOUBLE and STRING", runErr.getMessage());
        // String
        assertFalse(evaluateBool("\"\" != \"\""));
        assertFalse(evaluateBool("\"A\" != \"A\""));
        assertTrue(evaluateBool("\"A\" != \"B\""));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" != 0"));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" != 0"));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" != true"));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" != true"));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" != 0.0"));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" != 0.0"));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
    }

    @Test
    void nonEqualityWithEnvTests() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        assertFalse(evaluateBool("$enabled != true", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != $tuid", env));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != false", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != false", env));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != false", env));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != false", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled != $ric", env));
        assertEquals("Operands of different types cannot be compared: BOOL and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled != $tuid", env));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId != 123", env));
        assertFalse(evaluateBool("$productId != 123.0", env));
        assertTrue(evaluateBool("$productId != $PI", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != $ric", env));
        assertEquals("Operands of different types cannot be compared: LONG and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 != $tuid", env));
        assertEquals("Operands of different types cannot be compared: LONG and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid != 0", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != $enabled", env));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI != 3.14", env));
        assertTrue(evaluateBool("$PI != 3", env));
        assertTrue(evaluateBool("$PI != $productId", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != $ric", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 != $tuid", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 0.0", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != $enabled", env));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        // String
        assertFalse(evaluateBool("$ric != \"VOD.L\"", env));
        assertTrue(evaluateBool("$ric != $tuid", env));
        assertFalse(evaluateBool("\"CLIENT1\" != $tuid", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $productId", env));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != 123", env));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != 0.0", env));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $PI", env));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != true", env));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $enabled", env));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        // ByteBuffer
        assertTrue(evaluateBool("\"\" != $tuid", env));
        assertTrue(evaluateBool("$tuid != \"A\"", env));
        assertFalse(evaluateBool("$tuid != \"CLIENT1\"", env));
        assertFalse(evaluateBool("$ric != \"VOD.L\" and $ric != $tuid", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $productId", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 123", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 0.0", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $PI", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != true", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $enabled", env));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
    }

    @Test
    void comparisonTestsGreater() {
        RuntimeError runErr;
        // long
        assertTrue(evaluateBool("1 > 0"));
        assertFalse(evaluateBool("0 > 0"));
        assertFalse(evaluateBool("12 > 123"));
        assertTrue(evaluateBool("1 > 0.0"));
        assertFalse(evaluateBool("1 > 1"));
        assertTrue(evaluateBool("1.0 > 0"));
        assertFalse(evaluateBool("1.0 > 1"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 > true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 > \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertTrue(evaluateBool("1.0 > 0.0"));
        assertFalse(evaluateBool("123.0 > 123.0"));
        assertFalse(evaluateBool("0.0 > 1.0"));
        assertTrue(evaluateBool("1.0 > 0"));
        assertTrue(evaluateBool("1 > 0.0"));
        assertFalse(evaluateBool("1.0 > 1"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 > true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 > \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" > \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" > 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" > true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" > 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void comparisonWithEnvTestsGreater() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId > 123", env));
        assertFalse(evaluateBool("$productId > 123.0", env));
        assertTrue(evaluateBool("$productId > $PI", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid > 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI > 3.14", env));
        assertTrue(evaluateBool("$PI > 3", env));
        assertFalse(evaluateBool("$PI > $productId", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > \"VOD.L\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > \"CLIENT1\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > \"VOD.L\" and $ric > $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void comparisonTestsGreaterOrEqual() {
        RuntimeError runErr;
        // long
        assertTrue(evaluateBool("1 >= 0"));
        assertTrue(evaluateBool("0 >= 0"));
        assertFalse(evaluateBool("12 >= 123"));
        assertTrue(evaluateBool("1 >= 0.0"));
        assertTrue(evaluateBool("1 >= 1"));
        assertTrue(evaluateBool("1.0 >= 0"));
        assertTrue(evaluateBool("1.0 >= 1"));
        assertFalse(evaluateBool("1 >= 10.0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 >= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 >= \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertTrue(evaluateBool("1.0 >= 0.0"));
        assertTrue(evaluateBool("123.0 >= 123.0"));
        assertFalse(evaluateBool("0.0 >= 1.0"));
        assertTrue(evaluateBool("1.0 >= 0"));
        assertTrue(evaluateBool("1 >= 0.0"));
        assertTrue(evaluateBool("1.0 >= 1"));
        assertFalse(evaluateBool("1.0 >= 10"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 >= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 >= \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" >= \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" >= 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" >= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" >= 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void comparisonWithEnvTestsGreaterOrEqual() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId >= 123", env));
        assertTrue(evaluateBool("$productId >= 123.0", env));
        assertTrue(evaluateBool("$productId >= $PI", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid >= 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI >= 3.14", env));
        assertTrue(evaluateBool("$PI >= 3", env));
        assertFalse(evaluateBool("$PI >= $productId", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= \"VOD.L\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= \"CLIENT1\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= \"VOD.L\" and $ric >= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void comparisonTestsLess() {
        RuntimeError runErr;
        // long
        assertFalse(evaluateBool("1 < 0"));
        assertFalse(evaluateBool("0 < 0"));
        assertTrue(evaluateBool("12 < 123"));
        assertFalse(evaluateBool("1 < 0.0"));
        assertFalse(evaluateBool("1 < 1"));
        assertFalse(evaluateBool("1.0 < 0"));
        assertFalse(evaluateBool("1.0 < 1"));
        assertTrue(evaluateBool("1 < 10.0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 < true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 < \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertFalse(evaluateBool("1.0 < 0.0"));
        assertFalse(evaluateBool("123.0 < 123.0"));
        assertTrue(evaluateBool("0.0 < 1.0"));
        assertFalse(evaluateBool("1.0 < 0"));
        assertFalse(evaluateBool("1 < 0.0"));
        assertFalse(evaluateBool("1.0 < 1"));
        assertTrue(evaluateBool("1.0 < 10"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 < true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 < \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" < \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" < 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" < true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" < 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void comparisonWithEnvTestsLess() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId < 123", env));
        assertFalse(evaluateBool("$productId < 123.0", env));
        assertFalse(evaluateBool("$productId < $PI", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid < 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI < 3.14", env));
        assertFalse(evaluateBool("$PI < 3", env));
        assertTrue(evaluateBool("$PI < $productId", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < \"VOD.L\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < \"CLIENT1\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < \"VOD.L\" and $ric < $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void comparisonTestsLessOrEqual() {
        RuntimeError runErr;
        // long
        assertFalse(evaluateBool("1 <= 0"));
        assertTrue(evaluateBool("0 <= 0"));
        assertTrue(evaluateBool("12 <= 123"));
        assertFalse(evaluateBool("1 <= 0.0"));
        assertTrue(evaluateBool("1 <= 1"));
        assertFalse(evaluateBool("1.0 <= 0"));
        assertTrue(evaluateBool("1.0 <= 1"));
        assertTrue(evaluateBool("1 <= 10.0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 <= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 <= \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertFalse(evaluateBool("1.0 <= 0.0"));
        assertTrue(evaluateBool("123.0 <= 123.0"));
        assertTrue(evaluateBool("0.0 <= 1.0"));
        assertFalse(evaluateBool("1.0 <= 0"));
        assertFalse(evaluateBool("1 <= 0.0"));
        assertTrue(evaluateBool("1.0 <= 1"));
        assertTrue(evaluateBool("1.0 <= 10"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 <= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 <= \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" <= \"\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= \"A\""));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" <= 0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" <= true"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" <= 0.0"));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }

    @Test
    void comparisonWithEnvTestsLessOrEqual() {
        final ExprEnvironmentImpl env = createEnvironment();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= false", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId <= 123", env));
        assertTrue(evaluateBool("$productId <= 123.0", env));
        assertFalse(evaluateBool("$productId <= $PI", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid <= 0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI <= 3.14", env));
        assertFalse(evaluateBool("$PI <= 3", env));
        assertTrue(evaluateBool("$PI <= $productId", env));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= $ric", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= \"VOD.L\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= \"A\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= \"CLIENT1\"", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= \"VOD.L\" and $ric <= $tuid", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $productId", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 123", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 0.0", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $PI", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= true", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $enabled", env));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }
}