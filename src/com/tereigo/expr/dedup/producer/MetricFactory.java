package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;

public interface MetricFactory {

    SimpleLongMetric create(String name, boolean autoPublish);

    DedupLongMetric createDedup(String name, boolean autoPublish);
}
