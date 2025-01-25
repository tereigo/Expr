package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;

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
