package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.LongMetric;

public class ConsolePublisher implements MetricPublisher {

    @Override
    public void publish(int nodeId, LongMetric metric) {
        System.out.println("Node " + nodeId + " - " + metric);
    }
}
