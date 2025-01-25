package com.tereigo.expr.metrics;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.producer.MetricFactory;
import com.tereigo.expr.metrics.producer.MetricSender;

import java.util.function.LongSupplier;

public class ManualClient {
    private final SimpleBoolMetric boolMetric;
    private final DedupBoolMetric dedupBoolMetric;
    private final SimpleLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public ManualClient(MetricFactory factory, String prefix) {
        this.boolMetric = factory.createBool(prefix + "_manualClient_BoolMetric", false);
        this.dedupBoolMetric = factory.createBoolDedup(prefix + "_manualClient_DedupBoolMetric", false);
        this.dupMetric = factory.createLong(prefix + "_manualClient_DupMetric", false);
        this.dedupMetric = factory.createLongDedup(prefix + "_manualClient_DedupMetric", false);
    }

    public void update(LongSupplier supplier) {
        boolMetric.set(supplier.getAsLong() % 2 == 0);
        dedupBoolMetric.set(supplier.getAsLong() % 2 == 0);
        dupMetric.increment(supplier.getAsLong());
        dedupMetric.increment(supplier.getAsLong());
    }

    public void send(MetricSender sender) {
        sender.send(boolMetric);
        sender.send(dedupBoolMetric);
        sender.send(dupMetric);
        sender.send(dedupMetric);
    }
}
