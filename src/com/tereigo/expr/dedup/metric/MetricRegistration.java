package com.tereigo.expr.dedup.metric;

public interface MetricRegistration {
    String getMetricName();
    int getMetricId();
    String getSessionName();
    String getNodeName();
}
