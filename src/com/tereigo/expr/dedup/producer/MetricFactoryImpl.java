package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.*;
import com.tereigo.expr.dedup.node.NodeInfoProvider;

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
    public SimpleLongMetric create(final String name, boolean autoPublish) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        SimpleLongMetric result = new LongMetricImpl(name, metricId);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public DedupLongMetric createDedup(String name, boolean autoPublish) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        SimpleLongMetric realMetric = new LongMetricImpl(name, metricId);
        DedupLongMetric result = new DedupLongMetricImpl(realMetric);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }
}
