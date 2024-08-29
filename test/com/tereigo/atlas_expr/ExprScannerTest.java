package com.tereigo.atlas_expr;

import org.junit.jupiter.api.Test;

import static com.tereigo.atlas_expr.TokenType.AND;
import static com.tereigo.atlas_expr.TokenType.BETWEEN;
import static com.tereigo.atlas_expr.TokenType.COMMA;
import static com.tereigo.atlas_expr.TokenType.DIV;
import static com.tereigo.atlas_expr.TokenType.DOUBLE_NUMBER;
import static com.tereigo.atlas_expr.TokenType.EQUAL_EQUAL;
import static com.tereigo.atlas_expr.TokenType.FALSE;
import static com.tereigo.atlas_expr.TokenType.GREATER;
import static com.tereigo.atlas_expr.TokenType.IDENTIFIER;
import static com.tereigo.atlas_expr.TokenType.IN;
import static com.tereigo.atlas_expr.TokenType.LEFT_BRACKET;
import static com.tereigo.atlas_expr.TokenType.LEFT_PAREN;
import static com.tereigo.atlas_expr.TokenType.LONG_NUMBER;
import static com.tereigo.atlas_expr.TokenType.MINUS;
import static com.tereigo.atlas_expr.TokenType.MODULUS;
import static com.tereigo.atlas_expr.TokenType.MUL;
import static com.tereigo.atlas_expr.TokenType.NOT;
import static com.tereigo.atlas_expr.TokenType.NOT_EQUAL;
import static com.tereigo.atlas_expr.TokenType.OR;
import static com.tereigo.atlas_expr.TokenType.PLUS;
import static com.tereigo.atlas_expr.TokenType.RIGHT_BRACKET;
import static com.tereigo.atlas_expr.TokenType.RIGHT_PAREN;
import static com.tereigo.atlas_expr.TokenType.STRING;
import static com.tereigo.atlas_expr.TokenType.TRUE;
import static com.tereigo.atlas_expr.TokenType.WITHIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExprScannerTest {

    @Test
    void scannerTest() {
        testScanner("1", LONG_NUMBER);
        testScanner("1.0", DOUBLE_NUMBER);
        testScanner("\"A\"", STRING);
        testScanner("True", TRUE);
        testScanner("false", FALSE);
        testScanner("$ric in [\"A\", \"B\"]", IDENTIFIER, IN, LEFT_BRACKET, STRING, COMMA, STRING, RIGHT_BRACKET);
        testScanner("$productId in [1, 2]", IDENTIFIER, IN, LEFT_BRACKET, LONG_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("$ric in []", IDENTIFIER, IN, LEFT_BRACKET, RIGHT_BRACKET);
        testScanner("$ric in [ ]", IDENTIFIER, IN, LEFT_BRACKET, RIGHT_BRACKET);
        testScanner("$ric in [true]", IDENTIFIER, IN, LEFT_BRACKET, TRUE, RIGHT_BRACKET);
        testScanner("$ric in [1.0]", IDENTIFIER, IN, LEFT_BRACKET, DOUBLE_NUMBER, RIGHT_BRACKET);
        testScanner("1 within [1, 2]", LONG_NUMBER, WITHIN, LEFT_BRACKET, LONG_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("1.0 between [1.0, 2.0]", DOUBLE_NUMBER, BETWEEN, LEFT_BRACKET, DOUBLE_NUMBER, COMMA, DOUBLE_NUMBER, RIGHT_BRACKET);
        testScanner("(\"A\"+\"B\" == \"ABC\") or ($productId in [2,3] and 56.0 > 12.0 or 1*2 != 5/3) and not true",
                LEFT_PAREN, STRING, PLUS, STRING, EQUAL_EQUAL, STRING, RIGHT_PAREN, OR, LEFT_PAREN, IDENTIFIER, IN, LEFT_BRACKET, LONG_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET,
                AND, DOUBLE_NUMBER, GREATER, DOUBLE_NUMBER, OR, LONG_NUMBER, MUL, LONG_NUMBER, NOT_EQUAL, LONG_NUMBER, DIV, LONG_NUMBER, RIGHT_PAREN, AND, NOT, TRUE);
        // wrong input - should we reject it during scanning - no - it's done during parsing
        testScanner("$ric in [1.0, 2]", IDENTIFIER, IN, LEFT_BRACKET, DOUBLE_NUMBER, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("$ric in [\"A\", 1]", IDENTIFIER, IN, LEFT_BRACKET, STRING, COMMA, LONG_NUMBER, RIGHT_BRACKET);
        testScanner("10 % 3", LONG_NUMBER, MODULUS, LONG_NUMBER);
        testScanner("$now % 2", IDENTIFIER, MODULUS, LONG_NUMBER);
        testScanner("atlasRandom()", IDENTIFIER, LEFT_PAREN, RIGHT_PAREN);
        testScanner("now() % 2", IDENTIFIER, LEFT_PAREN, RIGHT_PAREN, MODULUS, LONG_NUMBER);
        testScanner("isEven($productId) % 2", IDENTIFIER, LEFT_PAREN, IDENTIFIER, RIGHT_PAREN, MODULUS, LONG_NUMBER);
        testScanner("isEven(1, 2.0, \"AB\", $tuid, true)", IDENTIFIER, LEFT_PAREN, LONG_NUMBER, COMMA, DOUBLE_NUMBER, COMMA, STRING, COMMA, IDENTIFIER, COMMA, TRUE, RIGHT_PAREN);
        testScanner("(10 % 3 + myFunc(1, $id)) - 2.3", LEFT_PAREN, LONG_NUMBER, MODULUS, LONG_NUMBER, PLUS, IDENTIFIER, LEFT_PAREN, LONG_NUMBER, COMMA, IDENTIFIER, RIGHT_PAREN, RIGHT_PAREN, MINUS, DOUBLE_NUMBER);
    }

    private void testScanner(String source, TokenType... expectedTypes) {
        ExprScanner scanner = new ExprScanner(source);
        int i = 0;
        for(TokenType type: expectedTypes) {
            assertEquals(type, scanner.tokens().get(i++).type);
        }
    }
}