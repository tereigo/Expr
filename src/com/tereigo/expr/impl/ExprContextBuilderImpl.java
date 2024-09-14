package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.function.ByteBufferSupplier;
import com.tereigo.expr.function.Function0;
import com.tereigo.expr.function.Function1;
import com.tereigo.expr.function.Function2;
import com.tereigo.expr.function.Function3;
import com.tereigo.expr.function.Function4;
import com.tereigo.expr.function.Function5;
import com.tereigo.expr.function.StringSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

final class ExprContextBuilderImpl implements ExprContextBuilder {

    // Map of "name" -> Function0/1/2/3/4/5 objects
    private final Map<String, Object> functions = new HashMap<>();
    private final Map<String, Expr.Literal> constants = new HashMap<>();

    ExprContextBuilderImpl(final ExprContextEnricher... enrichers) {
        for (final ExprContextEnricher enricher : enrichers) {
            enricher.enrich(this);
        }
    }

    @Override
    public ExprContextBuilder addLong(final String name, final LongSupplier supplier) {
        // we wrap all value providers into a function from 0 parameters (Function0)
        return addFunction(name, result -> result.accept(supplier.getAsLong()));
    }

    @Override
    public ExprContextBuilder addDouble(final String name, final DoubleSupplier supplier) {
        return addFunction(name, result -> result.accept(supplier.getAsDouble()));
    }

    @Override
    public ExprContextBuilder addString(final String name, final StringSupplier supplier) {
        return addFunction(name, result -> result.accept(supplier.getAsString()));
    }

    @Override
    public ExprContextBuilder addByteBuffer(final String name, final ByteBufferSupplier supplier) {
        return addFunction(name, result -> result.accept(supplier.getAsByteBuffer()));
    }

    @Override
    public ExprContextBuilder addBool(final String name, final BooleanSupplier supplier) {
        return addFunction(name, result -> result.accept(supplier.getAsBoolean()));
    }

    @Override
    public ExprContextBuilder addExprContext(final String name, final ExprContext exprContext) {
        return addFunction(name, result -> result.accept(exprContext));
    }

    @Override
    public ExprContextBuilder addFunction(final String name, final Function0 func) {
        validateName(name);
        functions.put(name, func);
        return this;
    }

    @Override
    public ExprContextBuilder addFunction(final String name, final Function1 func) {
        validateName(name);
        functions.put(name, func);
        return this;
    }

    @Override
    public ExprContextBuilder addFunction(final String name, final Function2 func) {
        validateName(name);
        functions.put(name, func);
        return this;
    }

    @Override
    public ExprContextBuilder addFunction(final String name, final Function3 func) {
        validateName(name);
        functions.put(name, func);
        return this;
    }

    @Override
    public ExprContextBuilder addFunction(final String name, final Function4 func) {
        validateName(name);
        functions.put(name, func);
        return this;
    }

    @Override
    public ExprContextBuilder addFunction(final String name, final Function5 func) {
        validateName(name);
        functions.put(name, func);
        return this;
    }

    @Override
    public ExprContextBuilder addAlias(final String name, final String alias) {
        if (name == null || alias == null) {
            throw new RuntimeException("Empty name or alias");
        }
        if (name.equals(alias)) {
            throw new RuntimeException("Identical name and alias: '" + name + "'");
        }
        final Object val = functions.get(name);
        if (val == null) {
            throw new RuntimeException("Unknown identifier '" + name + "' for alias '" + alias + "'");
        }
        validateName(alias);
        functions.put(alias, val);
        return this;
    }

    @Override
    public ExprContextBuilder enrich(final ExprContextEnricher... enrichers) {
        for (final ExprContextEnricher enricher : enrichers) {
            enricher.enrich(this);
        }
        return this;
    }

    @Override
    public ExprContextBuilder addLong(final String name, final long value) {
        validateConstantName(name);
        constants.put(name, new Expr.Literal(value));
        // add as a function as well in case we will evaluate the expression without optimization
        // in which case we should be able to retrieve this constant by name and set its value
        return addFunction(name, result -> result.accept(value));
    }

    @Override
    public ExprContextBuilder addDouble(final String name, final double value) {
        validateConstantName(name);
        constants.put(name, new Expr.Literal(value));
        return addFunction(name, result -> result.accept(value));
    }

    @Override
    public ExprContextBuilder addString(final String name, final String value) {
        validateConstantName(name);
        constants.put(name, new Expr.Literal(value));
        return addFunction(name, result -> result.accept(value));
    }

    @Override
    public ExprContext getAsExprContext() {
        // create an immutable copy of all functions
        return new ExprContextImpl(functions, constants);
    }

    @Override
    public String toString() {
        return "functions: " + functions.keySet();
    }

    private void validateName(final String name) {
        if (functions.containsKey(name)) {
            throw new RuntimeException("Function '" + name + "' is already defined");
        }
    }

    private void validateConstantName(final String name) {
        if (constants.containsKey(name)) {
            throw new RuntimeException("Constant '" + name + "' is already defined");
        }
    }
}
