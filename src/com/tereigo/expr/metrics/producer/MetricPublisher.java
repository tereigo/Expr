package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;

public interface MetricPublisher {

    void publish(int nodeId, LongMetric metric);

    void publish(int nodeId, BoolMetric metric);

}
