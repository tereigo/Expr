package com.tereigo.expr.metrics.metric.dedup.impl;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.impl.MutableBoolMetricProxy;

/**
 * A dummy implementation of {@link DedupBoolMetric} for DelayedFactory until Node is activated
 * It doesn't allow publishing before node is activated by throwing an exception
 */
public final class DedupDummyBoolMetric extends MutableBoolMetricProxy
                                        implements DedupBoolMetric {

    public DedupDummyBoolMetric(final SimpleBoolMetric delegate) {
        super(delegate);
    }

    @Override
    public SimpleBoolMetric getUnderlying() {
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
