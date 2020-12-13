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
    void equalityWithContextTests() {
        final ExprContextImpl ctx = createContext();

        RuntimeError runErr;
        assertTrue(evaluateBool("$enabled == true", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == false", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == false", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == false", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == false", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled == $ric", ctx));
        assertEquals("Operands of different types cannot be compared: BOOL and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled == $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId == 123", ctx));
        assertTrue(evaluateBool("$productId == 123.0", ctx));
        assertFalse(evaluateBool("$productId == $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == $ric", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 == $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid == 0", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == $enabled", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI == 3.14", ctx));
        assertFalse(evaluateBool("$PI == 3", ctx));
        assertFalse(evaluateBool("$PI == $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == $ric", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 == $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 0.0", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == $enabled", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        // String
        assertTrue(evaluateBool("$ric == \"VOD.L\"", ctx));
        assertFalse(evaluateBool("$ric == $tuid", ctx));
        assertTrue(evaluateBool("\"CLIENT1\" == $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $productId", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == 123", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == 0.0", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $PI", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == true", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $enabled", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        // ByteBuffer
        assertFalse(evaluateBool("\"\" == $tuid", ctx));
        assertFalse(evaluateBool("$tuid == \"A\"", ctx));
        assertTrue(evaluateBool("$tuid == \"CLIENT1\"", ctx));
        assertTrue(evaluateBool("$tuid in [\"CLIENT1\"]", ctx));
        assertTrue(evaluateBool("$ric == \"VOD.L\" and $ric != $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $productId", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 123", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 0.0", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $PI", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == true", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $enabled", ctx));
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
    void nonEqualityWithContextTests() {
        final ExprContextImpl ctx = createContext();

        RuntimeError runErr;
        assertFalse(evaluateBool("$enabled != true", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != false", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != false", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != false", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != false", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled != $ric", ctx));
        assertEquals("Operands of different types cannot be compared: BOOL and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled != $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: BOOL and BYTE_BUFFER", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId != 123", ctx));
        assertFalse(evaluateBool("$productId != 123.0", ctx));
        assertTrue(evaluateBool("$productId != $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != $ric", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 != $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid != 0", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != $enabled", ctx));
        assertEquals("Operands of different types cannot be compared: LONG and BOOL", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI != 3.14", ctx));
        assertTrue(evaluateBool("$PI != 3", ctx));
        assertTrue(evaluateBool("$PI != $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != $ric", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and STRING", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 != $tuid", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BYTE_BUFFER", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 0.0", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != $enabled", ctx));
        assertEquals("Operands of different types cannot be compared: DOUBLE and BOOL", runErr.getMessage());
        // String
        assertFalse(evaluateBool("$ric != \"VOD.L\"", ctx));
        assertTrue(evaluateBool("$ric != $tuid", ctx));
        assertFalse(evaluateBool("\"CLIENT1\" != $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $productId", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != 123", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != 0.0", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $PI", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != true", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $enabled", ctx));
        assertEquals("Operands of different types cannot be compared: STRING and BOOL", runErr.getMessage());
        // ByteBuffer
        assertTrue(evaluateBool("\"\" != $tuid", ctx));
        assertTrue(evaluateBool("$tuid != \"A\"", ctx));
        assertFalse(evaluateBool("$tuid != \"CLIENT1\"", ctx));
        assertFalse(evaluateBool("$ric != \"VOD.L\" and $ric != $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $productId", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 123", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and LONG", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 0.0", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $PI", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != true", ctx));
        assertEquals("Operands of different types cannot be compared: BYTE_BUFFER and BOOL", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $enabled", ctx));
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
    void comparisonWithContextTestsGreater() {
        final ExprContextImpl ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId > 123", ctx));
        assertFalse(evaluateBool("$productId > 123.0", ctx));
        assertTrue(evaluateBool("$productId > $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid > 0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI > 3.14", ctx));
        assertTrue(evaluateBool("$PI > 3", ctx));
        assertFalse(evaluateBool("$PI > $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > \"VOD.L\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > \"A\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > \"CLIENT1\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > \"VOD.L\" and $ric > $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $enabled", ctx));
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
    void comparisonWithContextTestsGreaterOrEqual() {
        final ExprContextImpl ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId >= 123", ctx));
        assertTrue(evaluateBool("$productId >= 123.0", ctx));
        assertTrue(evaluateBool("$productId >= $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid >= 0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI >= 3.14", ctx));
        assertTrue(evaluateBool("$PI >= 3", ctx));
        assertFalse(evaluateBool("$PI >= $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= \"VOD.L\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= \"A\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= \"CLIENT1\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= \"VOD.L\" and $ric >= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $enabled", ctx));
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
    void comparisonWithContextTestsLess() {
        final ExprContextImpl ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId < 123", ctx));
        assertFalse(evaluateBool("$productId < 123.0", ctx));
        assertFalse(evaluateBool("$productId < $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid < 0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI < 3.14", ctx));
        assertFalse(evaluateBool("$PI < 3", ctx));
        assertTrue(evaluateBool("$PI < $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < \"VOD.L\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < \"A\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < \"CLIENT1\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < \"VOD.L\" and $ric < $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $enabled", ctx));
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
    void comparisonWithContextTestsLessOrEqual() {
        final ExprContextImpl ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= false", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId <= 123", ctx));
        assertTrue(evaluateBool("$productId <= 123.0", ctx));
        assertFalse(evaluateBool("$productId <= $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid <= 0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI <= 3.14", ctx));
        assertFalse(evaluateBool("$PI <= 3", ctx));
        assertTrue(evaluateBool("$PI <= $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= $ric", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= \"VOD.L\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= \"A\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= \"CLIENT1\"", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= \"VOD.L\" and $ric <= $tuid", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $productId", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 123", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 0.0", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $PI", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= true", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $enabled", ctx));
        assertEquals("Operands must be numbers", runErr.getMessage());
    }
}