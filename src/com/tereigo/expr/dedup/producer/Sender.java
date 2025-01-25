package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.LongMetric;
import com.tereigo.expr.dedup.metric.LongMetricImpl;

public interface Sender {

    void send(LongMetric metric);
    void send(DedupLongMetric metric);
    void send(LongMetricImpl proxy);

}