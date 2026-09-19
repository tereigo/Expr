package com.tereigo.expr.variant;

import com.tereigo.expr.ExprContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.variant.VariantImplTest.createImmutableByteBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Exhaustively exercises VariantUtils across every (ExprType + empty) permutation: 8 possible
// operand "kinds" (EMPTY plus the 7 ExprType values), so every binary function here is tested
// across all 8x8 = 64 kind combinations, and every unary function across all 8 kinds.
class VariantUtilsTest {

    private record Sample(String label, ExprType kind, Variant variant) {
        @Override
        public String toString() {
            return label;
        }
    }

    private static Sample empty() {
        return new Sample("EMPTY", null, VariantFactory.createEmpty());
    }

    private static Sample longSample(final long value) {
        return new Sample("LONG(" + value + ")", ExprType.LONG, VariantFactory.createImmutableLong(value));
    }

    private static Sample doubleSample(final double value) {
        return new Sample("DOUBLE(" + value + ")", ExprType.DOUBLE, VariantFactory.createImmutableDouble(value));
    }

    private static Sample boolSample(final boolean value) {
        return new Sample("BOOL(" + value + ")", ExprType.BOOL, VariantFactory.createImmutableBoolean(value));
    }

    private static Sample stringSample(final String value) {
        return new Sample("STRING(" + value + ")", ExprType.STRING, VariantFactory.createImmutableString(value));
    }

    private static Sample byteBufferSample(final String value) {
        return new Sample("BYTE_BUFFER(" + value + ")", ExprType.BYTE_BUFFER, createImmutableByteBuffer(constant(value)));
    }

    private static Sample exprContextSample() {
        final MutableVariant v = VariantFactory.createEmpty();
        v.accept(mock(ExprContext.class));
        return new Sample("EXPR_CONTEXT", ExprType.EXPR_CONTEXT, v);
    }

    // OBJECT variants can't be produced through the real implementation - VariantImpl.accept(Object)
    // is intentionally disabled - so this is the only way to exercise the OBJECT branch of every
    // VariantUtils permutation.
    private static Sample objectSample() {
        final Variant v = mock(Variant.class);
        when(v.exprType()).thenReturn(ExprType.OBJECT);
        return new Sample("OBJECT", ExprType.OBJECT, v);
    }

    // One representative sample per kind, all carrying "the same value" (6 / 6.0 / "6" / <<6>>/true)
    // so that pairing any two compatible kinds produces a well-defined, equal/ordered result.
    private static List<Sample> allKinds() {
        return List.of(
                empty(),
                longSample(6),
                doubleSample(6.0),
                boolSample(true),
                stringSample("6"),
                byteBufferSample("6"),
                exprContextSample(),
                objectSample()
        );
    }

    private static Stream<Arguments> allPairs() {
        final List<Sample> kinds = allKinds();
        final List<Arguments> result = new ArrayList<>();
        for (final Sample left : kinds) {
            for (final Sample right : kinds) {
                result.add(Arguments.of(left, right));
            }
        }
        return result.stream();
    }

    private static boolean isNumberKind(final ExprType kind) {
        return kind == ExprType.LONG || kind == ExprType.DOUBLE;
    }

    private static boolean isStringOrByteBufferKind(final ExprType kind) {
        return kind == ExprType.STRING || kind == ExprType.BYTE_BUFFER;
    }

