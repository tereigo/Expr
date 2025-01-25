package com.tereigo.expr.dedup.experimental;

import com.tereigo.expr.dedup.metric.LongMetric;

public class DedupLongMetricImpl implements DedupMetric<LongMetric> {
    private final LongMetric delegate;
    private long lastValue;

    public DedupLongMetricImpl(LongMetric delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean needsPublishing() {
        return lastValue != delegate.get();
    }

    @Override
    public void onPublished() {
        lastValue = delegate.get();
    }

    @Override
    public LongMetric getMetric() {
        return delegate;
    }
}
