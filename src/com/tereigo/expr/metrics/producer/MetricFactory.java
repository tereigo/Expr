package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;

public interface MetricFactory {

    SimpleBoolMetric createBool(String name, boolean autoPublish);

    DedupBoolMetric createBoolDedup(String name, boolean autoPublish);

    SimpleLongMetric createLong(String name, boolean autoPublish);

    DedupLongMetric createLongDedup(String name, boolean autoPublish);

}
