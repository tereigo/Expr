package com.tereigo.expr.metrics.producer.impl;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.node.NodeInfoProvider;
import com.tereigo.expr.metrics.producer.MetricPublisher;
import com.tereigo.expr.metrics.producer.MetricSender;

public class MetricSenderImpl implements MetricSender {

    private final NodeInfoProvider nodeInfoProvider;
    private final MetricPublisher publisher;

    public MetricSenderImpl(NodeInfoProvider nodeInfoProvider,
                            MetricPublisher publisher) {
        this.nodeInfoProvider = nodeInfoProvider;
        this.publisher = publisher;
    }

    @Override
    public void send(SimpleBoolMetric metric) {
        publisher.publish(nodeInfoProvider.getNodeId(), metric);
    }

    @Override
    public void send(SimpleLongMetric metric) {
        publisher.publish(nodeInfoProvider.getNodeId(), metric);
    }
}
