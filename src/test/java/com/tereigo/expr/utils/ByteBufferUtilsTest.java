package com.tereigo.expr.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.utils.ByteBufferUtils.contains;
import static com.tereigo.expr.utils.ByteBufferUtils.containsIgnoreCase;
import static com.tereigo.expr.utils.ByteBufferUtils.endsWith;
import static com.tereigo.expr.utils.ByteBufferUtils.endsWithIgnoreCase;
import static com.tereigo.expr.utils.ByteBufferUtils.indexOf;
import static com.tereigo.expr.utils.ByteBufferUtils.indexOfIgnoreCase;
import static com.tereigo.expr.utils.ByteBufferUtils.isEmpty;
import static com.tereigo.expr.utils.ByteBufferUtils.startsWith;
import static com.tereigo.expr.utils.ByteBufferUtils.startsWithIgnoreCase;
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
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, Gradle, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, the following, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, following, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, you can do either of the following, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, true",
    })
    void containsTests(final String str, final String pattern, final boolean expected) {
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
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, Gradle, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, gradle, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, of through Gradle, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, OF THROUGH GRADLE, true",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, If you also want to run the benchmarks from within your IDE instead of through Gradle, true"
    })
    void containsTestsIgnoreCase(final String str, final String pattern, final boolean expected) {
        assertEquals(expected, containsIgnoreCase(str, pattern));
        assertEquals(expected, containsIgnoreCase(constant(str), pattern));
        assertEquals(expected, containsIgnoreCase(str, constant(pattern)));
        assertEquals(expected, containsIgnoreCase(constant(str), constant(pattern)));
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
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, Gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, the following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, you can do either of the following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, true"
    })
    void startsWithTests(final String str, final String pattern, final boolean expected) {
        assertEquals(expected, str.startsWith(pattern));
        assertEquals(expected, startsWith(constant(str), pattern));
        assertEquals(expected, startsWith(str, constant(pattern)));
        assertEquals(expected, startsWith(constant(str), constant(pattern)));
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
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, Gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, gradle, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, the following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, you can do either of the following, false",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, If you also want to run the benchmarks from within your IDE instead of through Gradle you can do either of the following, true"
    })
    void startsWithTestsIgnoreCase(final String str, final String pattern, final boolean expected) {
        assertEquals(expected, startsWithIgnoreCase(str, pattern));
        assertEquals(expected, startsWithIgnoreCase(constant(str), pattern));
        assertEquals(expected, startsWithIgnoreCase(str, constant(pattern)));
        assertEquals(expected, startsWithIgnoreCase(constant(str), constant(pattern)));
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
    void indexOfTests(final String str, final String pattern, final int expected) {
        assertEquals(expected, str.indexOf(pattern));
        assertEquals(expected, indexOf(constant(str), pattern));
        assertEquals(expected, indexOf(str, constant(pattern)));
        assertEquals(expected, indexOf(constant(str), constant(pattern)));
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
            "ABC, ABCD, -1",

            "aaaaabbb, a, 0",
            "aaaaabbb, aa, 0",
            "aaaaabbb, aaa, 0",
            "aaaaabbb, aaaa, 0",
            "aaaaabbb, aaaaa, 0",
            "aaaaabbb, aaaaaa, -1",
            "aaaaabbb, aaaaab, 0",
            "aaaaabbb, aaaaabb, 0",
            "aaaaabbb, aabbb, 3",
            "aaaaabbb, aaaabbb, 1",
            "aaaaabbb, aaaabbbbb, -1",
            "aaaaabbb, aaaaabbb, 0",

            "To be or not to be that is a question, To be, 0",
            "To be or not to be that is a question, o be that, 14",
            "To be or not to be that is a question, To be or not to be that is, 0",
            "To be or not to be that is a question, To be or not to be that was, -1",
            "To be or not to be that is a question, question, 29",
            "To be or not to be that is a question, question!, -1",
            "To be or not to be that is a question, questiom, -1",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, Gradle, 79",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, gradle, 79",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, of through Gradle, 68",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, OF THROUGH GRADLE, 68",
            "If you also want to run the benchmarks from within your IDE instead of through Gradle, If you also want to run the benchmarks from within your IDE instead of through Gradle, 0"
    })
    void indexOfTestsIgnoreCase(final String str, final String pattern, final int expected) {
        assertEquals(expected, indexOfIgnoreCase(str, pattern));
        assertEquals(expected, indexOfIgnoreCase(constant(str), pattern));
        assertEquals(expected, indexOfIgnoreCase(str, constant(pattern)));
        assertEquals(expected, indexOfIgnoreCase(constant(str), constant(pattern)));
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', true",
            "'', A, false",
            "'', ABC, false",
            "A, '', true",
            "a, '', true",
            "ABC, '', true",
            "ABC, A, false",
            "ABC, a, false",
            "aBC, A, false",
            "aBC, a, false",
            "ABC, B, false",
            "ABC, C, true",
            "ABC, c, false",
            "ABc, c, true",
            "ABc, C, false",
            "ABC, BC, true",
            "ABC, bc, false",
            "Abc, BC, false",
            "Abc, bc, true",
            "ABC, ABC, true",
            "ABC, abc, false",
            "abc, ABC, false",
            "abc, abc, true",
            "ABC, D, false",
            "ABC, CD, false",
            "ABC, ABCD, false",
    })
    void endsWithTests(final String str, final String pattern, final boolean expected) {
        assertEquals(expected, str.endsWith(pattern));
        assertEquals(expected, endsWith(constant(str), pattern));
        assertEquals(expected, endsWith(str, constant(pattern)));
        assertEquals(expected, endsWith(constant(str), constant(pattern)));
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', true",
            "'', A, false",
            "'', ABC, false",
            "A, '', true",
            "a, '', true",
            "ABC, '', true",
            "ABC, A, false",
            "ABC, a, false",
            "aBC, A, false",
            "aBC, a, false",
            "ABC, B, false",
            "ABC, C, true",
            "ABC, c, true",
            "ABc, c, true",
            "ABc, C, true",
            "ABC, BC, true",
            "ABC, bc, true",
            "Abc, BC, true",
            "Abc, bc, true",
            "ABC, ABC, true",
            "ABC, abc, true",
            "abc, ABC, true",
            "abc, abc, true",
            "ABC, D, false",
            "ABC, CD, false",
            "ABC, ABCD, false",
    })
    void endsWithIgnoreCaseTests(final String str, final String pattern, final boolean expected) {
        assertEquals(expected, endsWithIgnoreCase(str, pattern));
        assertEquals(expected, endsWithIgnoreCase(constant(str), pattern));
        assertEquals(expected, endsWithIgnoreCase(str, constant(pattern)));
        assertEquals(expected, endsWithIgnoreCase(constant(str), constant(pattern)));
    }
}
