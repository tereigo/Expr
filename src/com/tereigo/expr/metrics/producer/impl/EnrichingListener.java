package com.tereigo.expr.metrics.producer.impl;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.metric.MetricRegistration;
import com.tereigo.expr.metrics.producer.MetricEnrichedListener;
import com.tereigo.expr.metrics.producer.MetricListener;

import java.util.HashMap;
import java.util.Map;

public final class EnrichingListener implements MetricListener {
    private final Map<Long, MetricRegistration> registrations = new HashMap<>();
    private final MetricEnrichedListener listener;

    public EnrichingListener(MetricEnrichedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMetricRegistration(int nodeId, MetricRegistration registration) {
        registrations.put(key(nodeId, registration.getMetricId()), registration);
        listener.onMetricRegistration(nodeId, registration);
    }

    @Override
    public void onBoolMetric(int nodeId, BoolMetric metric) {
        MetricRegistration registration = registrations.get(key(nodeId, metric.getMetricId()));
        if (registration != null) {
            listener.onBoolMetric(nodeId, registration, metric);
        }
    }

    @Override
    public void onLongMetric(int nodeId, LongMetric metric) {
        MetricRegistration registration = registrations.get(key(nodeId, metric.getMetricId()));
        if (registration != null) {
            listener.onLongMetric(nodeId, registration, metric);
        }
    }

    private static long key(int nodeId, int metricId) {
        return ((long) nodeId << 32) | metricId;
    }
}
