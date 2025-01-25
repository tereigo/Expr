package com.tereigo.expr.dedup.metric;

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
