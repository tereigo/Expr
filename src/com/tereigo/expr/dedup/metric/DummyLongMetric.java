package com.tereigo.expr.dedup.metric;

/**
 * A dummy implementation of {@link DedupLongMetric} for DelayedFactory until Node is activated
 * It doesn't allow publishing before node is activated by throwing an exception
 */
public final class DummyLongMetric implements DedupLongMetric {
    private final SimpleLongMetric delegate;

    public DummyLongMetric(final SimpleLongMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public SimpleLongMetric getUnderlying() {
        return delegate;
    }

    @Override
    public boolean needsPublishing() {
        throw new UnsupportedOperationException("We should not publish before node is activated");
    }

    @Override
    public void onPublished() {
        throw new UnsupportedOperationException("We should not publish before node is activated");
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
