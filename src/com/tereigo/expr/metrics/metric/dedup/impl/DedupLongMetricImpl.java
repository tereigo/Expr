package com.tereigo.expr.metrics.metric.dedup.impl;

import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.metric.impl.MutableLongMetricProxy;

public final class DedupLongMetricImpl extends MutableLongMetricProxy
                                       implements DedupLongMetric {
    private long lastValue = Long.MIN_VALUE;

    public DedupLongMetricImpl(final SimpleLongMetric delegate) {
        super(delegate);
    }

    @Override
    public SimpleLongMetric getUnderlying() {
        return (SimpleLongMetric)delegate;
    }

    @Override
    public boolean needsPublishing() {
        return lastValue != get();
    }

    @Override
    public void onPublished() {
        lastValue = get();
    }
}
