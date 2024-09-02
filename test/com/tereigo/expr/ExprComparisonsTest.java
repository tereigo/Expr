package com.tereigo.expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExprComparisonsTest extends ExprEvaluatorTestBase {

    @Test
    void equalityTests() {
        RuntimeError runErr;
        assertTrue(evaluateBool("true == true"));
        assertTrue(evaluateBool("False == False"));
        assertFalse(evaluateBool("true == false"));
        assertFalse(evaluateBool("FALSE == true"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == 0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and LONG in expression 'true == 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and DOUBLE in expression 'true == 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and STRING in expression 'true == \"A\"'", runErr.getMessage());
        // long
        assertTrue(evaluateBool("0 == 0"));
        assertTrue(evaluateBool("123 == 123"));
        assertFalse(evaluateBool("0 == 1"));
        assertTrue(evaluateBool("0 == 0.0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 == true"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands of different types cannot be compared: LONG and BOOL in expression '0 == true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 == \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands of different types cannot be compared: LONG and STRING in expression '0 == \"\"'", runErr.getMessage());
        // double
        assertTrue(evaluateBool("0.0 == 0.0"));
        assertTrue(evaluateBool("123.0 == 123.0"));
        assertFalse(evaluateBool("0.0 == 1.0"));
        assertTrue(evaluateBool("0.0 == 0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 == true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BOOL in expression '0.0 == true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 == \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and STRING in expression '0.0 == \"\"'", runErr.getMessage());
        // String
        assertTrue(evaluateBool("\"\" == \"\""));
        assertTrue(evaluateBool("\"A\" == \"A\""));
        assertFalse(evaluateBool("\"A\" == \"B\""));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" == 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and LONG in expression '\"A\" == 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" == 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and LONG in expression '\"0\" == 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" == true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and BOOL in expression '\"A\" == true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" == true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands of different types cannot be compared: STRING and BOOL in expression '\"true\" == true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" == 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and DOUBLE in expression '\"A\" == 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" == 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: STRING and DOUBLE in expression '\"0.0\" == 0.0'", runErr.getMessage());
    }

    @Test
    void equalityWithContextTests() {
        final ExprContext ctx = createContext();

        RuntimeError runErr;
        assertTrue(evaluateBool("$enabled == true", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true == $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and BYTE_BUFFER in expression 'true == $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and BOOL in expression '$tuid == false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and BOOL in expression '$ric == false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands of different types cannot be compared: LONG and BOOL in expression '$productId == false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BOOL in expression '$PI == false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled == $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands of different types cannot be compared: BOOL and STRING in expression '$enabled == $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled == $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands of different types cannot be compared: BOOL and BYTE_BUFFER in expression '$enabled == $tuid'", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId == 123", ctx));
        assertTrue(evaluateBool("$productId == 123.0", ctx));
        assertFalse(evaluateBool("$productId == $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands of different types cannot be compared: LONG and STRING in expression '$productId == $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 == $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands of different types cannot be compared: LONG and BYTE_BUFFER in expression '0 == $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid == 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands of different types cannot be compared: BYTE_BUFFER and LONG in expression ' $tuid == 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId == $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands of different types cannot be compared: LONG and BOOL in expression '$productId == $enabled'", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI == 3.14", ctx));
        assertFalse(evaluateBool("$PI == 3", ctx));
        assertFalse(evaluateBool("$PI == $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and STRING in expression '$PI == $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 == $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BYTE_BUFFER in expression '0.0 == $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE in expression '$tuid == 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI == $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BOOL in expression '$PI == $enabled'", runErr.getMessage());
        // String
        assertTrue(evaluateBool("$ric == \"VOD.L\"", ctx));
        assertFalse(evaluateBool("$ric == $tuid", ctx));
        assertTrue(evaluateBool("\"CLIENT1\" == $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and LONG in expression '$ric == $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and LONG in expression '$ric == 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and DOUBLE in expression '$ric == 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and DOUBLE in expression '$ric == $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and BOOL in expression '$ric == true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric == $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and BOOL in expression '$ric == $enabled'", runErr.getMessage());
        // ByteBuffer
        assertFalse(evaluateBool("\"\" == $tuid", ctx));
        assertFalse(evaluateBool("$tuid == \"A\"", ctx));
        assertTrue(evaluateBool("$tuid == \"CLIENT1\"", ctx));
        assertTrue(evaluateBool("$tuid in [\"CLIENT1\"]", ctx));
        assertTrue(evaluateBool("$ric == \"VOD.L\" and $ric != $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and LONG in expression '$tuid == $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and LONG in expression '$tuid == 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE in expression '$tuid == 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE in expression '$tuid == $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and BOOL in expression '$tuid == true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid == $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and BOOL in expression '$tuid == $enabled'", runErr.getMessage());
    }

    @Test
    void nonEqualityTests() {
        RuntimeError runErr;
        assertFalse(evaluateBool("TRUE != True"));
        assertFalse(evaluateBool("FALSE != False"));
        assertTrue(evaluateBool("true != false"));
        assertTrue(evaluateBool("FALSE != true"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != 0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and LONG in expression 'true != 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and DOUBLE in expression 'true != 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and STRING in expression 'true != \"A\"'", runErr.getMessage());
        // long
        assertFalse(evaluateBool("0 != 0"));
        assertFalse(evaluateBool("123 != 123"));
        assertTrue(evaluateBool("0 != 1"));
        assertFalse(evaluateBool("0 != 0.0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 != true"));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands of different types cannot be compared: LONG and BOOL in expression '0 != true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 != \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands of different types cannot be compared: LONG and STRING in expression '0 != \"\"'", runErr.getMessage());
        // double
        assertFalse(evaluateBool("0.0 != 0.0"));
        assertFalse(evaluateBool("123.0 != 123.0"));
        assertTrue(evaluateBool("0.0 != 1.0"));
        assertFalse(evaluateBool("0.0 != 0"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 != true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BOOL in expression '0.0 != true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 != \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and STRING in expression '0.0 != \"\"'", runErr.getMessage());
        // String
        assertFalse(evaluateBool("\"\" != \"\""));
        assertFalse(evaluateBool("\"A\" != \"A\""));
        assertTrue(evaluateBool("\"A\" != \"B\""));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" != 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and LONG in expression '\"A\" != 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" != 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and LONG in expression '\"0\" != 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" != true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and BOOL in expression '\"A\" != true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" != true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands of different types cannot be compared: STRING and BOOL in expression '\"true\" != true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" != 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: STRING and DOUBLE in expression '\"A\" != 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" != 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: STRING and DOUBLE in expression '\"0.0\" != 0.0'", runErr.getMessage());
    }

    @Test
    void nonEqualityWithContextTests() {
        final ExprContext ctx = createContext();

        RuntimeError runErr;
        assertFalse(evaluateBool("$enabled != true", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true != $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: BOOL and BYTE_BUFFER in expression 'true != $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and BOOL in expression '$tuid != false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and BOOL in expression '$ric != false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands of different types cannot be compared: LONG and BOOL in expression '$productId != false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BOOL in expression '$PI != false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled != $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands of different types cannot be compared: BOOL and STRING in expression '$enabled != $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled != $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands of different types cannot be compared: BOOL and BYTE_BUFFER in expression '$enabled != $tuid'", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId != 123", ctx));
        assertFalse(evaluateBool("$productId != 123.0", ctx));
        assertTrue(evaluateBool("$productId != $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands of different types cannot be compared: LONG and STRING in expression '$productId != $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 != $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands of different types cannot be compared: LONG and BYTE_BUFFER in expression '0 != $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid != 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands of different types cannot be compared: BYTE_BUFFER and LONG in expression ' $tuid != 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId != $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands of different types cannot be compared: LONG and BOOL in expression '$productId != $enabled'", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI != 3.14", ctx));
        assertTrue(evaluateBool("$PI != 3", ctx));
        assertTrue(evaluateBool("$PI != $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and STRING in expression '$PI != $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 != $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BYTE_BUFFER in expression '0.0 != $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE in expression '$tuid != 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI != $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands of different types cannot be compared: DOUBLE and BOOL in expression '$PI != $enabled'", runErr.getMessage());
        // String
        assertFalse(evaluateBool("$ric != \"VOD.L\"", ctx));
        assertTrue(evaluateBool("$ric != $tuid", ctx));
        assertFalse(evaluateBool("\"CLIENT1\" != $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and LONG in expression '$ric != $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and LONG in expression '$ric != 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and DOUBLE in expression '$ric != 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and DOUBLE in expression '$ric != $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and BOOL in expression '$ric != true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric != $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands of different types cannot be compared: STRING and BOOL in expression '$ric != $enabled'", runErr.getMessage());
        // ByteBuffer
        assertTrue(evaluateBool("\"\" != $tuid", ctx));
        assertTrue(evaluateBool("$tuid != \"A\"", ctx));
        assertFalse(evaluateBool("$tuid != \"CLIENT1\"", ctx));
        assertFalse(evaluateBool("$ric != \"VOD.L\" and $ric != $tuid", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and LONG in expression '$tuid != $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and LONG in expression '$tuid != 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE in expression '$tuid != 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and DOUBLE in expression '$tuid != $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and BOOL in expression '$tuid != true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid != $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands of different types cannot be compared: BYTE_BUFFER and BOOL in expression '$tuid != $enabled'", runErr.getMessage());
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
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 > true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 > \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 > \"\"'", runErr.getMessage());
        // double
        assertTrue(evaluateBool("1.0 > 0.0"));
        assertFalse(evaluateBool("123.0 > 123.0"));
        assertFalse(evaluateBool("0.0 > 1.0"));
        assertTrue(evaluateBool("1.0 > 0"));
        assertTrue(evaluateBool("1 > 0.0"));
        assertFalse(evaluateBool("1.0 > 1"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 > true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 > true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 > \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 > \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" > \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" > \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" > \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" > 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" > 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" > 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" > true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" > true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" > true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" > 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" > 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" > 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" > 0.0'", runErr.getMessage());
    }

    @Test
    void comparisonWithContextTestsGreater() {
        final ExprContext ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled > true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true > $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true > $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId > false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI > false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled > $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled > $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled > $tuid'", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId > 123", ctx));
        assertFalse(evaluateBool("$productId > 123.0", ctx));
        assertTrue(evaluateBool("$productId > $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId > $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 > $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 > $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid > 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression ' $tuid > 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId > $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId > $enabled'", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI > 3.14", ctx));
        assertTrue(evaluateBool("$PI > 3", ctx));
        assertFalse(evaluateBool("$PI > $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI > $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 > $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 > $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI > $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI > $enabled'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > \"VOD.L\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > \"VOD.L\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" > $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 11]: Operands must be numbers in expression '\"CLIENT1\" > $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > $enabled'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" > $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" > $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > \"CLIENT1\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > \"CLIENT1\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric > \"VOD.L\" and $ric > $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric > \"VOD.L\" and $ric > $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid > $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid > $enabled'", runErr.getMessage());
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
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 >= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 >= \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 >= \"\"'", runErr.getMessage());
        // double
        assertTrue(evaluateBool("1.0 >= 0.0"));
        assertTrue(evaluateBool("123.0 >= 123.0"));
        assertFalse(evaluateBool("0.0 >= 1.0"));
        assertTrue(evaluateBool("1.0 >= 0"));
        assertTrue(evaluateBool("1 >= 0.0"));
        assertTrue(evaluateBool("1.0 >= 1"));
        assertFalse(evaluateBool("1.0 >= 10"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 >= true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 >= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 >= \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 >= \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" >= \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" >= \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" >= \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" >= 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" >= 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" >= 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" >= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" >= true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" >= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" >= 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" >= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" >= 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" >= 0.0'", runErr.getMessage());
    }

    @Test
    void comparisonWithContextTestsGreaterOrEqual() {
        final ExprContext ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled >= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true >= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true >= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId >= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI >= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled >= $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled >= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled >= $tuid'", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId >= 123", ctx));
        assertTrue(evaluateBool("$productId >= 123.0", ctx));
        assertTrue(evaluateBool("$productId >= $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId >= $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 >= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 >= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid >= 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression ' $tuid >= 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId >= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId >= $enabled'", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI >= 3.14", ctx));
        assertTrue(evaluateBool("$PI >= 3", ctx));
        assertFalse(evaluateBool("$PI >= $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI >= $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 >= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 >= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI >= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI >= $enabled'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= \"VOD.L\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= \"VOD.L\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" >= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 11]: Operands must be numbers in expression '\"CLIENT1\" >= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= $enabled'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" >= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" >= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= \"CLIENT1\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= \"CLIENT1\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric >= \"VOD.L\" and $ric >= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric >= \"VOD.L\" and $ric >= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid >= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid >= $enabled'", runErr.getMessage());
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
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 < true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 < \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 < \"\"'", runErr.getMessage());
        // double
        assertFalse(evaluateBool("1.0 < 0.0"));
        assertFalse(evaluateBool("123.0 < 123.0"));
        assertTrue(evaluateBool("0.0 < 1.0"));
        assertFalse(evaluateBool("1.0 < 0"));
        assertFalse(evaluateBool("1 < 0.0"));
        assertFalse(evaluateBool("1.0 < 1"));
        assertTrue(evaluateBool("1.0 < 10"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 < true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 < true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 < \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 < \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" < \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" < \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" < \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" < 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" < 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" < 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" < true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" < true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" < true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" < 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" < 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" < 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" < 0.0'", runErr.getMessage());
    }

    @Test
    void comparisonWithContextTestsLess() {
        final ExprContext ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled < true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true < $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true < $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId < false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI < false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled < $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled < $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled < $tuid'", runErr.getMessage());
        // long
        assertFalse(evaluateBool("$productId < 123", ctx));
        assertFalse(evaluateBool("$productId < 123.0", ctx));
        assertFalse(evaluateBool("$productId < $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId < $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 < $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 < $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid < 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression ' $tuid < 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId < $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId < $enabled'", runErr.getMessage());
        // double
        assertFalse(evaluateBool("$PI < 3.14", ctx));
        assertFalse(evaluateBool("$PI < 3", ctx));
        assertTrue(evaluateBool("$PI < $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI < $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 < $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 < $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI < $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI < $enabled'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < \"VOD.L\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < \"VOD.L\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" < $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 11]: Operands must be numbers in expression '\"CLIENT1\" < $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < $enabled'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" < $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" < $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < \"CLIENT1\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < \"CLIENT1\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric < \"VOD.L\" and $ric < $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric < \"VOD.L\" and $ric < $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid < $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid < $enabled'", runErr.getMessage());
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
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 <= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 <= \"\""));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 <= \"\"'", runErr.getMessage());
        // double
        assertFalse(evaluateBool("1.0 <= 0.0"));
        assertTrue(evaluateBool("123.0 <= 123.0"));
        assertTrue(evaluateBool("0.0 <= 1.0"));
        assertFalse(evaluateBool("1.0 <= 0"));
        assertFalse(evaluateBool("1 <= 0.0"));
        assertTrue(evaluateBool("1.0 <= 1"));
        assertTrue(evaluateBool("1.0 <= 10"));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 <= true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 <= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 <= \"\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 <= \"\"'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" <= \"\""));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" <= \"\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= \"A\""));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" <= \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" <= 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0\" <= 0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"0\" <= 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= true"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" <= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"true\" <= true"));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression '\"true\" <= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"A\" <= 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '\"A\" <= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"0.0\" <= 0.0"));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '\"0.0\" <= 0.0'", runErr.getMessage());
    }

    @Test
    void comparisonWithContextTestsLessOrEqual() {
        final ExprContext ctx = createContext();

        RuntimeError runErr;
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled <= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("true <= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression 'true <= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId <= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= false", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI <= false'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled <= $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$enabled <= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 10]: Operands must be numbers in expression '$enabled <= $tuid'", runErr.getMessage());
        // long
        assertTrue(evaluateBool("$productId <= 123", ctx));
        assertTrue(evaluateBool("$productId <= 123.0", ctx));
        assertFalse(evaluateBool("$productId <= $PI", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId <= $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0 <= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 3]: Operands must be numbers in expression '0 <= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool(" $tuid <= 0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 8]: Operands must be numbers in expression ' $tuid <= 0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$productId <= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 12]: Operands must be numbers in expression '$productId <= $enabled'", runErr.getMessage());
        // double
        assertTrue(evaluateBool("$PI <= 3.14", ctx));
        assertFalse(evaluateBool("$PI <= 3", ctx));
        assertTrue(evaluateBool("$PI <= $productId", ctx));
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= $ric", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI <= $ric'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("0.0 <= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '0.0 <= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$PI <= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 5]: Operands must be numbers in expression '$PI <= $enabled'", runErr.getMessage());
        // String
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= \"VOD.L\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= \"VOD.L\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"CLIENT1\" <= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 11]: Operands must be numbers in expression '\"CLIENT1\" <= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= $enabled'", runErr.getMessage());
        // ByteBuffer
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("\"\" <= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 4]: Operands must be numbers in expression '\"\" <= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= \"A\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= \"A\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= \"CLIENT1\"", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= \"CLIENT1\"'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$ric <= \"VOD.L\" and $ric <= $tuid", ctx));
        assertEquals("Expression evaluation error [line 1, pos 6]: Operands must be numbers in expression '$ric <= \"VOD.L\" and $ric <= $tuid'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $productId", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= $productId'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 123", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= 123'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= 0.0", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= 0.0'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $PI", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= $PI'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= true", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= true'", runErr.getMessage());
        runErr = assertThrows(RuntimeError.class, () -> evaluateBool("$tuid <= $enabled", ctx));
        assertEquals("Expression evaluation error [line 1, pos 7]: Operands must be numbers in expression '$tuid <= $enabled'", runErr.getMessage());
    }
}