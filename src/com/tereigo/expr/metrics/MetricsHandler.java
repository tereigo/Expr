package com.tereigo.expr.metrics;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.producer.MetricSender;
import com.tereigo.expr.metrics.producer.MetricStore;

import java.util.List;

public class MetricsHandler {
    private final MetricStore store;

    public MetricsHandler(MetricStore store) {
        this.store = store;
    }

    public void publish(MetricSender sender) {
        List<SimpleBoolMetric> boolMetrics = store.getBoolMetrics();
        for (SimpleBoolMetric metric: boolMetrics) {
            sender.send(metric);
        }
        List<DedupBoolMetric> dedupBoolMetrics = store.getDedupBoolMetrics();
        for (DedupBoolMetric metric: dedupBoolMetrics) {
            sender.send(metric);
        }
        List<SimpleLongMetric> longMetrics = store.getLongMetrics();
        for (SimpleLongMetric metric: longMetrics) {
            sender.send(metric);
        }
        List<DedupLongMetric> dedupLongMetrics = store.getDedupLongMetrics();
        for (DedupLongMetric metric: dedupLongMetrics) {
            sender.send(metric);
        }
    }
}
