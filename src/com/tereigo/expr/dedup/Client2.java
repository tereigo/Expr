package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.MutableLongMetric;
import com.tereigo.expr.dedup.producer.Factory;

import java.util.function.LongSupplier;

public class Client2 {
    private final MutableLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public Client2(Factory factory) {
        this.dupMetric = factory.create("client2_dupMetric", true);
        this.dedupMetric = factory.createDedup("client2_dedupMetric", true);
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
