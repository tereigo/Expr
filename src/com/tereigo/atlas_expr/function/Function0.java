package com.tereigo.atlas_expr.function;

import com.tereigo.atlas_expr.variant.MutableVariant;

@FunctionalInterface
public interface Function0 {
    void call(MutableVariant result);
}
