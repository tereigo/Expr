package com.tereigo.expr.metrics.metric.dedup;

import com.tereigo.expr.metrics.metric.MutableLongMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;

public interface DedupLongMetric extends MutableLongMetric, DedupMetric<SimpleLongMetric> {

}
