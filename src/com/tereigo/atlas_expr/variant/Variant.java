package com.tereigo.atlas_expr.variant;

import com.tereigo.atlas_expr.ExprType;
import com.tereigo.atlas_expr.function.ByteBufferSupplier;
import com.tereigo.atlas_expr.function.ObjectSupplier;
import com.tereigo.atlas_expr.function.StringSupplier;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

// This is immutable interface
public interface Variant extends LongSupplier, DoubleSupplier, BooleanSupplier, StringSupplier, ByteBufferSupplier, ObjectSupplier {
    ExprType exprType();
}
