package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;

import java.util.List;

public interface Store {

    List<SimpleLongMetric> getLongMetrics();

    List<DedupLongMetric> getDedupLongMetrics();
}
