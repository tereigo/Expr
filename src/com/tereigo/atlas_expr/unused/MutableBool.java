package com.tereigo.atlas_expr.unused;

import java.util.function.BooleanSupplier;

public class MutableBool implements BooleanSupplier {
    private boolean value;

    public void set(boolean value) {
        this.value = value;
    }

    @Override
    public boolean getAsBoolean() {
        return value;
    }
}
