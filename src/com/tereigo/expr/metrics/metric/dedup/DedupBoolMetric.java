package com.tereigo.expr.metrics.metric.dedup;

import com.tereigo.expr.metrics.metric.MutableBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleBoolMetric;

public interface DedupBoolMetric extends MutableBoolMetric, DedupMetric<SimpleBoolMetric> {

}
