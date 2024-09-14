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

    ExprContextBuilder addLong(final String name, final LongSupplier supplier);

    ExprContextBuilder addDouble(final String name, final DoubleSupplier supplier);

    ExprContextBuilder addString(final String name, final StringSupplier supplier);

    ExprContextBuilder addByteBuffer(final String name, final ByteBufferSupplier supplier);

    ExprContextBuilder addBool(final String name, final BooleanSupplier supplier);

    ExprContextBuilder addExprContext(final String name, final ExprContext exprContext);

    ExprContextBuilder addFunction(final String name, final Function0 func);

    ExprContextBuilder addFunction(final String name, final Function1 func);

    ExprContextBuilder addFunction(final String name, final Function2 func);

    ExprContextBuilder addFunction(final String name, final Function3 func);

    ExprContextBuilder addFunction(final String name, final Function4 func);

    ExprContextBuilder addFunction(final String name, final Function5 func);

    ExprContextBuilder addAlias(final String name, final String alias);

    ExprContextBuilder enrich(final ExprContextEnricher... enrichers);

    // Constants
    ExprContextBuilder addLong(final String name, final long value);

    ExprContextBuilder addDouble(final String name, final double value);

    ExprContextBuilder addString(final String name, final String value);
}
