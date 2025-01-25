package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.MutableLongMetric;
import com.tereigo.expr.dedup.producer.Factory;
import com.tereigo.expr.dedup.producer.Sender;

import java.util.function.LongSupplier;

public class ManualClient {

    private final MutableLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public ManualClient(Factory factory, String prefix) {
        this.dupMetric = factory.create(prefix + "_manualClient_DupMetric", false);
        this.dedupMetric = factory.createDedup(prefix + "_manualClient_DedupMetric", false);
    }

    public void update(LongSupplier supplier) {
        dupMetric.increment(supplier.getAsLong());
        dedupMetric.increment(supplier.getAsLong());
    }

    public void send(Sender sender) {
        sender.send(dupMetric);
        sender.send(dedupMetric);
    }
}
