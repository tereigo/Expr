package com.tereigo.atlas_expr.function;

import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.variant.Variant;

@FunctionalInterface
public interface Function3 {
    void call(MutableVariant result, Variant arg1, Variant arg2, Variant arg3);
}
