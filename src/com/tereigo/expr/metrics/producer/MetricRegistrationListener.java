package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.Metric;
import com.tereigo.expr.metrics.metric.MetricRegistration;

public interface MetricRegistrationListener {

    void onMetricRegistration(int nodeId, MetricRegistration registration, boolean autoPublish, Metric metric);

    MetricRegistrationListener NOOP = (n, x, y, z) -> {};

}
