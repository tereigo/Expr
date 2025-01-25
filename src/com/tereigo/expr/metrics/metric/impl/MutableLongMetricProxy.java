package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.MutableLongMetric;

public abstract class MutableLongMetricProxy implements MutableLongMetric {

    // NOTICE: delegate can be reset!
    protected MutableLongMetric delegate;

    public MutableLongMetricProxy(final MutableLongMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void set(final long value) {
        delegate.set(value);
    }

    @Override
    public long get() {
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
