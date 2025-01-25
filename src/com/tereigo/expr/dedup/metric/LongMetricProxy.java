package com.tereigo.expr.dedup.metric;

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
