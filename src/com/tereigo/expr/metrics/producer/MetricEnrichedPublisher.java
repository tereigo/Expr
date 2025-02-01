package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.metric.MetricRegistration;

public interface MetricEnrichedPublisher {

    void publish(int nodeId, MetricRegistration registration);

    void publish(int nodeId, MetricRegistration meta, LongMetric metric);

    void publish(int nodeId, MetricRegistration meta, BoolMetric metric);

}
