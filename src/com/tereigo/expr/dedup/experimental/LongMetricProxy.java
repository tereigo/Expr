package com.tereigo.expr.dedup.experimental;

import com.tereigo.expr.dedup.metric.MutableLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;

public class LongMetricProxy implements MutableLongMetric {
    private SimpleLongMetric delegate;

    public LongMetricProxy(final SimpleLongMetric delegate) {
        this.delegate = delegate;
    }

    SimpleLongMetric getDelegate() {
        return delegate;
    }

    void reset(SimpleLongMetric newDelegate) {
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
