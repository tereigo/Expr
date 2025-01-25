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

    private DedupLongMetric castDelegate() {
        return (DedupLongMetric)delegate;
    }

    @Override
    public SimpleLongMetric getUnderlying() {
        return castDelegate().getUnderlying();
    }

    @Override
    public boolean needsPublishing() {
        return castDelegate().needsPublishing();
    }

    @Override
    public void onPublished() {
        castDelegate().onPublished();
    }
}
