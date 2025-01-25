package com.tereigo.expr.dedup.experimental;

import com.tereigo.expr.dedup.metric.Metric;

public interface DedupMetric<T extends Metric> {
    boolean needsPublishing();
    void onPublished();
    T getMetric();
}
