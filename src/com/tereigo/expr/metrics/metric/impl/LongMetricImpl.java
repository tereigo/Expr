package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.SimpleLongMetric;

import java.util.Collection;
import java.util.List;
import java.util.function.IntSupplier;

public final class LongMetricImpl implements SimpleLongMetric {

    private final String name;
    private final int metricId;
    private long value;
    private final List<IntSupplier> keys;

    public LongMetricImpl(final String name) {
        this.name = name;
        this.metricId = -1;
        this.value = 0;
        this.keys = List.of();
    }

    public LongMetricImpl(final String name, final int metricId, Collection<IntSupplier> keys) {
        this.name = name;
        this.metricId = metricId;
        this.value = 0;
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
    public long get() {
        return value;
    }

    @Override
    public void set(final long value) {
        this.value = value;
        System.out.println("Set metric: " + name + "=" + this.value);
    }

    @Override
    public List<IntSupplier> getKeys() {
        return keys;
    }

    @Override
    public String toString() {
        return "LongMetric{" + name + ", id=" + metricId + ", val=" + value + ", kes=" + keys + "}";
    }
}
