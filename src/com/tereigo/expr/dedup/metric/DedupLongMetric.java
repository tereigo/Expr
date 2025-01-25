package com.tereigo.expr.dedup.metric;

public class DedupLongMetric extends LongMetricProxy implements DedupMetric {
    private long lastValue = Long.MIN_VALUE;

    public DedupLongMetric(MutableLongMetric delegate) {
        super(delegate);
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
