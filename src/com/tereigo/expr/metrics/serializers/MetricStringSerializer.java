package com.tereigo.expr.metrics.serializers;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.metric.MetricRegistration;
import com.tereigo.expr.metrics.producer.MetricSerializer;

public class MetricStringSerializer implements MetricSerializer<String> {

    @Override
    public String serialize(int nodeId, MetricRegistration registration) {
        return "Node " + nodeId + " - " + registration;
    }

    @Override
    public String serialize(int nodeId, BoolMetric metric) {
        return "Node " + nodeId + " - " + metric;
    }

    @Override
    public String serialize(int nodeId, LongMetric metric) {
        return "Node " + nodeId + " - " + metric;
    }
}
