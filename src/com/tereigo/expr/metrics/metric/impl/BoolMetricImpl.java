package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;

public final class BoolMetricImpl implements SimpleBoolMetric {

    private final String name;
    private final int metricId;
    private boolean value;

    public BoolMetricImpl(final String name) {
        this.name = name;
        this.metricId = -1;
        this.value = false;
    }

    public BoolMetricImpl(final String name, final int metricId) {
        this.name = name;
        this.metricId = metricId;
        this.value = false;
    }

    @Override
    public int getMetricId() {
        return metricId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean get() {
        return value;
    }

    @Override
    public void set(final boolean value) {
        this.value = value;
        System.out.println("Set metric: " + name + "=" + this.value);
    }

    @Override
    public String toString() {
        return "BoolMetric{" + name + ", val=" + value + "}";
    }
}
