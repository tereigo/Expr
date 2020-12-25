package com.tereigo.atlas_expr.variant;

import com.tereigo.atlas_expr.ExprContext;
import com.tereigo.atlas_expr.ExprType;
import com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.Objects;

public class VariantImpl implements MutableVariant {
    private ExprType type; // this field can be use to determine if it's empty: type != null
    // NOTICE: we re-use longVal for boolean values
    // true:  longVal != 0
    // false: longVal == 0
    private long longVal;
    private double doubleVal;
    // IMPORTANT NOTE on implementation:
    // String value and ByteBuffer values are interchangeable
    // we consider Variant values equal if strVal == other.strVal || strVal == other.bbVal || bbVal == other.bbVal
    // So if v1.strVal == "ABC" and v2.bbVal == "ABC" then they are equal
    // This is needed to be able to compare ByteBuffer values returned from the msg (order.ric) to String values in the expressions:
    // $ric == "VOD.L"
    // Here $ric - will be ByteBuffer taken from the msg and "VOD.L" will be String as a result of expression parsing
    //
    // IMPORTANT: ByteBuffer is not owned by Variant
    //            It's just a reference to a buffer stored and managed somewhere else
    //            It's used only for the temporary references during expression evaluation
    //            to the data provided by the external objects: Atlink msg, Order, etc
    //
    // IMPORTANT:
    // we use one obj reference for the following types: String, ByteBuffer, ExprContext, Object
    // and just cast to the required type when required
    private Object objVal;

    VariantImpl() {
    }

    VariantImpl(double value) {
        accept(value);
    }

    VariantImpl(long value) {
        accept(value);
    }

    VariantImpl(boolean value) {
        accept(value);
    }

    VariantImpl(String value) {
        accept(value);
    }

    // We don't provide the constructor from ByteBuffer to highlight the fact that ByteBuffer is not supposed to be stored in Variant
    // The only way to put it into Variant is:
    // v = new VariantImpl();
    // v.accept(byteBuffer);
//    public VariantImpl(ByteBuffer value) {
//        accept(value);
//    }

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
        return doubleVal;
    }

    @Override
    public boolean getAsBoolean() {
        sanityCheck(ExprType.BOOL);
        return longVal != 0;
    }

    @Override
    public String getAsString() {
        sanityCheck(ExprType.STRING);
        return (String)objVal;
    }

    @Override
    public ByteBuffer getAsByteBuffer() {
        sanityCheck(ExprType.BYTE_BUFFER);
        return (ByteBuffer)objVal;
    }

    @Override
    public ExprContext getAsExprContext() {
        sanityCheck(ExprType.EXPR_CONTEXT);
        return (ExprContext)objVal;
    }

    @Override
    public Object getAsObject() {
        switch (type) {
            case DOUBLE:        return doubleVal;
            case LONG:          return longVal;
            case BOOL:          return longVal != 0;
            case STRING:
            case BYTE_BUFFER:
            case EXPR_CONTEXT:
            case OBJECT:        return objVal;
        }
        throw new RuntimeException("Unknown Variant type: " + type);
    }

    @Override
    public void accept(long value) {
        this.longVal = value;
        this.type = ExprType.LONG;
    }

    @Override
    public void accept(double value) {
        this.doubleVal = value;
        this.type = ExprType.DOUBLE;
    }

    @Override
    public void accept(boolean value) {
        this.longVal = value ? 1 : 0;
        this.type = ExprType.BOOL;
    }

    @Override
    public void accept(String value) {
        this.objVal = value;
        this.type = ExprType.STRING;
    }

    @Override
    public void accept(ByteBuffer value) {
        this.objVal = value;
        this.type = ExprType.BYTE_BUFFER;
    }

    @Override
    public void accept(ExprContext value) {
        this.objVal = value;
        this.type = ExprType.EXPR_CONTEXT;
    }

    @Override
    public void accept(Object value) {
        this.objVal = value;
        this.type = ExprType.OBJECT;
    }

    private void sanityCheck(ExprType expected) {
        if (type != expected) {
            throw new RuntimeException("Variant type mismatch: " + type + ", expected: " + expected);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariantImpl variant = (VariantImpl) o;
        // see "IMPORTANT NOTE" above
        if (type == ExprType.STRING && variant.type == ExprType.BYTE_BUFFER) {
            return ByteBufferUtils.equals((ByteBuffer)variant.objVal, (String)objVal);
        } else if (type == ExprType.BYTE_BUFFER && variant.type == ExprType.STRING) {
            return ByteBufferUtils.equals((ByteBuffer)objVal, (String)variant.objVal);
        }
        return type == variant.type &&
                longVal == variant.longVal &&
                Double.compare(doubleVal, variant.doubleVal) == 0 &&
                Objects.equals(objVal, variant.objVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longVal, doubleVal, objVal, type);
    }

}
