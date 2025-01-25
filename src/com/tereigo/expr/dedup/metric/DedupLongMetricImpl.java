package com.tereigo.expr.dedup.metric;

public final class DedupLongMetricImpl implements DedupLongMetric {
    private final SimpleLongMetric delegate;
    private long lastValue = Long.MIN_VALUE;

    public DedupLongMetricImpl(final SimpleLongMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public SimpleLongMetric getUnderlying() {
        return delegate;
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
        return lastValue != get();
    }

    @Override
    public void onPublished() {
        lastValue = get();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
