package com.tereigo.expr.dedup.metric;

public interface DedupMetric<T extends Metric> {

    boolean needsPublishing();

    void onPublished();

    T getUnderlying();

//    void publishIfChanged(Sender sender);
}
