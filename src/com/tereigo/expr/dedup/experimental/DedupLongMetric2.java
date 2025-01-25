package com.tereigo.expr.dedup.experimental;

import com.tereigo.expr.dedup.metric.LongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;

public class DedupLongMetric2 extends LongMetricProxy implements DedupMetric<LongMetric> {
    private final DedupLongMetricImpl dedup;

    public DedupLongMetric2(SimpleLongMetric delegate) {
        super(delegate);
        dedup = new DedupLongMetricImpl(delegate);
    }

    @Override
    public boolean needsPublishing() {
        return dedup.needsPublishing();
    }

    @Override
    public void onPublished() {
        dedup.onPublished();
    }

    @Override
    public LongMetric getMetric() {
        return getDelegate();
    }
}
