package com.tereigo.expr.impl;

import org.junit.jupiter.api.Test;

import static com.tereigo.expr.impl.TokenType.AND;
import static com.tereigo.expr.impl.TokenType.BETWEEN;
import static com.tereigo.expr.impl.TokenType.COMMA;
import static com.tereigo.expr.impl.TokenType.DIV;
import static com.tereigo.expr.impl.TokenType.DOT;
import static com.tereigo.expr.impl.TokenType.DOUBLE_NUMBER;
import static com.tereigo.expr.impl.TokenType.EOF;
import static com.tereigo.expr.impl.TokenType.EQUAL_EQUAL;
import static com.tereigo.expr.impl.TokenType.FALSE;
import static com.tereigo.expr.impl.TokenType.GREATER;
import static com.tereigo.expr.impl.TokenType.IDENTIFIER;
import static com.tereigo.expr.impl.TokenType.IN;
import static com.tereigo.expr.impl.TokenType.LEFT_BRACKET;
import static com.tereigo.expr.impl.TokenType.LEFT_PAREN;
import static com.tereigo.expr.impl.TokenType.LESS;
import static com.tereigo.expr.impl.TokenType.LONG_NUMBER;
import static com.tereigo.expr.impl.TokenType.MINUS;
import static com.tereigo.expr.impl.TokenType.MODULUS;
import static com.tereigo.expr.impl.TokenType.MUL;
import static com.tereigo.expr.impl.TokenType.NOT;
import static com.tereigo.expr.impl.TokenType.NOT_EQUAL;
import static com.tereigo.expr.impl.TokenType.OR;
import static com.tereigo.expr.impl.TokenType.PLUS;
import static com.tereigo.expr.impl.TokenType.RIGHT_BRACKET;
import static com.tereigo.expr.impl.TokenType.RIGHT_PAREN;
import static com.tereigo.expr.impl.TokenType.STRING;
import static com.tereigo.expr.impl.TokenType.TRUE;
import static com.tereigo.expr.impl.TokenType.WITHIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExprScannerTest {

    @Test
    void scannerTest() {
        testScanner("1", LONG_NUMBER);
        testScanner("1.0", DOUBLE_NUMBER);
        testScanner("\"A\"", STRING);
        testScanner("True", TRUE);
        testScanner("false", FALSE);
        testScanner("not false", NOT, FALSE);
        testScanner("!false", NOT, FALSE);
        testScanner("not(false)", NOT, LEFT_PAREN, FALSE, RIGHT_PAREN);
        testScanner("!(false)", NOT, LEFT_PAREN, FALSE, RIGHT_PAREN);
        testScanner("not((false))", NOT, LEFT_PAREN, LEFT_PAREN, FALSE, RIGHT_PAREN, RIGHT_PAREN);
        testScanner("not(false) and -5 < -4.0", NOT, LEFT_PAREN, FALSE, RIGHT_PAREN, AND, MINUS, LONG_NUMBER, LESS, MINUS, DOUBLE_NUMBER);
        testScanner("$ric in [\"A\", \"B\"]", IDENTIFIER, IN, LEFT_BRACKET, STRING, COMMA, STRING, RIGHT_BRACKET);
        testScanner("$productId in [1, 2]", IDENTIFIER, IN, LEFT_BRACKET, LONG_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("$ric in []", IDENTIFIER, IN, LEFT_BRACKET, RIGHT_BRACKET);
        testScanner("$ric in [ ]", IDENTIFIER, IN, LEFT_BRACKET, RIGHT_BRACKET);
        testScanner("$ric in [true]", IDENTIFIER, IN, LEFT_BRACKET, TRUE, RIGHT_BRACKET);
        testScanner("$ric in [1.0]", IDENTIFIER, IN, LEFT_BRACKET, DOUBLE_NUMBER, RIGHT_BRACKET);
        testScanner("$ric not in [1]", IDENTIFIER, NOT, IN, LEFT_BRACKET, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("1 within [1, 2]", LONG_NUMBER, WITHIN, LEFT_BRACKET, LONG_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("1 not within [1, 2]", LONG_NUMBER, NOT, WITHIN, LEFT_BRACKET, LONG_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("1.0 between [1.0, 2.0]", DOUBLE_NUMBER, BETWEEN, LEFT_BRACKET, DOUBLE_NUMBER, COMMA, DOUBLE_NUMBER, RIGHT_BRACKET);
        testScanner("1.0 not between [1.0, 2.0]", DOUBLE_NUMBER, NOT, BETWEEN, LEFT_BRACKET, DOUBLE_NUMBER, COMMA, DOUBLE_NUMBER, RIGHT_BRACKET);
        testScanner("(\"A\"+\"B\" == \"ABC\") or ($productId in [2,3] and 56.0 > 12.0 or 1*2 != 5/3) and not true",
                LEFT_PAREN, STRING, PLUS, STRING, EQUAL_EQUAL, STRING, RIGHT_PAREN, OR, LEFT_PAREN, IDENTIFIER, IN, LEFT_BRACKET, LONG_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET,
                AND, DOUBLE_NUMBER, GREATER, DOUBLE_NUMBER, OR, LONG_NUMBER, MUL, LONG_NUMBER, NOT_EQUAL, LONG_NUMBER, DIV, LONG_NUMBER, RIGHT_PAREN, AND, NOT, TRUE);
        // wrong input - should we reject it during scanning - no - it's done during parsing
        testScanner("$ric in [1.0, 2]", IDENTIFIER, IN, LEFT_BRACKET, DOUBLE_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("$ric in [\"A\", 1]", IDENTIFIER, IN, LEFT_BRACKET, STRING, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("$ric in ['A', 1]", IDENTIFIER, IN, LEFT_BRACKET, STRING, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("10 % 3", LONG_NUMBER, MODULUS, LONG_NUMBER);
        testScanner("$now % 2", IDENTIFIER, MODULUS, LONG_NUMBER);
        testScanner("falconRandom()", IDENTIFIER, LEFT_PAREN, RIGHT_PAREN);
        testScanner("now() % 2", IDENTIFIER, LEFT_PAREN, RIGHT_PAREN, MODULUS, LONG_NUMBER);
        testScanner("isEven($productId) % 2", IDENTIFIER, LEFT_PAREN, IDENTIFIER, RIGHT_PAREN, MODULUS, LONG_NUMBER);
        testScanner("isEven(1, 2.0, \"AB\", $tuid, true)", IDENTIFIER, LEFT_PAREN, LONG_NUMBER, COMMA, DOUBLE_NUMBER, COMMA, STRING, COMMA, IDENTIFIER, COMMA, TRUE, RIGHT_PAREN);
        testScanner("isEven(1, 2.0, 'AB', $tuid, true)", IDENTIFIER, LEFT_PAREN, LONG_NUMBER, COMMA, DOUBLE_NUMBER, COMMA, STRING, COMMA, IDENTIFIER, COMMA, TRUE, RIGHT_PAREN);
        testScanner("(10 % 3 + myFunc(1, $id)) - 2.3", LEFT_PAREN, LONG_NUMBER, MODULUS, LONG_NUMBER, PLUS, IDENTIFIER, LEFT_PAREN, LONG_NUMBER, COMMA, IDENTIFIER, RIGHT_PAREN, RIGHT_PAREN, MINUS, DOUBLE_NUMBER);
    }

    @Test
    void scanNumbersTest() {
        testScanner("0", LONG_NUMBER);
        testScanner("1", LONG_NUMBER);
        testScanner("123", LONG_NUMBER);
        testScanner("0.0", DOUBLE_NUMBER);
        testScanner("1.0", DOUBLE_NUMBER);
        testScanner("1.", LONG_NUMBER, DOT);
        testScanner("0.", LONG_NUMBER, DOT);
        testScanner("1.2", DOUBLE_NUMBER);
        testScanner("123.456", DOUBLE_NUMBER);
        testScanner("1e5", DOUBLE_NUMBER);
        testScanner("1E5", DOUBLE_NUMBER);
        testScanner("1.e5", DOUBLE_NUMBER);
        testScanner("1.E5", DOUBLE_NUMBER);
        testScanner("0.2e03", DOUBLE_NUMBER);
        testScanner("0.2E03", DOUBLE_NUMBER);
        testScanner("1.123e-15", DOUBLE_NUMBER);
        testScanner("1.123E-15", DOUBLE_NUMBER);
        testScanner("3.1e+5", DOUBLE_NUMBER);
        testScanner("3.1E+5", DOUBLE_NUMBER);
        testScanner("1e+5", DOUBLE_NUMBER);
        testScanner("1E+5", DOUBLE_NUMBER);
        testScanner("1.e+5", DOUBLE_NUMBER);
        testScanner("1.E+5", DOUBLE_NUMBER);
        testScanner("1.e-5", DOUBLE_NUMBER);
        testScanner("1.E-5", DOUBLE_NUMBER);
        testScanner("5.toDouble()", LONG_NUMBER, DOT, IDENTIFIER, LEFT_PAREN, RIGHT_PAREN);
        testScanner("1.e+1-2.e-3+4.0e-5", DOUBLE_NUMBER, MINUS, DOUBLE_NUMBER, PLUS, DOUBLE_NUMBER);
    }

    @Test
    void scanEdgeNumbersTest() {
        testScanner("9223372036854775806", LONG_NUMBER);
        testScanner("9223372036854775806.", LONG_NUMBER, DOT); // ???
        testScanner("9223372036854775806.0", DOUBLE_NUMBER);
        testScanner("9223372036854775806.1", DOUBLE_NUMBER);
        testScanner("-9223372036854775806", MINUS, LONG_NUMBER);
        testScanner("-9223372036854775806.", MINUS, LONG_NUMBER, DOT); // ???
        testScanner("-9223372036854775806.0", MINUS, DOUBLE_NUMBER);
        testScanner("9223372036854775807", LONG_NUMBER);
        testScanner("9223372036854775807.", LONG_NUMBER, DOT); // ???
        testScanner("9223372036854775807.0", DOUBLE_NUMBER);
        testScanner("-9223372036854775807", MINUS, LONG_NUMBER);
        testScanner("-9223372036854775807.", MINUS, LONG_NUMBER, DOT); // ???
        testScanner("-9223372036854775807.0", MINUS, DOUBLE_NUMBER);
        testScanner("9223372036854775808", DOUBLE_NUMBER);
        testScanner("9223372036854775808.", DOUBLE_NUMBER, DOT); // ???
        testScanner("9223372036854775808.0", DOUBLE_NUMBER);
        testScanner("-9223372036854775808", LONG_NUMBER);
        testScanner("-9223372036854775808.", LONG_NUMBER, DOT); // ???
        testScanner("-9223372036854775808.0", MINUS, DOUBLE_NUMBER);
        testScanner("9223372036854775809", DOUBLE_NUMBER);
        testScanner("9223372036854775809.", DOUBLE_NUMBER, DOT); // ???
        testScanner("9223372036854775809.0", DOUBLE_NUMBER);
        testScanner("-9223372036854775809", MINUS, DOUBLE_NUMBER);
        testScanner("-9223372036854775809.", MINUS, DOUBLE_NUMBER, DOT);
        testScanner("-9223372036854775809.0", MINUS, DOUBLE_NUMBER);
    }

    @Test
    void scanCommentsTest() {
        testScanner("1 // trailing comment with nothing after it", LONG_NUMBER);
        testScanner("1 + 2 // comment", LONG_NUMBER, PLUS, LONG_NUMBER);
        testScanner("1 // comment\n+ 2", LONG_NUMBER, PLUS, LONG_NUMBER);
    }

    @Test
    void scanNewlinesTest() {
        testScanner("1\n+\n2", LONG_NUMBER, PLUS, LONG_NUMBER);
        testScanner("\n\n1", LONG_NUMBER);
        final ParseError err = assertThrows(ParseError.class, () -> new ExprScanner("1\n@"));
        assertEquals("[line 2, pos 3]: Unexpected character", err.getMessage());
    }

    @Test
    void scanLoneBangTest() {
        // '!' as the very last character of the source (isAtEnd() branch inside match())
        testScanner("!", NOT);
    }

    @Test
    void scanInvalidCharacterTest() {
        final ParseError err = assertThrows(ParseError.class, () -> new ExprScanner("@"));
        assertEquals("[line 1, pos 1]: Unexpected character", err.getMessage());
    }

    @Test
    void scanBadEqualsTest() {
        final ParseError err = assertThrows(ParseError.class, () -> new ExprScanner("1 = 2"));
        assertEquals("[line 1, pos 3]: Expected '==' comparison not found", err.getMessage());
    }

    @Test
    void scanUnterminatedStringWithNewlineTest() {
        // exercises the '\n' handling inside string() before the unterminated-string error
        final ParseError err = assertThrows(ParseError.class, () -> new ExprScanner("\"line1\nline2"));
        assertEquals("[line 2, pos 1]: Unterminated string", err.getMessage());
    }

    @Test
    void scanMultiLineStringTest() {
        testScanner("\"line1\nline2\"", STRING);
    }

    @Test
    void scanInvalidNumberFormatTest() {
        ParseError err = assertThrows(ParseError.class, () -> new ExprScanner("1e+"));
        assertEquals("[line 1, pos 1]: Invalid number format", err.getMessage());

        err = assertThrows(ParseError.class, () -> new ExprScanner("1e-"));
        assertEquals("[line 1, pos 1]: Invalid number format", err.getMessage());
    }

    @Test
    void scanUnderscoreAndDollarIdentifiersTest() {
        // isAlpha() allows '_'/'$' to start an identifier, and isAlphaNumeric() must allow them
        // to continue one too - otherwise e.g. "c_x" silently splits into two adjacent
        // identifier tokens ("c" and "_x") instead of scanning as a single "c_x" token.
        testScanner("c_x", IDENTIFIER);
        testScanner("my_variable_name", IDENTIFIER);
        testScanner("client_id", IDENTIFIER);
        testScanner("_leading", IDENTIFIER);
        testScanner("trailing_", IDENTIFIER);
        testScanner("$a_b", IDENTIFIER);
        testScanner("a$b", IDENTIFIER);
        testScanner("a_b$c_d", IDENTIFIER);
        testScanner("true and c_x()", TRUE, AND, IDENTIFIER, LEFT_PAREN, RIGHT_PAREN);
        testScanner("(c_x == 1)", LEFT_PAREN, IDENTIFIER, EQUAL_EQUAL, LONG_NUMBER, RIGHT_PAREN);

        assertLexeme("c_x", "c_x");
        assertLexeme("my_variable_name", "my_variable_name");
        assertLexeme("client_id", "client_id");
        assertLexeme("_leading", "_leading");
        assertLexeme("trailing_", "trailing_");
        assertLexeme("$a_b", "$a_b");
        assertLexeme("a_b$c_d", "a_b$c_d");
    }

    private void assertLexeme(final String source, final String expectedLexeme) {
        final ExprScanner scanner = new ExprScanner(source);
        assertEquals(1, scanner.tokens().size() - 1, "Actual tokens: " + scanner.tokens());
        assertEquals(expectedLexeme, scanner.tokens().get(0).lexeme);
    }

    private void testScanner(final String source, final TokenType... expectedTypes) {
        final ExprScanner scanner = new ExprScanner(source);
        int i = 0;
        for (final TokenType type : expectedTypes) {
            assertEquals(type, scanner.tokens().get(i++).type);
        }
        assertEquals(expectedTypes.length, scanner.tokens().size() - 1, "Actual tokens: " + scanner.tokens());
        assertEquals(EOF, scanner.tokens().get(scanner.tokens().size() - 1).type);
    }
}