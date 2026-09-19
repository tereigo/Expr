package com.tereigo.expr.variant;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextFactory;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        assertEquals(VariantFactory.createEmpty(), VariantFactory.createEmpty());
        assertEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(true));
        assertEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(true));
        assertEquals(VariantFactory.createLong(0), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createLong(1));
        assertNotEquals(VariantFactory.createLong(1), VariantFactory.createLong(0));
        assertEquals(VariantFactory.createDouble(0.0), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createDouble(1.0));
        assertNotEquals(VariantFactory.createDouble(1.0), VariantFactory.createDouble(0.0));
        assertEquals(VariantFactory.createString(""), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createString("A"), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createString("A"), VariantFactory.createString("B"));
        assertEquals(createByteBuffer(constant("")), createByteBuffer(constant("")));
        assertEquals(createByteBuffer(constant("")).hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(createByteBuffer(constant("A")), createByteBuffer(constant("A")));
        assertEquals(createByteBuffer(constant("A")).hashCode(), createByteBuffer(constant("A")).hashCode());
        assertNotEquals(createByteBuffer(constant("A")), createByteBuffer(constant("B")));
        // cross type comparisons
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createEmpty(), createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createEmpty(), createByteBuffer(constant("A")));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createEmpty());
        assertNotEquals(createByteBuffer(constant("")), VariantFactory.createEmpty());
        assertNotEquals(createByteBuffer(constant("A")), VariantFactory.createEmpty());

        assertEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createBoolean(false), createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createBoolean(false));
        assertNotEquals(createByteBuffer(constant("")), VariantFactory.createBoolean(false));

        assertEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createBoolean(true), createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createBoolean(true));
        assertNotEquals(createByteBuffer(constant("")), VariantFactory.createBoolean(true));

        assertEquals(VariantFactory.createLong(0), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createLong(1));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createLong(0), createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createLong(1), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createLong(0));
        assertNotEquals(createByteBuffer(constant("")), VariantFactory.createLong(0));

        assertEquals(VariantFactory.createDouble(0.0), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createDouble(1.0));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createDouble(0.0), createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createDouble(1.0), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createDouble(0));
        assertNotEquals(createByteBuffer(constant("")), VariantFactory.createDouble(0));

        assertEquals(VariantFactory.createString(""), VariantFactory.createString(""));
        assertEquals(VariantFactory.createString("A"), VariantFactory.createString("A"));
        assertEquals(VariantFactory.createString("ABC"), VariantFactory.createString("ABC"));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createString("ABC"), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createDouble(0));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createString("0.0"), VariantFactory.createDouble(0));
        assertNotEquals(VariantFactory.createString("0"), VariantFactory.createLong(0));
        assertEquals(VariantFactory.createString(""), createByteBuffer(constant("")));
        assertEquals(VariantFactory.createString("").hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(VariantFactory.createString("A"), createByteBuffer(constant("A")));
        assertEquals(VariantFactory.createString("A").hashCode(), createByteBuffer(constant("A")).hashCode());
        assertNotEquals(VariantFactory.createString("A"), createByteBuffer(constant("B")));
        assertNotEquals(VariantFactory.createDouble(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createDouble(0), VariantFactory.createString("0.0"));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString("0"));
        assertEquals(createByteBuffer(constant("")), VariantFactory.createString(""));
        assertEquals(createByteBuffer(constant("")).hashCode(), VariantFactory.createString("").hashCode());
        assertEquals(createByteBuffer(constant("A")), VariantFactory.createString("A"));
        assertEquals(createByteBuffer(constant("A")).hashCode(), VariantFactory.createString("A").hashCode());
        assertNotEquals(createByteBuffer(constant("B")), VariantFactory.createString("A"));
    }

    // the hashCode/equals contract requires that objects considered equal by equals() produce
    // the same hashCode() - this is exercised explicitly here because STRING and BYTE_BUFFER
    // variants can be equal to each other despite being backed by different concrete objects
    @Test
    void testHashCode() {
        assertEquals(VariantFactory.createEmpty().hashCode(), VariantFactory.createEmpty().hashCode());
        assertEquals(VariantFactory.createBoolean(true).hashCode(), VariantFactory.createBoolean(true).hashCode());
        assertEquals(VariantFactory.createBoolean(false).hashCode(), VariantFactory.createBoolean(false).hashCode());
        assertEquals(VariantFactory.createLong(0).hashCode(), VariantFactory.createLong(0).hashCode());
        assertEquals(VariantFactory.createLong(123).hashCode(), VariantFactory.createLong(123).hashCode());
        assertEquals(VariantFactory.createDouble(0.0).hashCode(), VariantFactory.createDouble(0.0).hashCode());
        assertEquals(VariantFactory.createDouble(1.5).hashCode(), VariantFactory.createDouble(1.5).hashCode());
        assertEquals(VariantFactory.createString("").hashCode(), VariantFactory.createString("").hashCode());
        assertEquals(VariantFactory.createString("ABC").hashCode(), VariantFactory.createString("ABC").hashCode());
        assertEquals(createByteBuffer(constant("")).hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(createByteBuffer(constant("ABC")).hashCode(), createByteBuffer(constant("ABC")).hashCode());

        // the actual contract fix: a STRING and a BYTE_BUFFER variant with equal content
        // are `.equals()` to each other (see testEquals above), so they must hash the same
        assertEquals(VariantFactory.createString("").hashCode(), createByteBuffer(constant("")).hashCode());
        assertEquals(VariantFactory.createString("A").hashCode(), createByteBuffer(constant("A")).hashCode());
        assertEquals(VariantFactory.createString("ABC").hashCode(), createByteBuffer(constant("ABC")).hashCode());
        assertEquals(createByteBuffer(constant("A")).hashCode(), VariantFactory.createString("A").hashCode());
    }

    @Test
    void testEqualsSpecialCases() {
        final Variant v = VariantFactory.createLong(1);
        // reflexive equality must actually invoke equals(), not just identity comparison
        assertTrue(v.equals(v));
        // equals() must gracefully handle null and other types, not throw
        assertFalse(v.equals(null));
        assertFalse(v.equals("not a variant"));
        assertFalse(v.equals(42));
    }

    @Test
    void testCloneViaFactory() {
        final Variant original = VariantFactory.createLong(123);
        final Variant clone = VariantFactory.clone(original);
        assertEquals(original, clone);
        assertEquals(original.hashCode(), clone.hashCode());

        final Variant originalDouble = VariantFactory.createDouble(1.5);
        assertEquals(originalDouble, VariantFactory.clone(originalDouble));

        final Variant originalString = VariantFactory.createString("ABC");
        assertEquals(originalString, VariantFactory.clone(originalString));

        final Variant originalBool = VariantFactory.createBoolean(true);
        assertEquals(originalBool, VariantFactory.clone(originalBool));

        final Variant originalEmpty = VariantFactory.createEmpty();
        assertEquals(originalEmpty, VariantFactory.clone(originalEmpty));
    }

    @Test
    void testGetAsObjectOnEmptyVariantThrows() {
        final MutableVariant empty = VariantFactory.createEmpty();
        final RuntimeException ex = assertThrows(RuntimeException.class, empty::getAsObject);
        assertEquals("Unknown Variant type: null", ex.getMessage());
    }

    @Test
    void testHashCodeForExprContextVariant() {
        final ExprContext ctx1 = ExprContextFactory.globalContext().getAsExprContext();
        final ExprContext ctx2 = ExprContextFactory.globalContext().getAsExprContext();

        final MutableVariant v1 = VariantFactory.createEmpty();
        v1.accept(ctx1);
        final MutableVariant v2 = VariantFactory.createEmpty();
        v2.accept(ctx1);
        final MutableVariant v3 = VariantFactory.createEmpty();
        v3.accept(ctx2);

        assertEquals(v1.hashCode(), v2.hashCode());
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
        assertEquals(ctx1, v1.getAsObject());
    }
}