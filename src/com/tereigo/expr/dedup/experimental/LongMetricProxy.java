package com.tereigo.expr.dedup.experimental;

import com.tereigo.expr.dedup.metric.MutableLongMetric;

public class LongMetricProxy implements MutableLongMetric {
    private MutableLongMetric delegate;

    public LongMetricProxy(final MutableLongMetric delegate) {
        this.delegate = delegate;
    }

    MutableLongMetric getDelegate() {
        return delegate;
    }

    void reset(MutableLongMetric newDelegate) {
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
