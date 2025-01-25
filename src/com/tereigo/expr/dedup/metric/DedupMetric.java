package com.tereigo.expr.dedup.metric;

public interface DedupMetric {

    boolean needsPublishing();

    void onPublished();

    //void publishIfChanged(Sender sender);
}
