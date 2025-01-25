package com.tereigo.expr.metrics.not_used;

import com.tereigo.expr.metrics.metric.Metric;

public abstract class AbstractMetricProxy<T extends Metric> implements Metric {

    // NOTICE: delegate can be reset!
    protected T delegate;

    public AbstractMetricProxy(final T delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
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
