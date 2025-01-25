package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;
import com.tereigo.expr.dedup.producer.MetricFactory;
import com.tereigo.expr.dedup.producer.MetricSender;

import java.util.function.LongSupplier;

public class ManualClient {

    private final SimpleLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public ManualClient(MetricFactory factory, String prefix) {
        this.dupMetric = factory.create(prefix + "_manualClient_DupMetric", false);
        this.dedupMetric = factory.createDedup(prefix + "_manualClient_DedupMetric", false);
    }

    public void update(LongSupplier supplier) {
        dupMetric.increment(supplier.getAsLong());
        dedupMetric.increment(supplier.getAsLong());
    }

    public void send(MetricSender sender) {
        sender.send(dupMetric);
        sender.send(dedupMetric);
    }
}
