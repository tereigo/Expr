package com.tereigo.expr.metrics.metric;

public interface MetricRegistration {
    String getMetricName();
    int getMetricId();
    String getSessionName();
    String getNodeName();
}
