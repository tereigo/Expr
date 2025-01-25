package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.SimpleLongMetric;
import com.tereigo.expr.dedup.node.NodeInfoProvider;

public class MetricSenderImpl implements MetricSender {

    private final NodeInfoProvider nodeInfoProvider;
    private final MetricPublisher publisher;

    public MetricSenderImpl(NodeInfoProvider nodeInfoProvider,
                            MetricPublisher publisher) {
        this.nodeInfoProvider = nodeInfoProvider;
        this.publisher = publisher;
    }

    @Override
    public void send(SimpleLongMetric metric) {
        publisher.publish(nodeInfoProvider.getNodeId(), metric);
    }

//    @Override
//    public void send(LongMetricProxy metric) {
//        MetricUtils.publish(metric.getDelegate());
//    }

//    @Override
//    public void send(LongMetric metric) {
//        switch (metric) {
//            case DedupLongMetric dedup -> Dedup.sendIfChanged(this, dedup);
//            case LongMetricProxy proxy -> send(proxy.getDelegate());
//            default -> MetricUtils.publish(metric);
//        }
////        if (metric instanceof DedupLongMetric dedup) {
////            Dedup.sendIfChanged(this, dedup);
////        } else if (metric instanceof LongMetricProxy proxy) {
////            send(proxy.getDelegate());
////        } else {
////            sendImpl(metric);
////        }
//    }

}
