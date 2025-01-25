package com.tereigo.expr.metrics;

import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.producer.MetricFactory;

import java.util.function.LongSupplier;

public class Client2 {
    private final SimpleLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public Client2(MetricFactory factory) {
        this.dupMetric = factory.createLong("client2_dupMetric", true);
        this.dedupMetric = factory.createLongDedup("client2_dedupMetric", true);
    }

    public void update(LongSupplier supplier) {
        dupMetric.increment(supplier.getAsLong());
        dedupMetric.increment(supplier.getAsLong());
    }

//    public void send(Sender sender) {
//        sender.send(dupMetric);
//        sender.send(dedupMetric);
//    }
}
