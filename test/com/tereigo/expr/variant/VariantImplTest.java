package com.tereigo.expr.variant;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VariantImplTest {

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
        assertEquals(createByteBuffer(constant("A")), createByteBuffer(constant("A")));
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
        assertEquals(VariantFactory.createString("A"), createByteBuffer(constant("A")));
        assertNotEquals(VariantFactory.createString("A"), createByteBuffer(constant("B")));
        assertNotEquals(VariantFactory.createDouble(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createDouble(0), VariantFactory.createString("0.0"));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString("0"));
        assertEquals(createByteBuffer(constant("")), VariantFactory.createString(""));
        assertEquals(createByteBuffer(constant("A")), VariantFactory.createString("A"));
        assertNotEquals(createByteBuffer(constant("B")), VariantFactory.createString("A"));
    }

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
}