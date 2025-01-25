package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;

public interface MetricSender {

    void send(SimpleBoolMetric metric);

    default void send(DedupBoolMetric metric) {
        if (metric.needsPublishing()) {
            send(metric.getUnderlying());
            metric.onPublished();
        }
    }

    void send(SimpleLongMetric metric);

    default void send(DedupLongMetric metric) {
        if (metric.needsPublishing()) {
            send(metric.getUnderlying());
            metric.onPublished();
        }
    }
}