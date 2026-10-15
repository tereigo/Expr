package com.tereigo.expr.variant;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextFactory;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.variant.VariantFactory.createBoolean;
import static com.tereigo.expr.variant.VariantFactory.createDouble;
import static com.tereigo.expr.variant.VariantFactory.createEmpty;
import static com.tereigo.expr.variant.VariantFactory.createLong;
import static com.tereigo.expr.variant.VariantFactory.createString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VariantImplTest {

    // only for testing
    // we should not create ByteBuffers during the expression parsing
    // ByteBuffer can only be fetched from the external data holder
    private static VariantImpl createByteBuffer(final ByteBuffer value) {
        final VariantImpl result = new VariantImpl();
        result.accept(value);
        return result;
    }

    static Variant createImmutableByteBuffer(final ByteBuffer value) {
        return createByteBuffer(value);
    }

    @Test
    void testEquals() {
        assertEquals(createEmpty(), createEmpty());
        assertEquals(createBoolean(true), createBoolean(true));
        assertEquals(createBoolean(false), createBoolean(false));
        assertNotEquals(createBoolean(true), createBoolean(false));
        assertNotEquals(createBoolean(false), createBoolean(true));
        assertEquals(createLong(0), createLong(0));
        assertNotEquals(createLong(0), createLong(1));
        assertNotEquals(createLong(1), createLong(0));
        assertEquals(createDouble(0.0), createDouble(0.0));
        assertNotEquals(createDouble(0.0), createDouble(1.0));
        assertNotEquals(createDouble(1.0), createDouble(0.0));
        assertEquals(createString(""), createString(""));
        assertNotEquals(createString(""), createString("A"));
        assertNotEquals(createString("A"), createString(""));
        assertNotEquals(createString("A"), createString("B"));
        assertEquals(createByteBuffer(constant("")), createByteBuffer(constant("")));
        assertEquals(createByteBuffer(constant("")).hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(createByteBuffer(constant("A")), createByteBuffer(constant("A")));
        assertEquals(createByteBuffer(constant("A")).hashCode(), createByteBuffer(constant("A")).hashCode());
        assertNotEquals(createByteBuffer(constant("A")), createByteBuffer(constant("B")));
        // cross type comparisons
        assertNotEquals(createEmpty(), createBoolean(true));
        assertNotEquals(createEmpty(), createBoolean(false));
        assertNotEquals(createEmpty(), createLong(0));
        assertNotEquals(createEmpty(), createDouble(0.0));
        assertNotEquals(createEmpty(), createString(""));
        assertNotEquals(createEmpty(), createString("A"));
        assertNotEquals(createEmpty(), createByteBuffer(constant("")));
        assertNotEquals(createEmpty(), createByteBuffer(constant("A")));
        assertNotEquals(createBoolean(false), createEmpty());
        assertNotEquals(createBoolean(true), createEmpty());
        assertNotEquals(createLong(0), createEmpty());
        assertNotEquals(createDouble(0.0), createEmpty());
        assertNotEquals(createString(""), createEmpty());
        assertNotEquals(createByteBuffer(constant("")), createEmpty());
        assertNotEquals(createByteBuffer(constant("A")), createEmpty());

        assertEquals(createBoolean(false), createBoolean(false));
        assertNotEquals(createBoolean(false), createBoolean(true));
        assertNotEquals(createBoolean(false), createLong(0));
        assertNotEquals(createBoolean(false), createDouble(0.0));
        assertNotEquals(createBoolean(false), createString(""));
        assertNotEquals(createBoolean(false), createByteBuffer(constant("")));
        assertNotEquals(createBoolean(true), createBoolean(false));
        assertNotEquals(createLong(0), createBoolean(false));
        assertNotEquals(createDouble(0.0), createBoolean(false));
        assertNotEquals(createString(""), createBoolean(false));
        assertNotEquals(createByteBuffer(constant("")), createBoolean(false));

        assertEquals(createBoolean(true), createBoolean(true));
        assertNotEquals(createBoolean(true), createBoolean(false));
        assertNotEquals(createBoolean(true), createLong(0));
        assertNotEquals(createBoolean(true), createDouble(0.0));
        assertNotEquals(createBoolean(true), createString(""));
        assertNotEquals(createBoolean(true), createByteBuffer(constant("")));
        assertNotEquals(createBoolean(false), createBoolean(true));
        assertNotEquals(createLong(0), createBoolean(true));
        assertNotEquals(createDouble(0.0), createBoolean(true));
        assertNotEquals(createString(""), createBoolean(true));
        assertNotEquals(createByteBuffer(constant("")), createBoolean(true));

        assertEquals(createLong(0), createLong(0));
        assertNotEquals(createLong(0), createLong(1));
        assertNotEquals(createLong(0), createDouble(0.0));
        assertNotEquals(createLong(0), createString(""));
        assertNotEquals(createLong(0), createByteBuffer(constant("")));
        assertNotEquals(createLong(1), createLong(0));
        assertNotEquals(createDouble(0.0), createLong(0));
        assertNotEquals(createString(""), createLong(0));
        assertNotEquals(createByteBuffer(constant("")), createLong(0));

        assertEquals(createDouble(0.0), createDouble(0.0));
        assertNotEquals(createDouble(0.0), createDouble(1.0));
        assertNotEquals(createDouble(0.0), createString(""));
        assertNotEquals(createDouble(0.0), createByteBuffer(constant("")));
        assertNotEquals(createDouble(1.0), createDouble(0.0));
        assertNotEquals(createString(""), createDouble(0));
        assertNotEquals(createByteBuffer(constant("")), createDouble(0));

        assertEquals(createString(""), createString(""));
        assertEquals(createString("A"), createString("A"));
        assertEquals(createString("ABC"), createString("ABC"));
        assertNotEquals(createString(""), createString("A"));
        assertNotEquals(createString("ABC"), createString("A"));
        assertNotEquals(createString(""), createDouble(0));
        assertNotEquals(createString(""), createLong(0));
        assertNotEquals(createString("0.0"), createDouble(0));
        assertNotEquals(createString("0"), createLong(0));
        assertEquals(createString(""), createByteBuffer(constant("")));
        assertEquals(createString("").hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(createString("A"), createByteBuffer(constant("A")));
        assertEquals(createString("A").hashCode(), createByteBuffer(constant("A")).hashCode());
        assertNotEquals(createString("A"), createByteBuffer(constant("B")));
        assertNotEquals(createDouble(0), createString(""));
        assertNotEquals(createLong(0), createString(""));
        assertNotEquals(createDouble(0), createString("0.0"));
        assertNotEquals(createLong(0), createString("0"));
        assertEquals(createByteBuffer(constant("")), createString(""));
        assertEquals(createByteBuffer(constant("")).hashCode(), createString("").hashCode());
        assertEquals(createByteBuffer(constant("A")), createString("A"));
        assertEquals(createByteBuffer(constant("A")).hashCode(), createString("A").hashCode());
        assertNotEquals(createByteBuffer(constant("B")), createString("A"));
    }

    // the hashCode/equals contract requires that objects considered equal by equals() produce
    // the same hashCode() - this is exercised explicitly here because STRING and BYTE_BUFFER
    // variants can be equal to each other despite being backed by different concrete objects
    @Test
    void testHashCode() {
        assertEquals(createEmpty().hashCode(), createEmpty().hashCode());
        assertEquals(createBoolean(true).hashCode(), createBoolean(true).hashCode());
        assertEquals(createBoolean(false).hashCode(), createBoolean(false).hashCode());
        assertEquals(createLong(0).hashCode(), createLong(0).hashCode());
        assertEquals(createLong(123).hashCode(), createLong(123).hashCode());
        assertEquals(createDouble(0.0).hashCode(), createDouble(0.0).hashCode());
        assertEquals(createDouble(1.5).hashCode(), createDouble(1.5).hashCode());
        assertEquals(createString("").hashCode(), createString("").hashCode());
        assertEquals(createString("ABC").hashCode(), createString("ABC").hashCode());
        assertEquals(createByteBuffer(constant("")).hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(createByteBuffer(constant("ABC")).hashCode(), createByteBuffer(constant("ABC")).hashCode());

        // the actual contract fix: a STRING and a BYTE_BUFFER variant with equal content
        // are `.equals()` to each other (see testEquals above), so they must hash the same
        assertEquals(createString("").hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(createString("A").hashCode(), createByteBuffer(constant("A")).hashCode());
        assertEquals(createString("ABC").hashCode(), createByteBuffer(constant("ABC")).hashCode());
        assertEquals(createByteBuffer(constant("A")).hashCode(), createString("A").hashCode());
    }

    // regression test: accept(String/ByteBuffer/ExprContext) must reset the stale longVal left over
    // from a previous accept(long/double/boolean) call on the same reused MutableVariant instance -
    // otherwise two variants holding identical STRING/BYTE_BUFFER content can compare unequal or hash
    // differently purely because of their prior accept() history. Reuse of a single mutable instance
    // across evaluations (e.g. Expr.BaseExpr.result) is the whole point of the GC-free design, so this
    // is directly reachable in practice, not just a theoretical edge case.
    @Test
    void testReusedMutableVariantStringEqualityIsNotCorruptedByPriorNumericValue() {
        final MutableVariant v1 = createEmpty();
        v1.accept(123L);
        v1.accept("same");

        final MutableVariant v2 = createEmpty();
        v2.accept(1.5);
        v2.accept("same");

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertTrue(VariantUtils.isEqual(v1, v2));
    }

    @Test
    void testReusedMutableVariantByteBufferEqualityIsNotCorruptedByPriorNumericValue() {
        final MutableVariant v1 = createEmpty();
        v1.accept(true);
        v1.accept(constant("same"));

        final MutableVariant v2 = createEmpty();
        v2.accept(999L);
        v2.accept(constant("same"));

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    // regression tests: objVal may now be any CharSequence, not just String (e.g. a caller-supplied
    // StringBuilder), so that external functions/identifiers can hand over string content without
    // forcing a String allocation. StringBuilder does not override Object.equals()/hashCode() (identity
    // semantics), so VariantImpl.equals()/hashCode() must never fall back to Objects.equals(objVal, ...)
    // for STRING content - they must always go through ByteBufferUtils.equals/hashCode(CharSequence),
    // which compares by content regardless of the concrete backing type.
    @Test
    void testStringBuilderBackedVariantEqualsStringBackedVariant() {
        final Variant fromString = VariantFactory.createImmutableString("VOD.L");
        final Variant fromStringBuilder = createString(new StringBuilder("VOD.L"));

        assertEquals(fromString, fromStringBuilder);
        assertEquals(fromStringBuilder, fromString);
        assertEquals(fromString.hashCode(), fromStringBuilder.hashCode());
    }

    @Test
    void testTwoDifferentStringBuilderInstancesWithSameContentAreEqual() {
        // the exact case Objects.equals(objVal, ...) would get wrong: two distinct StringBuilder
        // instances holding identical text are NOT .equals() under Object's default identity semantics,
        // but they must be VariantImpl-equal since the expression language compares by content
        final Variant v1 = createString(new StringBuilder("VOD.L"));
        final Variant v2 = createString(new StringBuilder("VOD.L"));

        assertNotSame(v1, v2);
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void testStringBuilderBackedVariantEqualsByteBufferBackedVariant() {
        final Variant fromStringBuilder = createString(new StringBuilder("VOD.L"));
        final Variant fromByteBuffer = createByteBuffer(constant("VOD.L"));

        assertEquals(fromStringBuilder, fromByteBuffer);
        assertEquals(fromByteBuffer, fromStringBuilder);
        assertEquals(fromStringBuilder.hashCode(), fromByteBuffer.hashCode());
    }

    @Test
    void testMutatedAndReusedStringBuilderReflectsCurrentContentOnEachAccept() {
        // the actual GC-free reuse pattern this feature exists for: a caller-owned StringBuilder
        // mutated and re-accept()-ed into the same MutableVariant instance across repeated evaluations
        final StringBuilder buffer = new StringBuilder("VOD.L");
        final MutableVariant v = createEmpty();
        v.accept(buffer);
        assertEquals(VariantFactory.createImmutableString("VOD.L"), v);

        buffer.setLength(0);
        buffer.append("BARC.L");
        v.accept(buffer);
        assertEquals(VariantFactory.createImmutableString("BARC.L"), v);
        assertNotEquals(VariantFactory.createImmutableString("VOD.L"), v);
    }

    @Test
    void testEqualsSpecialCases() {
        final Variant v = createLong(1);
        // reflexive equality must actually invoke equals(), not just identity comparison
        assertTrue(v.equals(v));
        // equals() must gracefully handle null and other types, not throw
        assertFalse(v.equals(null));
        assertFalse(v.equals("not a variant"));
        assertFalse(v.equals(42));
    }

    @Test
    void testCloneViaFactory() {
        final Variant original = createLong(123);
        final Variant clone = VariantFactory.clone(original);
        assertEquals(original, clone);
        assertEquals(original.hashCode(), clone.hashCode());

        final Variant originalDouble = createDouble(1.5);
        assertEquals(originalDouble, VariantFactory.clone(originalDouble));

        final Variant originalString = createString("ABC");
        assertEquals(originalString, VariantFactory.clone(originalString));

        final Variant originalBool = createBoolean(true);
        assertEquals(originalBool, VariantFactory.clone(originalBool));

        final Variant originalEmpty = createEmpty();
        assertEquals(originalEmpty, VariantFactory.clone(originalEmpty));
    }

    @Test
    void testGetAsObjectOnEmptyVariantThrows() {
        final MutableVariant empty = createEmpty();
        final RuntimeException ex = assertThrows(RuntimeException.class, empty::getAsObject);
        assertEquals("Unknown Variant type: null", ex.getMessage());
    }

    @Test
    void testHashCodeForExprContextVariant() {
        final ExprContext ctx1 = ExprContextFactory.globalContext().getAsExprContext();
        final ExprContext ctx2 = ExprContextFactory.globalContext().getAsExprContext();

        final MutableVariant v1 = createEmpty();
        v1.accept(ctx1);
        final MutableVariant v2 = createEmpty();
        v2.accept(ctx1);
        final MutableVariant v3 = createEmpty();
        v3.accept(ctx2);

        assertEquals(v1.hashCode(), v2.hashCode());
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
        assertEquals(ctx1, v1.getAsObject());
    }
}