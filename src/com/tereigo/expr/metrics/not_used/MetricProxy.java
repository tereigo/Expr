package com.tereigo.expr.metrics.not_used;

import com.tereigo.expr.metrics.metric.Metric;

public interface MetricProxy<M extends Metric> {
    M getDelegate();
}
