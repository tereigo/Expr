package com.tereigo.expr.metrics.metric.dedup.impl;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.impl.MutableBoolMetricProxy;

public final class DedupBoolMetricImpl extends MutableBoolMetricProxy
                                       implements DedupBoolMetric {
    private boolean published = false;
    private boolean lastValue = false;

    public DedupBoolMetricImpl(final SimpleBoolMetric delegate) {
        super(delegate);
    }

    @Override
    public SimpleBoolMetric getUnderlying() {
        return (SimpleBoolMetric)delegate;
    }

    @Override
    public boolean needsPublishing() {
        return !published || lastValue != get();
    }

    @Override
    public void onPublished() {
        lastValue = get();
        published = true;
    }
}
