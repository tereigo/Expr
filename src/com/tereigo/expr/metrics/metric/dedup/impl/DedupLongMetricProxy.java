package com.tereigo.expr.metrics.metric.dedup.impl;

import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.metric.impl.MutableLongMetricProxy;

public final class DedupLongMetricProxy extends MutableLongMetricProxy
                                        implements DedupLongMetric {

    public DedupLongMetricProxy(final DedupLongMetric delegate) {
        super(delegate);
    }

    public void reset(DedupLongMetric newDelegate) {
        newDelegate.set(delegate.get());
        delegate = newDelegate;
    }

    @Override
    public SimpleLongMetric getUnderlying() {
        return ((DedupLongMetric)delegate).getUnderlying();
    }

    @Override
    public boolean needsPublishing() {
        return ((DedupLongMetric)delegate).needsPublishing();
    }

    @Override
    public void onPublished() {
        ((DedupLongMetric)delegate).onPublished();
    }
}
