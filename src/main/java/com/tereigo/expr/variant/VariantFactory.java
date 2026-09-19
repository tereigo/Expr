package com.tereigo.expr.variant;

public final class VariantFactory {
    private static final Variant BOOL_TRUE = new VariantImpl(true);
    private static final Variant BOOL_FALSE = new VariantImpl(false);

    private VariantFactory() {
    }

    public static MutableVariant createEmpty() {
        return new VariantImpl();
    }

    public static MutableVariant clone(final Variant var) {
        return new VariantImpl(var);
    }

    public static MutableVariant createDouble(final double value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableDouble(final double value) {
        return createDouble(value);
    }

    public static MutableVariant createLong(final long value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableLong(final long value) {
        return createLong(value);
    }

    public static MutableVariant createBoolean(final boolean value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableBoolean(final boolean value) {
        return value ? BOOL_TRUE : BOOL_FALSE;
    }

    public static MutableVariant createString(final String value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableString(final String value) {
        return createString(value);
    }

}
