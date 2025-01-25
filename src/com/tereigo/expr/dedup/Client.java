package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;
import com.tereigo.expr.dedup.producer.Factory;

import java.util.function.LongSupplier;

public class Client {

    private final SimpleLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public Client(Factory factory) {
        this.dupMetric = factory.create("client1_dupMetric", true);
        this.dedupMetric = factory.createDedup("client1_dedupMetric", true);
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
