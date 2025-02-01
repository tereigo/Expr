package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.metric.MetricRegistration;

public interface MetricSerializer<T> {
    T serialize(int nodeId, MetricRegistration registration);
    T serialize(int nodeId, BoolMetric metric);
    T serialize(int nodeId, LongMetric metric);
}
