package com.tereigo.expr.dedup.metric;

/**
 * A dummy implementation of {@link DedupLongMetric} for DelayedFactory until Node is activated
 * It doesn't allow publishing before node is activated by throwing an exception
 */
public final class DedupDummyLongMetric extends MutableLongMetricProxy
                                        implements DedupLongMetric {

    public DedupDummyLongMetric(final SimpleLongMetric delegate) {
        super(delegate);
    }

    @Override
    public SimpleLongMetric getUnderlying() {
        throw new UnsupportedOperationException("We should not publish before node is activated");
    }

    @Override
    public boolean needsPublishing() {
        throw new UnsupportedOperationException("We should not publish before node is activated");
    }

    @Override
    public void onPublished() {
        throw new UnsupportedOperationException("We should not publish before node is activated");
    }
}
