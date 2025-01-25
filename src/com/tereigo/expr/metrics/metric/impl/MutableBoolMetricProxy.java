package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.MutableBoolMetric;

public abstract class MutableBoolMetricProxy implements MutableBoolMetric {

    // NOTICE: delegate can be reset!
    protected MutableBoolMetric delegate;

    public MutableBoolMetricProxy(final MutableBoolMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void set(final boolean value) {
        delegate.set(value);
    }

    @Override
    public boolean get() {
        return delegate.get();
    }

    @Override
    public int getMetricId() {
        return delegate.getMetricId();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
