package com.tereigo.expr.metrics;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.params.BoolParam;
import com.tereigo.expr.metrics.producer.MetricFactory;

import java.util.Map;

import static com.tereigo.expr.metrics.producer.MetricFactory.builder;

public class OrderClient {
    private static final BoolParam TEST_METRIC = new BoolParam("appClient_TestMetric");

    private final SimpleBoolMetric testMetric;

    private final MetricFactory factory;
//    private final MetricPool pool1;
//    private final MetricPool pool2;
    private final SimpleLongMetric dupMetric;
    private final DedupLongMetric dedupMetric;

    public OrderClient(MetricFactory factory) {
        this.factory = factory;
        this.dupMetric = factory.createLong("client1_dupMetric", true); //, () -> getOrderId());
        this.dedupMetric = factory.createLongDedup("client1_dedupMetric", true);
        this.testMetric = factory.create(TEST_METRIC, true,
                builder().keys(Map.of("orderId", this::getOrderId)));

    }

    public int getOrderId() {
        return 1;
    }

    public void onNewOrder(int orderId) {
//        this.dupMetric = pool1.take();
//        this.dedupMetric = pool2.take();
    }

//    public void onOrderTerminated(int orderId) {
//        pool1.release(dupMetric);
//        pool2.release(dedupMetric);
//    }

//    public void send(Sender sender) {
//        sender.send(dupMetric);
//        sender.send(dedupMetric);
//    }
}
