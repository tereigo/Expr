package com.tereigo.expr.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.utils.ByteBufferUtils.contains;
import static com.tereigo.expr.utils.ByteBufferUtils.indexOf;
import static com.tereigo.expr.utils.ByteBufferUtils.isEmpty;
import static com.tereigo.expr.utils.ByteBufferUtils.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ConstantValue")
class ByteBufferUtilsTest {

    @Test
    void isEmptyTests() {
        assertTrue(isEmpty(constant("")));
        assertFalse(isEmpty(constant("A")));
        assertFalse(isEmpty(constant("ABC")));
        assertFalse(isEmpty(constant(" ")));
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', true",
            "'', A, false",
            "'', ABC, false",
            "A, '', true",
            "ABC, '', true",
            "ABC, A, true",
            "ABC, B, true",
            "ABC, C, true",
            "ABC, BC, true",
            "ABC, ABC, true",
            "ABC, D, false",
            "ABC, CD, false",
            "ABC, ABCD, false",

            "aaaaabbb, a, true",
            "aaaaabbb, aa, true",
            "aaaaabbb, aaa, true",
            "aaaaabbb, aaaa, true",
            "aaaaabbb, aaaaa, true",
            "aaaaabbb, aaaaaa, false",
            "aaaaabbb, aaaaab, true",
            "aaaaabbb, aaaaabb, true",
            "aaaaabbb, aabbb, true",
            "aaaaabbb, aaaabbb, true",
            "aaaaabbb, aaaabbbbb, false",
            "aaaaabbb, aaaaabbb, true",

            "To be or not to be that is a question, To be, true",
            "To be or not to be that is a question, o be that, true",
            "To be or not to be that is a question, To be or not to be that is, true",
            "To be or not to be that is a question, To be or not to be that was, false",
            "To be or not to be that is a question, question, true",
            "To be or not to be that is a question, question!, false",
            "To be or not to be that is a question, questiom, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, Gradle, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, the following, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, following, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, you can do either of the following, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, true",
    })
    void containsTests(String str, String pattern, boolean expected) {
        assertEquals(expected, str.contains(pattern));
        assertEquals(expected, contains(constant(str), pattern));
        assertEquals(expected, contains(str, constant(pattern)));
        assertEquals(expected, contains(constant(str), constant(pattern)));
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', true",
            "'', A, false",
            "'', ABC, false",
            "A, '', true",
            "ABC, '', true",
            "ABC, A, true",
            "ABC, B, false",
            "ABC, C, false",
            "ABC, BC, false",
            "ABC, ABC, true",
            "ABC, D, false",
            "ABC, CD, false",
            "ABC, ABCD, false",

            "aaaaabbb, a, true",
            "aaaaabbb, aa, true",
            "aaaaabbb, aaa, true",
            "aaaaabbb, aaaa, true",
            "aaaaabbb, aaaaa, true",
            "aaaaabbb, aaaaaa, false",
            "aaaaabbb, aaaaab, true",
            "aaaaabbb, aaaaabb, true",
            "aaaaabbb, aabbb, false",
            "aaaaabbb, aaaabbb, false",
            "aaaaabbb, aaaabbbbb, false",
            "aaaaabbb, aaaaabbb, true",

            "To be or not to be that is a question, To be, true",
            "To be or not to be that is a question, o be that, false",
            "To be or not to be that is a question, To be or not to be that is, true",
            "To be or not to be that is a question, To be or not to be that was, false",
            "To be or not to be that is a question, question, false",
            "To be or not to be that is a question, question!, false",
            "To be or not to be that is a question, questiom, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, Gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, the following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, you can do either of the following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following, true"
    })
    void startsWithTests(String str, String pattern, boolean expected) {
        assertEquals(expected, str.startsWith(pattern));
        assertEquals(expected, startsWith(constant(str), pattern));
        assertEquals(expected, startsWith(str, constant(pattern)));
        assertEquals(expected, startsWith(constant(str), constant(pattern)));
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', 0",
            "'', A, -1",
            "'', ABC, -1",
            "A, '', 0",
            "ABC, '', 0",
            "ABC, A, 0",
            "ABC, B, 1",
            "ABC, C, 2",
            "ABC, BC, 1",
            "ABC, ABC, 0",
            "ABC, D, -1",
            "ABC, CD, -1",
            "ABC, ABCD, -1"
    })
    void indexOfTests(String str, String pattern, int expected) {
        assertEquals(expected, str.indexOf(pattern));
        assertEquals(expected, indexOf(constant(str), pattern));
        assertEquals(expected, indexOf(str, constant(pattern)));
        assertEquals(expected, indexOf(constant(str), constant(pattern)));
    }
}
