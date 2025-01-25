package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.Metric;
import com.tereigo.expr.dedup.metric.MetricRegistration;

public interface RegistrationListener {

    void onMetricRegistration(int nodeId, MetricRegistration registration, boolean autoPublish, Metric metric);

    RegistrationListener NOOP = (n, x, y, z) -> {};

}
