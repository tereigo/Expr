package com.tereigo.expr.dedup.metric;

public class LongMetricProxy implements SimpleLongMetric, MetricProxy<SimpleLongMetric> {
    private SimpleLongMetric delegate;

    public LongMetricProxy(final SimpleLongMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public SimpleLongMetric getDelegate() {
        return delegate;
    }

    public void reset(SimpleLongMetric newDelegate) {
        newDelegate.set(delegate.get());
        delegate = newDelegate;
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
}
