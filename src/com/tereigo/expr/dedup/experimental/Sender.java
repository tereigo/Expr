package com.tereigo.expr.dedup.experimental;


import com.tereigo.expr.dedup.metric.LongMetric;

public interface Sender {

    void send(LongMetric metric);
    void send(DedupLongMetric2 metric);
    void send(LongMetricProxy proxy);
    void send(DedupMetric<LongMetric> metric);

}