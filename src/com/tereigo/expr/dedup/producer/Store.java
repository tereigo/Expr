package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.LongMetricImpl;

import java.util.List;

public interface Store {

    List<LongMetricImpl> getLongMetrics();

    List<DedupLongMetric> getDedupLongMetrics();
}
