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
    // Mixed long/double comparisons must be exact, never lose precision by converting the long
    // side to double (magnitudes beyond 2^53 can't be represented exactly as a double).
    // ---------------------------------------------------------------------

    @Test
    void isEqual_longDouble_beyondDoublePrecision_isNotFalselyEqual() {
        // 9007199254740993L (2^53 + 1) is odd and has no exact double representation: naively
        // converting it to double rounds (ties-to-even) down to 9007199254740992.0 (2^53) - they
        // must NOT compare equal despite that rounding collision
        final Variant oddBeyondPrecision = VariantFactory.createImmutableLong(9007199254740993L);
        final Variant roundedDouble = VariantFactory.createImmutableDouble(9007199254740992.0);

        assertFalse(VariantUtils.isEqual(oddBeyondPrecision, roundedDouble));
        assertFalse(VariantUtils.isEqual(roundedDouble, oddBeyondPrecision));

        // sanity check: a long that genuinely IS exactly representable as a double at this magnitude
        // (even, so it falls exactly on the double grid, whose spacing is 2 here) still compares
        // equal/unequal correctly
        final Variant evenBeyondPrecision = VariantFactory.createImmutableLong(9007199254740994L);
        assertTrue(VariantUtils.isEqual(evenBeyondPrecision, VariantFactory.createImmutableDouble(9007199254740994.0)));
        assertFalse(VariantUtils.isEqual(evenBeyondPrecision, roundedDouble));
    }

    @Test
    void isGreaterLess_longDouble_beyondDoublePrecision_isExact() {
        final Variant justAboveDoublePrecision = VariantFactory.createImmutableLong(9007199254740993L);
        final Variant roundedDouble = VariantFactory.createImmutableDouble(9007199254740992.0);

        // 9007199254740993 > 9007199254740992.0 - naive double conversion would make them equal
        // and get both of these wrong (neither strictly greater nor less)
        assertTrue(VariantUtils.isGreaterNumbers(justAboveDoublePrecision, roundedDouble));
        assertFalse(VariantUtils.isGreaterNumbers(roundedDouble, justAboveDoublePrecision));
        assertTrue(VariantUtils.isLessNumbers(roundedDouble, justAboveDoublePrecision));
        assertFalse(VariantUtils.isLessNumbers(justAboveDoublePrecision, roundedDouble));
        assertFalse(VariantUtils.isGreaterOrEqualNumbers(roundedDouble, justAboveDoublePrecision));
        assertFalse(VariantUtils.isLessOrEqualNumbers(justAboveDoublePrecision, roundedDouble));
    }

    @Test
    void longDouble_comparisons_withFractionalDouble() {
        final Variant five = VariantFactory.createImmutableLong(5);
        final Variant negFive = VariantFactory.createImmutableLong(-5);

        assertFalse(VariantUtils.isEqual(five, VariantFactory.createImmutableDouble(5.5)));
        assertTrue(VariantUtils.isLessNumbers(five, VariantFactory.createImmutableDouble(5.5)));
        assertTrue(VariantUtils.isGreaterNumbers(VariantFactory.createImmutableDouble(5.5), five));

        assertFalse(VariantUtils.isEqual(five, VariantFactory.createImmutableDouble(4.5)));
        assertTrue(VariantUtils.isGreaterNumbers(five, VariantFactory.createImmutableDouble(4.5)));
        assertTrue(VariantUtils.isLessNumbers(VariantFactory.createImmutableDouble(4.5), five));

        // negative side: l == floor(d) but d's fractional remainder still makes d the larger value
        assertFalse(VariantUtils.isEqual(negFive, VariantFactory.createImmutableDouble(-4.5)));
        assertTrue(VariantUtils.isLessNumbers(negFive, VariantFactory.createImmutableDouble(-4.5)));
        assertFalse(VariantUtils.isEqual(negFive, VariantFactory.createImmutableDouble(-5.5)));
        assertTrue(VariantUtils.isGreaterNumbers(negFive, VariantFactory.createImmutableDouble(-5.5)));
    }

    @Test
    void longDouble_comparisons_atLongRangeBoundaries() {
        final Variant longMin = VariantFactory.createImmutableLong(Long.MIN_VALUE);
        final Variant longMax = VariantFactory.createImmutableLong(Long.MAX_VALUE);

        // Long.MIN_VALUE is an exact power of two, so it round-trips through double exactly
        assertTrue(VariantUtils.isEqual(longMin, VariantFactory.createImmutableDouble(-9223372036854775808.0)));

        // Long.MAX_VALUE has no exact double representation: the literal rounds UP to 2^63,
        // which is one greater than any long, so it must compare greater than every long - never equal
        final Variant twoToThe63 = VariantFactory.createImmutableDouble(9223372036854775808.0);
        assertFalse(VariantUtils.isEqual(longMax, twoToThe63));
        assertFalse(VariantUtils.isEqual(longMin, twoToThe63));
        assertTrue(VariantUtils.isLessNumbers(longMax, twoToThe63));
        assertTrue(VariantUtils.isLessNumbers(longMin, twoToThe63));

        // symmetrically, nothing below Long.MIN_VALUE can equal or be less than any long.
        // Computed via Math.nextDown rather than a decimal literal, since at this magnitude (ulp=2048)
        // a hand-written literal like "-9223372036854775809.0" actually rounds back UP to
        // -9223372036854775808.0 (the nearest representable double, only 1 away) - not below it.
        final double justBelowLongMinValueAsDouble = Math.nextDown((double) Long.MIN_VALUE);
        final Variant belowLongMin = VariantFactory.createImmutableDouble(justBelowLongMinValueAsDouble);
        assertFalse(VariantUtils.isEqual(longMin, belowLongMin));
        assertTrue(VariantUtils.isGreaterNumbers(longMin, belowLongMin));
    }

    @Test
    void longDouble_comparisons_withNaNAndInfinity_areAlwaysFalse() {
        final Variant five = VariantFactory.createImmutableLong(5);
        for (final Variant unordered : List.of(
                VariantFactory.createImmutableDouble(Double.NaN),
                VariantFactory.createImmutableDouble(Double.POSITIVE_INFINITY),
                VariantFactory.createImmutableDouble(Double.NEGATIVE_INFINITY))) {
            if (Double.isNaN(unordered.getAsDouble())) {
                assertFalse(VariantUtils.isEqual(five, unordered));
                assertFalse(VariantUtils.isGreaterNumbers(five, unordered));
                assertFalse(VariantUtils.isGreaterOrEqualNumbers(five, unordered));
                assertFalse(VariantUtils.isLessNumbers(five, unordered));
                assertFalse(VariantUtils.isLessOrEqualNumbers(five, unordered));
            }
        }

        // +-Infinity are ordered (just not equal to any finite long)
        final Variant posInf = VariantFactory.createImmutableDouble(Double.POSITIVE_INFINITY);
        final Variant negInf = VariantFactory.createImmutableDouble(Double.NEGATIVE_INFINITY);
        assertFalse(VariantUtils.isEqual(five, posInf));
        assertTrue(VariantUtils.isLessNumbers(five, posInf));
        assertFalse(VariantUtils.isEqual(five, negInf));
        assertTrue(VariantUtils.isGreaterNumbers(five, negInf));
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
