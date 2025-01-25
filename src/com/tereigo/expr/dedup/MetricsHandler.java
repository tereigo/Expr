package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;
import com.tereigo.expr.dedup.producer.MetricSender;
import com.tereigo.expr.dedup.producer.MetricStore;

import java.util.List;

public class MetricsHandler {
    private final MetricStore store;

    public MetricsHandler(MetricStore store) {
        this.store = store;
    }

    public void publish(MetricSender sender) {
        List<SimpleLongMetric> metrics = store.getLongMetrics();
        for (SimpleLongMetric metric: metrics) {
            sender.send(metric);
        }
        List<DedupLongMetric> dedupMetrics = store.getDedupLongMetrics();
        for (DedupLongMetric metric: dedupMetrics) {
            sender.send(metric);
        }
    }
}
