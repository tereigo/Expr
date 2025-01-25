package com.tereigo.expr.metrics.producer.impl;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.producer.MetricPublisher;

public class ConsolePublisher implements MetricPublisher {

    @Override
    public void publish(int nodeId, BoolMetric metric) {
        System.out.println("Node " + nodeId + " - " + metric);
    }

    @Override
    public void publish(int nodeId, LongMetric metric) {
        System.out.println("Node " + nodeId + " - " + metric);
    }
}
