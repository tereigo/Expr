package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.LongMetricProxy;
import com.tereigo.expr.dedup.metric.SimpleLongMetric;

public interface Sender {

    void send(SimpleLongMetric proxy);

    void send(LongMetricProxy metric);

    void send(DedupLongMetric metric);

}