package com.tereigo.expr.function;

@FunctionalInterface
public interface CharSequenceConsumer {

    void accept(CharSequence value);
}