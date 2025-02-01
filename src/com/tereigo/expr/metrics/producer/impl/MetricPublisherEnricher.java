package com.tereigo.expr.metrics.producer.impl;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.metric.MetricRegistration;
import com.tereigo.expr.metrics.producer.MetricEnrichedPublisher;
import com.tereigo.expr.metrics.producer.MetricPublisher;

import java.util.HashMap;
import java.util.Map;

public class MetricPublisherEnricher implements MetricPublisher {
    private Map<Long, MetricRegistration> metrics = new HashMap<>();
    private final MetricEnrichedPublisher publisher;

    public MetricPublisherEnricher(MetricEnrichedPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(int nodeId, MetricRegistration registration) {
        metrics.put(key(nodeId, registration.getMetricId()), registration);
        publisher.publish(nodeId, registration);
    }

    @Override
    public void publish(int nodeId, LongMetric metric) {
        MetricRegistration registration = metrics.get(key(nodeId, metric.getMetricId()));
        if (registration != null) {
            publisher.publish(nodeId, registration, metric);
        }
    }

    @Override
    public void publish(int nodeId, BoolMetric metric) {
        MetricRegistration registration = metrics.get(key(nodeId, metric.getMetricId()));
        if (registration != null) {
            publisher.publish(nodeId, registration, metric);
        }
    }

    private static long key(int nodeId, int metricId) {
        return ((long) nodeId << 32) | metricId;
    }
}
