package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.LongMetricImpl;
import com.tereigo.expr.dedup.metric.Metric;
import com.tereigo.expr.dedup.metric.MetricRegistration;

import java.util.ArrayList;
import java.util.List;

public class StoreImpl implements Store, RegistrationListener {
    private final List<LongMetricImpl> longMetrics = new ArrayList<>();
    private final List<DedupLongMetric> longDedupMetrics = new ArrayList<>();

    @Override
    public void onMetricRegistration(int nodeId, MetricRegistration registration, boolean autoPublish, Metric metric) {
        if (autoPublish) {
            if (metric instanceof DedupLongMetric dedup) {
                longDedupMetrics.add(dedup);
            } else if (metric instanceof LongMetricImpl longMetric) {
                longMetrics.add(longMetric);
            } else {
                throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
            }
        }
    }

    @Override
    public List<LongMetricImpl> getLongMetrics() {
        return longMetrics;
    }

    @Override
    public List<DedupLongMetric> getDedupLongMetrics() {
        return longDedupMetrics;
    }
}
