package com.tereigo.expr.metrics.metric.impl;

import com.tereigo.expr.metrics.metric.SimpleLongMetric;

public class LongMetricProxy extends MutableLongMetricProxy
                             implements SimpleLongMetric {

    public LongMetricProxy(final SimpleLongMetric delegate) {
        super(delegate);
    }

    public void reset(SimpleLongMetric newDelegate) {
        newDelegate.set(delegate.get());
        delegate = newDelegate;
    }
}
