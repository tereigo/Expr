package com.tereigo.atlas_expr.variant;

import java.nio.ByteBuffer;

public final class VariantFactory {
    private static final Variant BOOL_TRUE = new VariantImpl(true);
    private static final Variant BOOL_FALSE = new VariantImpl(false);

    private VariantFactory() {}

    public static VariantImpl createEmpty() {
        return new VariantImpl();
    }

    public static VariantImpl createDouble(double value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableDouble(double value) {
        return createDouble(value);
    }

    public static VariantImpl createLong(long value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableLong(long value) {
        return createLong(value);
    }

    public static VariantImpl createBoolean(boolean value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableBoolean(boolean value) {
        return value ? BOOL_TRUE : BOOL_FALSE;
    }

    public static VariantImpl createString(String value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableString(String value) {
        return createString(value);
    }

    // only for testing
    // we should not create ByteBuffers during the expression parsing
    // ByteBuffer can only be fetched from the messages
    static VariantImpl createByteBuffer(ByteBuffer value) {
        VariantImpl result = new VariantImpl();
        result.accept(value);
        return result;
    }

    static Variant createImmutableByteBuffer(ByteBuffer value) {
        return createByteBuffer(value);
    }
}
