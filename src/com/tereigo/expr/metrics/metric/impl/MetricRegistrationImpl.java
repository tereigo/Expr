package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.MetricRegistration;
import com.tereigo.expr.metrics.serializers.MetricType;

import java.util.List;
import java.util.Map;

public class MetricRegistrationImpl implements MetricRegistration {
    private final MetricType metricType;
    private final String metricName;
    private final int metricId;
    private final String nodeName;
    private final Map<String, String> tags;
    private final String[] keyNames;

    public MetricRegistrationImpl(String metricName, MetricType metricType, int metricId, String nodeName,
                                  final Map<String, String> tags, final List<String> keyNames) {
        this.metricName = metricName;
        this.metricType = metricType;
        this.metricId = metricId;
        this.nodeName = nodeName;
        this.tags = Map.copyOf(tags);
        this.keyNames = keyNames.toArray(new String[0]);
    }

    @Override
    public MetricType getMetricType() {
        return metricType;
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
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public String[] getKeyNames() {
        return keyNames;
    }
}
