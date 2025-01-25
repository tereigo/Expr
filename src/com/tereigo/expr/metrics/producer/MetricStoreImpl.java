package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.Metric;
import com.tereigo.expr.metrics.metric.MetricRegistration;
import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;

import java.util.ArrayList;
import java.util.List;

public class MetricStoreImpl implements MetricStore, RegistrationListener {
    private final List<SimpleBoolMetric> boolMetrics = new ArrayList<>();
    private final List<DedupBoolMetric> boolDedupMetrics = new ArrayList<>();
    private final List<SimpleLongMetric> longMetrics = new ArrayList<>();
    private final List<DedupLongMetric> longDedupMetrics = new ArrayList<>();

    @Override
    public void onMetricRegistration(int nodeId, MetricRegistration registration, boolean autoPublish, Metric metric) {
        if (autoPublish) {
            switch (metric) {
                case DedupBoolMetric dedup -> boolDedupMetrics.add(dedup);
                case SimpleBoolMetric boolMetric -> boolMetrics.add(boolMetric);
                case DedupLongMetric dedup -> longDedupMetrics.add(dedup);
                case SimpleLongMetric longMetric -> longMetrics.add(longMetric);
                default -> throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
            }
        }
    }

    @Override
    public List<SimpleBoolMetric> getBoolMetrics() {
        return boolMetrics;
    }

    @Override
    public List<DedupBoolMetric> getDedupBoolMetrics() {
        return boolDedupMetrics;
    }

    @Override
    public List<SimpleLongMetric> getLongMetrics() {
        return longMetrics;
    }

    @Override
    public List<DedupLongMetric> getDedupLongMetrics() {
        return longDedupMetrics;
    }
}
