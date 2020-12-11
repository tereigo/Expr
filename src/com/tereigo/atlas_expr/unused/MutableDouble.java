package com.tereigo.atlas_expr.unused;

import java.util.function.DoubleSupplier;

public class MutableDouble implements DoubleSupplier {
    private double value;

    public void set(double value) {
        this.value = value;
    }

    @Override
    public double getAsDouble() {
        return value;
    }
}
