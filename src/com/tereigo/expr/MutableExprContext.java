package com.tereigo.expr;

import com.tereigo.expr.function.ByteBufferSupplier;
import com.tereigo.expr.function.ExprContextSupplier;
import com.tereigo.expr.function.Function0;
import com.tereigo.expr.function.Function1;
import com.tereigo.expr.function.Function2;
import com.tereigo.expr.function.Function3;
import com.tereigo.expr.function.Function4;
import com.tereigo.expr.function.Function5;
import com.tereigo.expr.function.StringSupplier;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

public interface MutableExprContext extends ExprContextSupplier {

    void defineLong(String name, LongSupplier supplier);

    void defineDouble(String name, DoubleSupplier supplier);

    void defineString(String name, StringSupplier supplier);

    void defineByteBuffer(String name, ByteBufferSupplier supplier);

    void defineBool(String name, BooleanSupplier supplier);

    void defineExprContext(String name, ExprContextSupplier supplier);

    void defineFunction(String name, Function0 func);

    void defineFunction(String name, Function1 func);

    void defineFunction(String name, Function2 func);

    void defineFunction(String name, Function3 func);

    void defineFunction(String name, Function4 func);

    void defineFunction(String name, Function5 func);

    void addAlias(String name, String alias);

    void enrich(ExprContextEnricher... enrichers);
}
