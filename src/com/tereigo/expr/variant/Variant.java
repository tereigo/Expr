package com.tereigo.expr.variant;

import com.tereigo.expr.function.ByteBufferSupplier;
import com.tereigo.expr.function.ExprContextSupplier;
import com.tereigo.expr.function.NumberSupplier;
import com.tereigo.expr.function.ObjectSupplier;
import com.tereigo.expr.function.StringSupplier;
import com.tereigo.expr.impl.ExprType;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

// This is an immutable interface
public interface Variant extends LongSupplier, DoubleSupplier, NumberSupplier, BooleanSupplier, StringSupplier,
        ByteBufferSupplier, ExprContextSupplier, ObjectSupplier {

    ExprType exprType();

}