    // ---------------------------------------------------------------------
    // Type-predicate truth table: for every kind, exactly the matching predicate is true.
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "{0}")
    @MethodSource("allKinds")
    void typePredicates_matchExactlyOneKind(final Sample sample) {
        final Variant v = sample.variant();
        final ExprType kind = sample.kind();

        assertEquals(kind == null, VariantUtils.isEmpty(v), "isEmpty");
        assertEquals(kind == ExprType.LONG, VariantUtils.isLong(v), "isLong");
        assertEquals(kind == ExprType.DOUBLE, VariantUtils.isDouble(v), "isDouble");
        assertEquals(isNumberKind(kind), VariantUtils.isNumber(v), "isNumber");
        assertEquals(kind == ExprType.BOOL, VariantUtils.isBoolean(v), "isBoolean");
        assertEquals(kind == ExprType.STRING, VariantUtils.isString(v), "isString");
        assertEquals(kind == ExprType.BYTE_BUFFER, VariantUtils.isByteBuffer(v), "isByteBuffer");
        assertEquals(isStringOrByteBufferKind(kind), VariantUtils.isStringOrByteBuffer(v), "isStringOrByteBuffer");
        assertEquals(kind == ExprType.EXPR_CONTEXT, VariantUtils.isExprContext(v), "isExprContext");
        assertEquals(kind == ExprType.OBJECT, VariantUtils.isObject(v), "isObject");
    }

    // ---------------------------------------------------------------------
    // isEqual: full 8x8 type-compatibility matrix.
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "isEqual({0}, {1})")
    @MethodSource("allPairs")
    void isEqual_typeCompatibilityMatrix(final Sample left, final Sample right) {
        final boolean leftEmpty = left.kind() == null;
        final boolean rightEmpty = right.kind() == null;

        if (leftEmpty && rightEmpty) {
            assertTrue(VariantUtils.isEqual(left.variant(), right.variant()));
        } else if (leftEmpty != rightEmpty) {
            assertFalse(VariantUtils.isEqual(left.variant(), right.variant()));
        } else if (isNumberKind(left.kind()) && isNumberKind(right.kind())
                || isStringOrByteBufferKind(left.kind()) && isStringOrByteBufferKind(right.kind())
                || left.kind() == ExprType.BOOL && right.kind() == ExprType.BOOL) {
            // all samples represent the same underlying value ("6"), so compatible-type pairs are equal
            assertTrue(VariantUtils.isEqual(left.variant(), right.variant()));
        } else {
            assertThrows(RuntimeException.class, () -> VariantUtils.isEqual(left.variant(), right.variant()));
        }
    }

    @Test
    void isEqual_sameTypeDifferentValue_isFalse() {
        assertFalse(VariantUtils.isEqual(VariantFactory.createImmutableLong(3), VariantFactory.createImmutableLong(5)));
        assertFalse(VariantUtils.isEqual(VariantFactory.createImmutableDouble(3.0), VariantFactory.createImmutableDouble(5.0)));
        assertFalse(VariantUtils.isEqual(VariantFactory.createImmutableString("A"), VariantFactory.createImmutableString("B")));
        assertFalse(VariantUtils.isEqual(VariantFactory.createImmutableBoolean(true), VariantFactory.createImmutableBoolean(false)));
        assertFalse(VariantUtils.isEqual(createImmutableByteBuffer(constant("A")), createImmutableByteBuffer(constant("B"))));
    }

    @Test
    void isEqual_bothNullStrings_isTrue() {
        assertTrue(VariantUtils.isEqual(VariantFactory.createString(null), VariantFactory.createString(null)));
    }

    @Test
    void isEqual_oneNullString_isFalse() {
        assertFalse(VariantUtils.isEqual(VariantFactory.createString(null), VariantFactory.createImmutableString("A")));
        assertFalse(VariantUtils.isEqual(VariantFactory.createImmutableString("A"), VariantFactory.createString(null)));
    }

    // ---------------------------------------------------------------------
    // Numeric comparisons: full 8x8 type-compatibility matrix (non-number pairs must throw).
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "compare({0}, {1})")
    @MethodSource("allPairs")
    void numericComparisons_typeCompatibilityMatrix(final Sample left, final Sample right) {
        if (isNumberKind(left.kind()) && isNumberKind(right.kind())) {
            // both samples carry the equal value "6", so the relational outcome is well-defined
            assertFalse(VariantUtils.isGreaterNumbers(left.variant(), right.variant()));
            assertTrue(VariantUtils.isGreaterOrEqualNumbers(left.variant(), right.variant()));
            assertFalse(VariantUtils.isLessNumbers(left.variant(), right.variant()));
            assertTrue(VariantUtils.isLessOrEqualNumbers(left.variant(), right.variant()));
        } else {
            assertThrows(RuntimeException.class, () -> VariantUtils.isGreaterNumbers(left.variant(), right.variant()));
            assertThrows(RuntimeException.class, () -> VariantUtils.isGreaterOrEqualNumbers(left.variant(), right.variant()));
            assertThrows(RuntimeException.class, () -> VariantUtils.isLessNumbers(left.variant(), right.variant()));
            assertThrows(RuntimeException.class, () -> VariantUtils.isLessOrEqualNumbers(left.variant(), right.variant()));
        }
    }

    @Test
    void numericComparisons_actualOrdering_acrossLongAndDouble() {
        final Variant three = VariantFactory.createImmutableLong(3);
        final Variant five = VariantFactory.createImmutableLong(5);
        final Variant threeD = VariantFactory.createImmutableDouble(3.0);
        final Variant fiveD = VariantFactory.createImmutableDouble(5.0);

        for (final Variant lo : List.of(three, threeD)) {
            for (final Variant hi : List.of(five, fiveD)) {
                assertTrue(VariantUtils.isGreaterNumbers(hi, lo));
                assertFalse(VariantUtils.isGreaterNumbers(lo, hi));
                assertTrue(VariantUtils.isGreaterOrEqualNumbers(hi, lo));
                assertFalse(VariantUtils.isGreaterOrEqualNumbers(lo, hi));
                assertTrue(VariantUtils.isLessNumbers(lo, hi));
                assertFalse(VariantUtils.isLessNumbers(hi, lo));
                assertTrue(VariantUtils.isLessOrEqualNumbers(lo, hi));
                assertFalse(VariantUtils.isLessOrEqualNumbers(hi, lo));
            }
        }
    }

    // ---------------------------------------------------------------------
    // min / max
    // ---------------------------------------------------------------------

    @Test
    void minMax_pickCorrectOperand() {
        final Variant three = VariantFactory.createImmutableLong(3);
        final Variant five = VariantFactory.createImmutableLong(5);

        assertEquals(three, VariantUtils.min(three, five));
        assertEquals(three, VariantUtils.min(five, three));
        assertEquals(five, VariantUtils.max(three, five));
        assertEquals(five, VariantUtils.max(five, three));
    }

    @Test
    void minMax_nonNumberOperand_throws() {
        final Variant num = VariantFactory.createImmutableLong(3);
        final Variant str = VariantFactory.createImmutableString("A");

        assertThrows(RuntimeException.class, () -> VariantUtils.min(num, str));
        assertThrows(RuntimeException.class, () -> VariantUtils.max(num, str));
    }

    // ---------------------------------------------------------------------
    // Arithmetic (add/subtract/multiply/divide): full 8x8 type-compatibility matrix.
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "arithmetic({0}, {1})")
    @MethodSource("allPairs")
    void arithmeticOps_typeCompatibilityMatrix(final Sample left, final Sample right) {
        final MutableVariant result = VariantFactory.createEmpty();
        if (isNumberKind(left.kind()) && isNumberKind(right.kind())) {
            // both samples carry the value "6": 6+6=12, 6-6=0, 6*6=36, 6/6=1 - exact under either
            // long or double arithmetic, so a single set of expectations covers every number pairing
            VariantUtils.addNumbers(result, left.variant(), right.variant());
            assertEquals(12.0, result.getAsNumber(), 1e-9);
            VariantUtils.subtractNumbers(result, left.variant(), right.variant());
            assertEquals(0.0, result.getAsNumber(), 1e-9);
            VariantUtils.multiplyNumbers(result, left.variant(), right.variant());
            assertEquals(36.0, result.getAsNumber(), 1e-9);
            VariantUtils.divideNumbers(result, left.variant(), right.variant());
            assertEquals(1.0, result.getAsNumber(), 1e-9);
        } else {
            assertThrows(RuntimeException.class, () -> VariantUtils.addNumbers(result, left.variant(), right.variant()));
            assertThrows(RuntimeException.class, () -> VariantUtils.subtractNumbers(result, left.variant(), right.variant()));
            assertThrows(RuntimeException.class, () -> VariantUtils.multiplyNumbers(result, left.variant(), right.variant()));
            assertThrows(RuntimeException.class, () -> VariantUtils.divideNumbers(result, left.variant(), right.variant()));
        }
    }

    @Test
    void addSubtractMultiply_longOverflow_throwsArithmeticException() {
        final MutableVariant result = VariantFactory.createEmpty();
        final Variant max = VariantFactory.createImmutableLong(Long.MAX_VALUE);
        final Variant min = VariantFactory.createImmutableLong(Long.MIN_VALUE);
        final Variant one = VariantFactory.createImmutableLong(1);
        final Variant two = VariantFactory.createImmutableLong(2);

        assertThrows(ArithmeticException.class, () -> VariantUtils.addNumbers(result, max, one));
        assertThrows(ArithmeticException.class, () -> VariantUtils.subtractNumbers(result, min, one));
        assertThrows(ArithmeticException.class, () -> VariantUtils.multiplyNumbers(result, max, two));
    }

    @Test
    void addMultiply_doubleOverflow_producesInfinity_doesNotThrow() {
        final MutableVariant result = VariantFactory.createEmpty();
        final Variant max = VariantFactory.createImmutableDouble(Double.MAX_VALUE);
        final Variant two = VariantFactory.createImmutableDouble(2.0);

        VariantUtils.addNumbers(result, max, max);
        assertEquals(Double.POSITIVE_INFINITY, result.getAsNumber());

        VariantUtils.multiplyNumbers(result, max, two);
        assertEquals(Double.POSITIVE_INFINITY, result.getAsNumber());
    }

    @Test
    void divide_longByZero_throws() {
        final MutableVariant result = VariantFactory.createEmpty();
        final Variant five = VariantFactory.createImmutableLong(5);
        final Variant zero = VariantFactory.createImmutableLong(0);

        assertThrows(RuntimeException.class, () -> VariantUtils.divideNumbers(result, five, zero));
    }

    @Test
    void divide_doubleOrMixedByZero_producesInfinityOrNaN_doesNotThrow() {
        final MutableVariant result = VariantFactory.createEmpty();
        final Variant fiveLong = VariantFactory.createImmutableLong(5);
        final Variant fiveDouble = VariantFactory.createImmutableDouble(5.0);
        final Variant zeroDouble = VariantFactory.createImmutableDouble(0.0);
        final Variant zeroLong = VariantFactory.createImmutableLong(0);

        // mixed long/double division by zero does NOT go through the exact-long zero-check,
        // since that only applies when BOTH operands are long
        VariantUtils.divideNumbers(result, fiveLong, zeroDouble);
        assertEquals(Double.POSITIVE_INFINITY, result.getAsNumber());

        VariantUtils.divideNumbers(result, fiveDouble, zeroLong);
        assertEquals(Double.POSITIVE_INFINITY, result.getAsNumber());

        VariantUtils.divideNumbers(result, fiveDouble, zeroDouble);
        assertEquals(Double.POSITIVE_INFINITY, result.getAsNumber());

        VariantUtils.divideNumbers(result, zeroDouble, zeroDouble);
        assertTrue(Double.isNaN(result.getAsNumber()));
    }

    // ---------------------------------------------------------------------
    // modulusNumbers: only LONG % LONG is supported - notably NOT DOUBLE % DOUBLE,
    // unlike every other arithmetic op above.
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "modulusNumbers({0}, {1})")
    @MethodSource("allPairs")
    void modulusNumbers_onlyBothLongSucceeds(final Sample left, final Sample right) {
        final MutableVariant result = VariantFactory.createEmpty();
        if (left.kind() == ExprType.LONG && right.kind() == ExprType.LONG) {
            VariantUtils.modulusNumbers(result, left.variant(), right.variant());
            assertEquals(0L, result.getAsLong());
        } else {
            assertThrows(RuntimeException.class, () -> VariantUtils.modulusNumbers(result, left.variant(), right.variant()));
        }
    }

    @Test
    void modulusNumbers_actualValue() {
        final MutableVariant result = VariantFactory.createEmpty();
        VariantUtils.modulusNumbers(result, VariantFactory.createImmutableLong(7), VariantFactory.createImmutableLong(3));
        assertEquals(1L, result.getAsLong());
    }

    @Test
    void modulusNumbers_byZero_throws() {
        final MutableVariant result = VariantFactory.createEmpty();
        final Variant five = VariantFactory.createImmutableLong(5);
        final Variant zero = VariantFactory.createImmutableLong(0);

        assertThrows(RuntimeException.class, () -> VariantUtils.modulusNumbers(result, five, zero));
    }

    // ---------------------------------------------------------------------
    // negateNumber: full 8-kind matrix.
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "negateNumber({0})")
    @MethodSource("allKinds")
    void negateNumber_matrix(final Sample sample) {
        final MutableVariant result = VariantFactory.createEmpty();
        if (isNumberKind(sample.kind())) {
            VariantUtils.negateNumber(result, sample.variant());
            assertEquals(-6.0, result.getAsNumber(), 1e-9);
        } else {
            assertThrows(RuntimeException.class, () -> VariantUtils.negateNumber(result, sample.variant()));
        }
    }

    @Test
    void negateNumber_longOverflow_throwsArithmeticException() {
        final MutableVariant result = VariantFactory.createEmpty();
        assertThrows(ArithmeticException.class,
                () -> VariantUtils.negateNumber(result, VariantFactory.createImmutableLong(Long.MIN_VALUE)));
    }

    // ---------------------------------------------------------------------
    // epsilonEquals
    // ---------------------------------------------------------------------

    @Test
    void epsilonEquals_boundaryCases() {
        assertTrue(VariantUtils.epsilonEquals(1.0, 1.0));
        assertTrue(VariantUtils.epsilonEquals(1.0, 1.0 + 1e-9));
        assertFalse(VariantUtils.epsilonEquals(1.0, 1.00001));
        assertTrue(VariantUtils.epsilonEquals(-1.0, -1.0));
        assertTrue(VariantUtils.epsilonEquals(0.0, -0.0));
        // NaN is never "close" to anything, including itself: Math.abs(NaN - NaN) is NaN,
        // and any comparison with NaN is false
        assertFalse(VariantUtils.epsilonEquals(Double.NaN, Double.NaN));
    }
}
