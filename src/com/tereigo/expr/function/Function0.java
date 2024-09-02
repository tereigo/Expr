package com.tereigo.expr.function;

import com.tereigo.expr.variant.MutableVariant;

@FunctionalInterface
public interface Function0 {
    void call(MutableVariant result);
}
