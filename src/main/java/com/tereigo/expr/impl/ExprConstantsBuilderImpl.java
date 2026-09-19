package com.tereigo.expr.impl;

import com.tereigo.expr.ExprConstantsBuilder;

import java.util.HashMap;
import java.util.Map;

final class ExprConstantsBuilderImpl implements ExprConstantsBuilder {

    private final HashMap<String, Expr.Literal> constants = new HashMap<>();

    public Map<String, Expr.Literal> getConstants() {
        return constants;
    }

    @Override
    public ExprConstantsBuilder addBool(final String name, final boolean value) {
        validateName(name);
        constants.put(name, new Expr.Literal(value));
        return this;
    }

    @Override
    public ExprConstantsBuilder addLong(final String name, final long value) {
        validateName(name);
        constants.put(name, new Expr.Literal(value));
        return this;
    }

    @Override
    public ExprConstantsBuilder addDouble(final String name, final double value) {
        validateName(name);
        constants.put(name, new Expr.Literal(value));
        return this;
    }

    @Override
    public ExprConstantsBuilder addString(final String name, final String value) {
        validateName(name);
        constants.put(name, new Expr.Literal(value));
        return this;
    }

    @Override
    public Map<String, ?> build() {
        return new HashMap<>(constants);
    }

    @Override
    public String toString() {
        return "constants: " + constants.keySet();
    }

    private void validateName(final String name) {
        if (constants.containsKey(name)) {
            throw new RuntimeException("Constant '" + name + "' is already defined");
        }
    }
}
