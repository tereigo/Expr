package com.tereigo.expr.dedup.not_used;

import com.tereigo.expr.dedup.metric.Metric;

public interface MetricProxy<M extends Metric> {
    M getDelegate();
}
