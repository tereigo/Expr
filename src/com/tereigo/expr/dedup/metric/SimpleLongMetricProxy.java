package com.tereigo.expr.dedup.metric;

public abstract class SimpleLongMetricProxy implements SimpleLongMetric {

    // NOTICE: delegate can be reset!
    protected SimpleLongMetric delegate;

    public SimpleLongMetricProxy(final SimpleLongMetric delegate) {
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
