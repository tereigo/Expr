package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.MetricRegistration;
import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.metric.dedup.impl.DedupBoolMetricImpl;
import com.tereigo.expr.metrics.metric.dedup.impl.DedupLongMetricImpl;
import com.tereigo.expr.metrics.metric.impl.BoolMetricImpl;
import com.tereigo.expr.metrics.metric.impl.LongMetricImpl;
import com.tereigo.expr.metrics.metric.impl.MetricRegistrationImpl;
import com.tereigo.expr.metrics.node.NodeInfoProvider;

public class MetricFactoryImpl implements MetricFactory {

    private final NodeInfoProvider nodeInfoProvider;
    private RegistrationListener listener = RegistrationListener.NOOP;
    private int counter = 0;

    public MetricFactoryImpl(NodeInfoProvider nodeInfoProvider) {
        this.nodeInfoProvider = nodeInfoProvider;
    }

    public void setListener(RegistrationListener listener) {
        this.listener = listener;
    }

    @Override
    public SimpleLongMetric createLong(final String name, boolean autoPublish) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        SimpleLongMetric result = new LongMetricImpl(name, metricId);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public DedupLongMetric createLongDedup(String name, boolean autoPublish) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        SimpleLongMetric realMetric = new LongMetricImpl(name, metricId);
        DedupLongMetric result = new DedupLongMetricImpl(realMetric);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public SimpleBoolMetric createBool(final String name, boolean autoPublish) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        SimpleBoolMetric result = new BoolMetricImpl(name, metricId);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public DedupBoolMetric createBoolDedup(String name, boolean autoPublish) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        SimpleBoolMetric realMetric = new BoolMetricImpl(name, metricId);
        DedupBoolMetric result = new DedupBoolMetricImpl(realMetric);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }
}
