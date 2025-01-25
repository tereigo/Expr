package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;

public interface MetricSender {

    void send(SimpleLongMetric metric);

    default void send(DedupLongMetric metric) {
        if (metric.needsPublishing()) {
            send(metric.getUnderlying());
            metric.onPublished();
        }
    }

//    void send(LongMetricProxy metric);

}