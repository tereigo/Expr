package com.tereigo.expr.metrics.metric;

public interface MetricProxy<M extends Metric> {

    void reset(M newDelegate);
}
