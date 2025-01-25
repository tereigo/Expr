package com.tereigo.expr.metrics.metric.dedup.impl;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.impl.MutableBoolMetricProxy;

public final class DedupBoolMetricProxy extends MutableBoolMetricProxy
                                        implements DedupBoolMetric {

    public DedupBoolMetricProxy(final DedupBoolMetric delegate) {
        super(delegate);
    }

    public void reset(DedupBoolMetric newDelegate) {
        newDelegate.set(delegate.get());
        delegate = newDelegate;
    }

    @Override
    public SimpleBoolMetric getUnderlying() {
        return ((DedupBoolMetric)delegate).getUnderlying();
    }

    @Override
    public boolean needsPublishing() {
        return ((DedupBoolMetric)delegate).needsPublishing();
    }

    @Override
    public void onPublished() {
        ((DedupBoolMetric)delegate).onPublished();
    }
}
