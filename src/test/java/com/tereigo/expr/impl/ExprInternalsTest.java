package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests for small package-private implementation details that aren't exercised through the
// public expression-language surface (builder validation/toString, Token convenience constructor, etc).
class ExprInternalsTest {

    @Test
    void contextBuilderAddAliasWithNullArgumentsThrows() {
        final ExprContextBuilder builder = ExprContextFactory.globalContext();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> builder.addAlias(null, "alias"));
        assertEquals("Empty name or alias", ex.getMessage());

        ex = assertThrows(RuntimeException.class, () -> builder.addAlias("name", null));
        assertEquals("Empty name or alias", ex.getMessage());
    }

    @Test
    void contextBuilderDuplicateFunctionNameThrows() {
        final ExprContextBuilder builder = ExprContextFactory.globalContext();
        builder.addLong("myValue", () -> 1L);
        final RuntimeException ex = assertThrows(RuntimeException.class, () -> builder.addLong("myValue", () -> 2L));
        assertEquals("Function 'myValue' is already defined", ex.getMessage());
    }

    @Test
    void contextBuilderToStringTest() {
        final ExprContextBuilder builder = ExprContextFactory.globalContext();
        builder.addLong("myValue", () -> 1L);
        assertTrue(builder.toString().contains("myValue"));
        assertTrue(builder.toString().startsWith("functions:"));
    }

    @Test
    void contextImplToStringTest() {
        final ExprContextBuilder builder = ExprContextFactory.globalContext();
        builder.addLong("myValue", () -> 1L);
        final ExprContext ctx = builder.getAsExprContext();
        assertTrue(ctx.toString().contains("myValue"));
        assertTrue(ctx.toString().startsWith("functions:"));
    }

    @Test
    void constantsBuilderDuplicateNameThrows() {
        final ExprConstantsBuilderImpl builder = new ExprConstantsBuilderImpl();
        builder.addLong("$a", 1L);
        final RuntimeException ex = assertThrows(RuntimeException.class, () -> builder.addLong("$a", 2L));
        assertEquals("Constant '$a' is already defined", ex.getMessage());
    }

    @Test
    void constantsBuilderToStringAndGetConstantsTest() {
        final ExprConstantsBuilderImpl builder = new ExprConstantsBuilderImpl();
        builder.addLong("$a", 1L);
        builder.addBool("$b", true);
        builder.addDouble("$c", 1.5);
        builder.addString("$d", "ABC");

        assertTrue(builder.toString().contains("$a"));
        assertTrue(builder.toString().startsWith("constants:"));

        assertEquals(4, builder.getConstants().size());
        assertTrue(builder.getConstants().containsKey("$a"));
        assertTrue(builder.getConstants().containsKey("$b"));
        assertTrue(builder.getConstants().containsKey("$c"));
        assertTrue(builder.getConstants().containsKey("$d"));
    }

    @Test
    void tokenThreeArgConstructorTest() {
        final Token token = new Token(TokenType.IDENTIFIER, "abc", null);
        assertEquals(TokenType.IDENTIFIER, token.type);
        assertEquals("abc", token.lexeme);
        assertEquals(0, token.line);
        assertEquals(0, token.pos);
        assertTrue(token.toString().contains("abc"));
    }
}
