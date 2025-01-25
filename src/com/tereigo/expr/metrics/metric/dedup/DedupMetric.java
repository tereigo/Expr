package com.tereigo.expr.metrics.metric.dedup;

import com.tereigo.expr.metrics.metric.SimpleMetric;

public interface DedupMetric<T extends SimpleMetric> {

    T getUnderlying();

    boolean needsPublishing();

    void onPublished();

//    void publishIfChanged(Sender sender);
}
