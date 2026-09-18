package com.tereigo.expr.function;

@FunctionalInterface
public interface ObjectConsumer {

    void accept(Object value);
}