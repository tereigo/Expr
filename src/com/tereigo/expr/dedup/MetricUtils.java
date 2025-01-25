package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.metric.LongMetric;

public class MetricUtils {

    public static void publish(LongMetric metric) {
        System.out.println("Sending metric: " + metric.getName() + "=" + metric.get());
    }

    private MetricUtils() { }
}
