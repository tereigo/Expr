package com.tereigo.expr.metrics.serializers;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;

public class MetricStringSerializer implements MetricSerializer<String> {

    @Override
    public String serialize(int nodeId, BoolMetric metric) {
        return "Node " + nodeId + " - " + metric;
    }

    @Override
    public String serialize(int nodeId, LongMetric metric) {
        return "Node " + nodeId + " - " + metric;
    }
}
