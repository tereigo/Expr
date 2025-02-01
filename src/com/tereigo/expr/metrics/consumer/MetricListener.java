package com.tereigo.expr.metrics.consumer;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.metric.MetricRegistration;

public interface MetricListener {

    void onMetricRegistration(int nodeId, MetricRegistration registration);

    void onBoolMetric(int nodeId, BoolMetric metric);

    void onLongMetric(int nodeId, LongMetric metric);

}
