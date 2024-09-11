package com.tereigo.expr.variant;

public final class VariantFactory {
    private static final Variant BOOL_TRUE = new VariantImpl(true);
    private static final Variant BOOL_FALSE = new VariantImpl(false);

    private VariantFactory() {}

    public static VariantImpl createEmpty() {
        return new VariantImpl();
    }

    public static VariantImpl createDouble(final double value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableDouble(final double value) {
        return createDouble(value);
    }

    public static VariantImpl createLong(final long value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableLong(final long value) {
        return createLong(value);
    }

    public static VariantImpl createBoolean(final boolean value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableBoolean(final boolean value) {
        return value ? BOOL_TRUE : BOOL_FALSE;
    }

    public static VariantImpl createString(final String value) {
        return new VariantImpl(value);
    }

    public static Variant createImmutableString(final String value) {
        return createString(value);
    }

}
