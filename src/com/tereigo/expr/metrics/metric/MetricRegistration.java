package com.tereigo.expr.metrics.metric;

import com.tereigo.expr.metrics.serializers.MetricType;

import java.util.Map;

public interface MetricRegistration {
    MetricType getMetricType();
    String getMetricName();
    int getMetricId();
    String getNodeName();
    Map<String, String> getTags();
    String[] getKeyNames();
}
