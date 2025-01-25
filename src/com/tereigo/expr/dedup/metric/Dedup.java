package com.tereigo.expr.dedup.metric;

import com.tereigo.expr.dedup.MetricUtils;
import com.tereigo.expr.dedup.producer.SenderImpl;

public class Dedup {

    public static void sendIfChanged(SenderImpl sender, DedupLongMetric metric) {
        if (metric.needsPublishing()) {
            MetricUtils.publish(metric);
            metric.onPublished();
        }
    }
}
