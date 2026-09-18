package com.tereigo.expr.function;

import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;

@FunctionalInterface
public interface Function5 {
    void call(MutableVariant result, Variant arg1, Variant arg2, Variant arg3, Variant arg4, Variant arg5);
}
