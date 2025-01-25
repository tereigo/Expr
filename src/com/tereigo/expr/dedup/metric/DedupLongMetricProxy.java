package com.tereigo.expr.dedup.metric;

public class DedupLongMetricProxy implements DedupLongMetric, MetricProxy<DedupLongMetric> {
    private DedupLongMetric delegate;

    public DedupLongMetricProxy(final DedupLongMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public SimpleLongMetric getUnderlying() {
        return delegate.getUnderlying();
    }

    @Override
    public DedupLongMetric getDelegate() {
        return delegate;
    }

    public void reset(DedupLongMetric newDelegate) {
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

    @Override
    public boolean needsPublishing() {
        return delegate.needsPublishing();
    }

    @Override
    public void onPublished() {
        delegate.onPublished();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
