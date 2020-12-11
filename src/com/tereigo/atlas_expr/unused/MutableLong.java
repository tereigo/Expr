package com.tereigo.atlas_expr.unused;

import java.util.function.LongSupplier;

public class MutableLong implements LongSupplier {
    private long value;

    public void set(long value) {
        this.value = value;
    }

    @Override
    public long getAsLong() {
        return value;
    }
}
