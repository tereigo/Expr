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

public interface ExprContextBuilder extends ExprContextSupplier {

    ExprContextBuilder defineLong(final String name, final LongSupplier supplier);

    ExprContextBuilder defineDouble(final String name, final DoubleSupplier supplier);

    ExprContextBuilder defineString(final String name, final StringSupplier supplier);

    ExprContextBuilder defineByteBuffer(final String name, final ByteBufferSupplier supplier);

    ExprContextBuilder defineBool(final String name, final BooleanSupplier supplier);

    ExprContextBuilder defineExprContext(final String name, final ExprContextSupplier supplier);

    ExprContextBuilder defineFunction(final String name, final Function0 func);

    ExprContextBuilder defineFunction(final String name, final Function1 func);

    ExprContextBuilder defineFunction(final String name, final Function2 func);

    ExprContextBuilder defineFunction(final String name, final Function3 func);

    ExprContextBuilder defineFunction(final String name, final Function4 func);

    ExprContextBuilder defineFunction(final String name, final Function5 func);

    ExprContextBuilder addAlias(final String name, final String alias);

    ExprContextBuilder enrich(final ExprContextEnricher... enrichers);

}
