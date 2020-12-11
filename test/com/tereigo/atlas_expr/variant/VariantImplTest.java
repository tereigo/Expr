package com.tereigo.atlas_expr.variant;

import org.junit.jupiter.api.Test;

import static com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils.constant;
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
        assertEquals(VariantFactory.createByteBuffer(constant("")), VariantFactory.createByteBuffer(constant("")));
        assertEquals(VariantFactory.createByteBuffer(constant("A")), VariantFactory.createByteBuffer(constant("A")));
        assertNotEquals(VariantFactory.createByteBuffer(constant("A")), VariantFactory.createByteBuffer(constant("B")));
        // cross type comparisons
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createEmpty(), VariantFactory.createByteBuffer(constant("A")));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createByteBuffer(constant("")), VariantFactory.createEmpty());
        assertNotEquals(VariantFactory.createByteBuffer(constant("A")), VariantFactory.createEmpty());

        assertEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createByteBuffer(constant("")), VariantFactory.createBoolean(false));

        assertEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createBoolean(false));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createBoolean(true), VariantFactory.createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createBoolean(false), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createBoolean(true));
        assertNotEquals(VariantFactory.createByteBuffer(constant("")), VariantFactory.createBoolean(true));

        assertEquals(VariantFactory.createLong(0), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createLong(1));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createLong(1), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createByteBuffer(constant("")), VariantFactory.createLong(0));

        assertEquals(VariantFactory.createDouble(0.0), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createDouble(1.0));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createDouble(0.0), VariantFactory.createByteBuffer(constant("")));
        assertNotEquals(VariantFactory.createDouble(1.0), VariantFactory.createDouble(0.0));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createDouble(0));
        assertNotEquals(VariantFactory.createByteBuffer(constant("")), VariantFactory.createDouble(0));

        assertEquals(VariantFactory.createString(""), VariantFactory.createString(""));
        assertEquals(VariantFactory.createString("A"), VariantFactory.createString("A"));
        assertEquals(VariantFactory.createString("ABC"), VariantFactory.createString("ABC"));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createString("ABC"), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createDouble(0));
        assertNotEquals(VariantFactory.createString(""), VariantFactory.createLong(0));
        assertNotEquals(VariantFactory.createString("0.0"), VariantFactory.createDouble(0));
        assertNotEquals(VariantFactory.createString("0"), VariantFactory.createLong(0));
        assertEquals(VariantFactory.createString(""), VariantFactory.createByteBuffer(constant("")));
        assertEquals(VariantFactory.createString("A"), VariantFactory.createByteBuffer(constant("A")));
        assertNotEquals(VariantFactory.createString("A"), VariantFactory.createByteBuffer(constant("B")));
        assertNotEquals(VariantFactory.createDouble(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString(""));
        assertNotEquals(VariantFactory.createDouble(0), VariantFactory.createString("0.0"));
        assertNotEquals(VariantFactory.createLong(0), VariantFactory.createString("0"));
        assertEquals(VariantFactory.createByteBuffer(constant("")), VariantFactory.createString(""));
        assertEquals(VariantFactory.createByteBuffer(constant("A")), VariantFactory.createString("A"));
        assertNotEquals(VariantFactory.createByteBuffer(constant("B")), VariantFactory.createString("A"));
    }

}