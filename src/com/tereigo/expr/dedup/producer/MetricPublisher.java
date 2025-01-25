package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.LongMetric;

public interface MetricPublisher {

    void publish(int nodeId, LongMetric metric);

}
