package com.tereigo.expr.dedup.metric;

public class LongMetricProxy implements MutableLongMetric, MetricProxy<MutableLongMetric> {
    private MutableLongMetric delegate;

    public LongMetricProxy(final MutableLongMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public MutableLongMetric getDelegate() {
        return delegate;
    }

    public void reset(MutableLongMetric newDelegate) {
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
