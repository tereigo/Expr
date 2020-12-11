package com.tereigo.atlas_expr.unused;

import com.tereigo.atlas_expr.function.StringSupplier;

public class MutableString implements StringSupplier {
    private String value;

    public void set(String value) {
        this.value = value;
    }

    @Override
    public String getAsString() {
        return value;
    }
}
