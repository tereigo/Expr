package com.tereigo.expr.impl;

import org.junit.jupiter.api.Test;

import static com.tereigo.expr.impl.TokenType.AND;
import static com.tereigo.expr.impl.TokenType.BETWEEN;
import static com.tereigo.expr.impl.TokenType.COMMA;
import static com.tereigo.expr.impl.TokenType.DIV;
import static com.tereigo.expr.impl.TokenType.DOUBLE_NUMBER;
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

    private void testScanner(final String source, final TokenType... expectedTypes) {
        final ExprScanner scanner = new ExprScanner(source);
        int i = 0;
        for (final TokenType type : expectedTypes) {
            assertEquals(type, scanner.tokens().get(i++).type);
        }
    }
}