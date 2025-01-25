package com.tereigo.expr.dedup.metric;

public interface MetricProxy<M extends Metric> {
    M getDelegate();
}
