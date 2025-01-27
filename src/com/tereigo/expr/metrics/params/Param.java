package com.tereigo.expr.metrics.params;

public abstract class Param {
    private final String name;

    protected Param(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
