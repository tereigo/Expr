package com.tereigo.expr.utils;

import org.junit.jupiter.api.Test;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.utils.ByteBufferUtils.contains;
import static com.tereigo.expr.utils.ByteBufferUtils.indexOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ConstantValue")
class ByteBufferUtilsTest {

    @Test
    void referenceContainsStringTests() {
        assertTrue("".contains(""));
        assertFalse("".contains("A"));
        assertFalse("".contains("ABC"));
        assertTrue("A".contains(""));
        assertTrue("ABC".contains(""));

        assertTrue("ABC".contains("A"));
        assertTrue("ABC".contains("B"));
        assertTrue("ABC".contains("C"));
        assertTrue("ABC".contains("BC"));
        assertTrue("ABC".contains("ABC"));
        assertFalse("ABC".contains("D"));
        assertFalse("ABC".contains("CD"));
        assertFalse("ABC".contains("ABCD"));
    }

    @Test
    void containsTests() {
        assertTrue(contains(constant(""), ""));
        assertFalse(contains(constant(""), "A"));
        assertFalse(contains(constant(""), "ABC"));
        assertTrue(contains(constant("A"), ""));
        assertTrue(contains(constant("ABC"), ""));

        assertTrue(contains(constant("ABC"), "A"));
        assertTrue(contains(constant("ABC"), "B"));
        assertTrue(contains(constant("ABC"), "C"));
        assertTrue(contains(constant("ABC"), "BC"));
        assertTrue(contains(constant("ABC"), "ABC"));
        assertFalse(contains(constant("ABC"), "D"));
        assertFalse(contains(constant("ABC"), "CD"));
        assertFalse(contains(constant("ABC"), "ABCD"));

        assertTrue(contains(constant("aaaaabbb"), "a"));
        assertTrue(contains(constant("aaaaabbb"), "aa"));
        assertTrue(contains(constant("aaaaabbb"), "aaa"));
        assertTrue(contains(constant("aaaaabbb"), "aaaa"));
        assertTrue(contains(constant("aaaaabbb"), "aaaaa"));
        assertFalse(contains(constant("aaaaabbb"), "aaaaaa"));
        assertTrue(contains(constant("aaaaabbb"), "aaaaab"));
        assertTrue(contains(constant("aaaaabbb"), "aaaaabb"));
        assertTrue(contains(constant("aaaaabbb"), "aabbb"));
        assertTrue(contains(constant("aaaaabbb"), "aaabbb"));
        assertTrue(contains(constant("aaaaabbb"), "aaaabbb"));
        assertFalse(contains(constant("aaaaabbb"), "aaaabbbb"));
        assertTrue(contains(constant("aaaaabbb"), "aaaaabbb"));

        assertTrue(contains(constant("To be or not to be that is a question"), "To be"));
        assertTrue(contains(constant("To be or not to be that is a question"), "o be that"));
        assertTrue(contains(constant("To be or not to be that is a question"), "To be or not to be that is"));
        assertFalse(contains(constant("To be or not to be that is a question"), "To be or not to be that was"));
        assertTrue(contains(constant("To be or not to be that is a question"), "question"));
        assertFalse(contains(constant("To be or not to be that is a question"), "question!"));
        assertFalse(contains(constant("To be or not to be that is a question"), "questiom"));
        assertTrue(contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "Gradle"));
        assertFalse(contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "gradle"));
        assertTrue(contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "the following"));
        assertTrue(contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "following"));
        assertTrue(contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "you can do either of the following"));
        assertTrue(contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"));
    }

    @Test
    void referenceIndexOfStringTests() {
        assertEquals(0, "".indexOf(""));
        assertEquals(-1, "".indexOf("A"));
        assertEquals(-1, "".indexOf("ABC"));
        assertEquals(0, "A".indexOf(""));
        assertEquals(0, "ABC".indexOf(""));

        assertEquals(0, "ABC".indexOf("A"));
        assertEquals(1, "ABC".indexOf("B"));
        assertEquals(2, "ABC".indexOf("C"));
        assertEquals(1, "ABC".indexOf("BC"));
        assertEquals(0, "ABC".indexOf("ABC"));
        assertEquals(-1, "ABC".indexOf("D"));
        assertEquals(-1, "ABC".indexOf("CD"));
        assertEquals(-1, "ABC".indexOf("ABCD"));
    }

    @Test
    void indexOfTests() {
        assertEquals(0, indexOf(constant(""), ""));
        assertEquals(-1, indexOf(constant(""), "A"));
        assertEquals(-1, indexOf(constant(""), "ABC"));
        assertEquals(0, indexOf(constant("A"), ""));
        assertEquals(0, indexOf(constant("ABC"), ""));

        assertEquals(0, indexOf(constant("ABC"), "A"));
        assertEquals(1, indexOf(constant("ABC"), "B"));
        assertEquals(2, indexOf(constant("ABC"), "C"));
        assertEquals(1, indexOf(constant("ABC"), "BC"));
        assertEquals(0, indexOf(constant("ABC"), "ABC"));
        assertEquals(-1, indexOf(constant("ABC"), "D"));
        assertEquals(-1, indexOf(constant("ABC"), "CD"));
        assertEquals(-1, indexOf(constant("ABC"), "ABCD"));
    }
}
