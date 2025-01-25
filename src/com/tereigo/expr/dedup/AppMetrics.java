package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;
import com.tereigo.expr.dedup.producer.Factory;

import java.util.function.LongSupplier;

public class AppMetrics {
    private final SimpleLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public AppMetrics(Factory factory) {
        this.dupMetric = factory.create("appClient_DupMetric", true);
        this.dedupMetric = factory.createDedup("appClient_DedupMetric", true);
    }

    public void update(LongSupplier supplier) {
        dupMetric.increment(supplier.getAsLong());
        dedupMetric.increment(supplier.getAsLong());
    }
}
