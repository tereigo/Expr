package com.tereigo.expr.function;

@FunctionalInterface
public interface StringConsumer {

    void accept(String value);
}