package com.tereigo.expr.metrics.serializers;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;

public interface Serializer<T> {
    T serialize(int nodeId, BoolMetric metric);
    T serialize(int nodeId, LongMetric metric);
}
