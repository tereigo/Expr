package com.tereigo.expr.dedup.metric;

public interface DedupMetric<T extends Metric> {

    T getUnderlying();

    boolean needsPublishing();

    void onPublished();

//    void publishIfChanged(Sender sender);
}
