package com.tereigo.expr.dedup.metric;

public class MetricRegistrationImpl implements MetricRegistration {
    private final String metricName;
    private final int metricId;
    private final String sessionName;
    private final String nodeName;

    public MetricRegistrationImpl(String metricName, int metricId, String sessionName, String nodeName) {
        this.metricName = metricName;
        this.metricId = metricId;
        this.sessionName = sessionName;
        this.nodeName = nodeName;
    }

    @Override
    public String getMetricName() {
        return metricName;
    }

    @Override
    public int getMetricId() {
        return metricId;
    }

    @Override
    public String getSessionName() {
        return sessionName;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }
}
