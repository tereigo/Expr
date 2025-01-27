package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;

import java.util.Collection;
import java.util.List;
import java.util.function.IntSupplier;

public final class BoolMetricImpl implements SimpleBoolMetric {

    private final String name;
    private final int metricId;
    private boolean value;
    private final List<IntSupplier> keys;

    public BoolMetricImpl(final String name) {
        this.name = name;
        this.metricId = -1;
        this.value = false;
        this.keys = List.of();
    }

    public BoolMetricImpl(final String name, boolean initialValue) {
        this.name = name;
        this.metricId = -1;
        this.value = initialValue;
        this.keys = List.of();
    }

    public BoolMetricImpl(final String name, final int metricId, boolean initialValue, Collection<IntSupplier> keys) {
        this.name = name;
        this.metricId = metricId;
        this.value = initialValue;
        this.keys = List.copyOf(keys);
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
    public List<IntSupplier> getKeys() {
        return keys;
    }

    @Override
    public String toString() {
        return "BoolMetric{" + name + ", id=" + metricId + ", val=" + value + ", keys=" + keys.size() + "}";
    }
}
