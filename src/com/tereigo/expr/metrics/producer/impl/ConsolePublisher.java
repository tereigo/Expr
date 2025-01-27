package com.tereigo.expr.metrics.producer.impl;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.producer.MetricPublisher;
import com.tereigo.expr.metrics.serializers.Serializer;

public class ConsolePublisher implements MetricPublisher {

    private final Serializer<String> serializer;

    public ConsolePublisher(Serializer<String> serializer) {
        this.serializer = serializer;
    }

     @Override
    public void publish(int nodeId, BoolMetric metric) {
        System.out.println(serializer.serialize(nodeId, metric));
    }

    @Override
    public void publish(int nodeId, LongMetric metric) {
        System.out.println(serializer.serialize(nodeId, metric));
    }
}
