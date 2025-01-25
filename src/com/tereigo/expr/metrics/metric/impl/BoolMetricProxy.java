package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.MetricProxy;
import com.tereigo.expr.metrics.metric.SimpleBoolMetric;

public class BoolMetricProxy extends MutableBoolMetricProxy
                             implements SimpleBoolMetric, MetricProxy<SimpleBoolMetric> {

    public BoolMetricProxy(final SimpleBoolMetric delegate) {
        super(delegate);
    }

    @Override
    public void reset(SimpleBoolMetric newDelegate) {
        newDelegate.set(delegate.get());
        delegate = newDelegate;
    }
}
