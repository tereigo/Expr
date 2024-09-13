package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.function.ByteBufferSupplier;
import com.tereigo.expr.function.ExprContextSupplier;
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

final class ExprContextMutable implements MutableExprContext {

    // Map of "name" -> Function0/1/2/3/4/5 objects
    private final Map<String, Object> functions = new HashMap<>();

    ExprContextMutable(final ExprContextEnricher... enrichers) {
        for (final ExprContextEnricher enricher : enrichers) {
            enricher.enrich(this);
        }
    }

    @Override
    public void defineLong(final String name, final LongSupplier supplier) {
        // we wrap all value providers into a function from 0 parameters (Function0)
        defineFunction(name, result -> result.accept(supplier.getAsLong()));
    }

    @Override
    public void defineDouble(final String name, final DoubleSupplier supplier) {
        defineFunction(name, result -> result.accept(supplier.getAsDouble()));
    }

    @Override
    public void defineString(final String name, final StringSupplier supplier) {
        defineFunction(name, result -> result.accept(supplier.getAsString()));
    }

    @Override
    public void defineByteBuffer(final String name, final ByteBufferSupplier supplier) {
        defineFunction(name, result -> result.accept(supplier.getAsByteBuffer()));
    }

    @Override
    public void defineBool(final String name, final BooleanSupplier supplier) {
        defineFunction(name, result -> result.accept(supplier.getAsBoolean()));
    }

    @Override
    public void defineExprContext(final String name, final ExprContextSupplier supplier) {
        defineFunction(name, result -> result.accept(supplier.getAsExprContext()));
    }

    @Override
    public void defineFunction(final String name, final Function0 func) {
        validateName(name);
        functions.put(name, func);
    }

    @Override
    public void defineFunction(final String name, final Function1 func) {
        validateName(name);
        functions.put(name, func);
    }

    @Override
    public void defineFunction(final String name, final Function2 func) {
        validateName(name);
        functions.put(name, func);
    }

    @Override
    public void defineFunction(final String name, final Function3 func) {
        validateName(name);
        functions.put(name, func);
    }

    @Override
    public void defineFunction(final String name, final Function4 func) {
        validateName(name);
        functions.put(name, func);
    }

    @Override
    public void defineFunction(final String name, final Function5 func) {
        validateName(name);
        functions.put(name, func);
    }

    @Override
    public void addAlias(final String name, final String alias) {
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
    }

    @Override
    public void enrich(final ExprContextEnricher... enrichers) {
        for (final ExprContextEnricher enricher : enrichers) {
            enricher.enrich(this);
        }
    }

    @Override
    public ExprContext getAsExprContext() {
        // create an immutable copy of all functions
        return new ExprContextImpl(functions);
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
}
