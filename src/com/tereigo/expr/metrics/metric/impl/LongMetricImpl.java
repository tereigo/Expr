package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.SimpleLongMetric;

public final class LongMetricImpl implements SimpleLongMetric {

    private final String name;
    private final int metricId;
    private long value;

    public LongMetricImpl(final String name) {
        this.name = name;
        this.metricId = -1;
        this.value = 0;
    }

    public LongMetricImpl(final String name, final int metricId) {
        this.name = name;
        this.metricId = metricId;
        this.value = 0;
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
    public String toString() {
        return "LongMetric{" + name + ", val=" + value + "}";
    }
}
