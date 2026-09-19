package com.tereigo.expr.variant;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.impl.ExprType;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.Objects;

final class VariantImpl implements MutableVariant {
    private ExprType type; // this field can be used to determine if it's empty: type != null
    // NOTICE: we re-use longVal for boolean and double values
    // true:  longVal != 0
    // false: longVal == 0
    // double is stored as long bits
    private long longVal;
    // IMPORTANT NOTE on implementation:
    // String value and ByteBuffer values are interchangeable
    // we consider Variant values equal if
    // 1. Both are Strings and objVal == other.objVal
    // 2. First is String and second is ByteBuffer and ByteBufferUtils.equals((String) objVal, (ByteBuffer) variant.objVal)
    // 3. First is ByteBuffer and second is String and ByteBufferUtils.equals((ByteBuffer) objVal, (String) variant.objVal)
    // 4. Both are ByteBuffers and ByteBufferUtils.equals((ByteBuffer) objVal, (ByteBuffer) variant.objVal)
    // So if v1.objVal == "ABC" and v2.objVal == "ABC" then they are equal
    // This is needed to be able to compare ByteBuffer values returned from the external data providers (order.ric) to String values in the expressions:
    // $ric == "VOD.L"
    // Here $ric - will be ByteBuffer taken from the msg and "VOD.L" will be String as a result of expression parsing
    //
    // IMPORTANT: ByteBuffer is not owned by Variant
    //            It's just a reference to a buffer stored and managed somewhere else
    //            It's used only for the temporary references during expression evaluation
    //            to the data provided by the external objects: transport msg, Order object, etc
    //
    // IMPORTANT:
    // we use one obj reference for the following types: String, ByteBuffer, ExprContext, Object
    // and just cast to the required type when needed
    private Object objVal;

    VariantImpl() {
    }

    VariantImpl(final double value) {
        accept(value);
    }

    VariantImpl(final long value) {
        accept(value);
    }

    VariantImpl(final boolean value) {
        accept(value);
    }

    VariantImpl(final String value) {
        accept(value);
    }

    // We don't provide the constructor from ByteBuffer to highlight the fact that ByteBuffer is not supposed to be stored in Variant
    // The only way to put it into Variant is: (as part of evaluation)
    // v = new VariantImpl();
    // v.accept(byteBuffer);
    //
//    public VariantImpl(ByteBuffer value) {
//        accept(value);
//    }

    // clone
    VariantImpl(final Variant val) {
        val.cloneTo(this);
    }

    @Override
    public void cloneTo(final Variant target) {
        final VariantImpl impl = (VariantImpl) target;
        impl.type = type;
        impl.longVal = longVal;
        impl.objVal = objVal;
    }

    @Override
    public ExprType exprType() {
        return type;
    }

    @Override
    public long getAsLong() {
        sanityCheck(ExprType.LONG);
        return longVal;
    }

    @Override
    public double getAsDouble() {
        sanityCheck(ExprType.DOUBLE);
        return Double.longBitsToDouble(longVal);
    }

    @Override
    public double getAsNumber() {
        if (type == ExprType.LONG) {
            return longVal;
        } else if (type == ExprType.DOUBLE) {
            return Double.longBitsToDouble(longVal);
        }
        throw new RuntimeException("Variant type mismatch: " + type + ", expected: LONG or DOUBLE");
    }

    @Override
    public boolean getAsBoolean() {
        sanityCheck(ExprType.BOOL);
        return longVal != 0;
    }

    @Override
    public String getAsString() {
        sanityCheck(ExprType.STRING);
        return (String) objVal;
    }

    @Override
    public ByteBuffer getAsByteBuffer() {
        sanityCheck(ExprType.BYTE_BUFFER);
        return (ByteBuffer) objVal;
    }

    @Override
    public ExprContext getAsExprContext() {
        sanityCheck(ExprType.EXPR_CONTEXT);
        return (ExprContext) objVal;
    }

    @GeneratesGarbage
    @Override
    public Object getAsObject() {
        if (type != null) {
            switch (type) {
                case DOUBLE:
                    return Double.longBitsToDouble(longVal);
                case LONG:
                    return longVal;
                case BOOL:
                    return longVal != 0;
                case STRING:
                case BYTE_BUFFER:
                case EXPR_CONTEXT:
                case OBJECT:
                    return objVal;
            }
        }
        throw new RuntimeException("Unknown Variant type: " + type);
    }

    @Override
    public void accept(final long value) {
        this.longVal = value;
        this.type = ExprType.LONG;
    }

    @Override
    public void accept(final double value) {
        this.longVal = Double.doubleToLongBits(value);
        this.type = ExprType.DOUBLE;
    }

    @Override
    public void accept(final boolean value) {
        this.longVal = value ? 1 : 0;
        this.type = ExprType.BOOL;
    }

    @Override
    public void accept(final String value) {
        this.objVal = value;
        this.type = ExprType.STRING;
    }

    @Override
    public void accept(final ByteBuffer value) {
        this.objVal = value;
        this.type = ExprType.BYTE_BUFFER;
    }

    @Override
    public void accept(final ExprContext value) {
        this.objVal = value;
        this.type = ExprType.EXPR_CONTEXT;
    }

    // it's not supposed to be used
    // and it protects from defining a user-function for some unknown Object type into VariantImpl by mistake
//    @Override
//    public void accept(Object value) {
//        this.objVal = value;
//        this.type = ExprType.OBJECT;
//    }

    private void sanityCheck(final ExprType expected) {
        if (type != expected) {
            throw new RuntimeException("Variant type mismatch: " + type + ", expected: " + expected);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final VariantImpl variant = (VariantImpl) o;
        // TODO: can we just call VariantUtils.isEqual(this, variant)

        // see "IMPORTANT NOTE" above
        if (type == ExprType.STRING && variant.type == ExprType.BYTE_BUFFER) {
            return ByteBufferUtils.equals((ByteBuffer) variant.objVal, (String) objVal);
        } else if (type == ExprType.BYTE_BUFFER && variant.type == ExprType.STRING) {
            return ByteBufferUtils.equals((ByteBuffer) objVal, (String) variant.objVal);
        }
        return type == variant.type &&
                longVal == variant.longVal &&
                Objects.equals(objVal, variant.objVal);
    }

    @Override
    public int hashCode() {
        // NOTE: STRING and BYTE_BUFFER variants holding equal content are `.equals()` (see equals() above),
        // so they must hash identically no matter which of the two concrete types backs them.
        // That's why the content hash below is computed the same way for both types (and why `type`
        // itself is deliberately left out of the hash - it isn't needed for the contract, and mixing
        // it in would make STRING/BYTE_BUFFER equal variants hash differently).
        int result = 31 + Long.hashCode(longVal);
        result = 31 * result + valueHashCode();
        return result;
    }

    private int valueHashCode() {
        if (objVal == null) {
            return 0;
        }
        if (type == ExprType.STRING) {
            return ByteBufferUtils.hashCode((String) objVal);
        }
        if (type == ExprType.BYTE_BUFFER) {
            return ByteBufferUtils.hashCode((ByteBuffer) objVal);
        }
        return objVal.hashCode();
    }
}
