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

    void defineLong(final String name, final LongSupplier supplier);

    void defineDouble(final String name, final DoubleSupplier supplier);

    void defineString(final String name, final StringSupplier supplier);

    void defineByteBuffer(final String name, final ByteBufferSupplier supplier);

    void defineBool(final String name, final BooleanSupplier supplier);

    void defineExprContext(final String name, final ExprContextSupplier supplier);

    void defineFunction(final String name, final Function0 func);

    void defineFunction(final String name, final Function1 func);

    void defineFunction(final String name, final Function2 func);

    void defineFunction(final String name, final Function3 func);

    void defineFunction(final String name, final Function4 func);

    void defineFunction(final String name, final Function5 func);

    void addAlias(final String name, final String alias);

    void enrich(final ExprContextEnricher... enrichers);
}
