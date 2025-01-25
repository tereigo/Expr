package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;
import com.tereigo.expr.dedup.producer.Sender;
import com.tereigo.expr.dedup.producer.Store;

import java.util.List;

public class MetricsHandler {
    private final Store store;

    public MetricsHandler(Store store) {
        this.store = store;
    }

    public void publish(Sender sender) {
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
