package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;

import java.util.List;

public interface MetricStore {

    List<SimpleBoolMetric> getBoolMetrics();

    List<DedupBoolMetric> getDedupBoolMetrics();

    List<SimpleLongMetric> getLongMetrics();

    List<DedupLongMetric> getDedupLongMetrics();
}
