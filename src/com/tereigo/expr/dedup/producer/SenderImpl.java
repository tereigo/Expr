package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.MetricUtils;
import com.tereigo.expr.dedup.metric.*;

public class SenderImpl implements Sender {

    @Override
    public void send(LongMetric metric) {
        switch (metric) {
            case DedupLongMetric dedup -> Dedup.sendIfChanged(this, dedup);
            case LongMetricProxy proxy -> send(proxy.getDelegate());
            default -> MetricUtils.publish(metric);
        }
//        if (metric instanceof DedupLongMetric dedup) {
//            Dedup.sendIfChanged(this, dedup);
//        } else if (metric instanceof LongMetricProxy proxy) {
//            send(proxy.getDelegate());
//        } else {
//            sendImpl(metric);
//        }
    }

    @Override
    public void send(DedupLongMetric metric) {
        Dedup.sendIfChanged(this, metric);
    }

    @Override
    public void send(LongMetricImpl metric) {
        MetricUtils.publish(metric);
    }
}
