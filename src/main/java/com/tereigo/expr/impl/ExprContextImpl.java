package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.function.Function0;
import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;

import java.util.HashMap;
import java.util.Map;

final class ExprContextImpl implements ExprContext {

    // Map of "name" -> Function0/1/2/3/4/5 objects
    private final Map<String, Object> functions = new HashMap<>();

    ExprContextImpl(final Map<String, Object> functions) {
        this.functions.putAll(functions);
    }

    @Override
    public Variant get(final String name, final MutableVariant result) {
        final Object funcObj = getFunction(name);
        if (funcObj == null) {
            return null;
        }
        ((Function0) funcObj).call(result);
        return result;
    }

    @Override
    public Object getFunction(final String name) {
        return functions.get(name);
    }

    @Override
    public String toString() {
        return "functions: " + functions.keySet();
    }
}
