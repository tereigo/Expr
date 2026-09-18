package com.tereigo.expr.function;

@FunctionalInterface
public interface BooleanConsumer {

    void accept(boolean value);
}