package com.tereigo.expr.metrics;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.producer.MetricFactory;

import java.util.function.LongSupplier;

public class AppMetrics {
    private final SimpleBoolMetric boolMetric;
    private final DedupBoolMetric dedupBoolMetric;
    private final SimpleLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public AppMetrics(MetricFactory factory) {
        this.boolMetric = factory.createBool("appClient_BoolMetric", true);
        this.dedupBoolMetric = factory.createBoolDedup("appClient_DedupBoolMetric", true);
        this.dupMetric = factory.createLong("appClient_DupMetric", true);
        this.dedupMetric = factory.createLongDedup("appClient_DedupMetric", true);
    }

    public void update(LongSupplier supplier) {
        boolMetric.set(supplier.getAsLong() % 2 == 0);
        dedupBoolMetric.set(supplier.getAsLong() % 2 == 0);
        dupMetric.increment(supplier.getAsLong());
        dedupMetric.increment(supplier.getAsLong());
    }
}
