package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.*;
import com.tereigo.expr.dedup.node.NodeInfoProvider;

public class FactoryImpl implements Factory {

    private final NodeInfoProvider nodeInfoProvider;
    private RegistrationListener listener = RegistrationListener.NOOP;
    private int counter = 0;

    public FactoryImpl(NodeInfoProvider nodeInfoProvider) {
        this.nodeInfoProvider = nodeInfoProvider;
    }

    public void setListener(RegistrationListener listener) {
        this.listener = listener;
    }

    @Override
    public SimpleLongMetric create(final String name, boolean autoPublish) {
        int metricId = ++counter;
        SimpleLongMetric result = new LongMetricImpl(name, metricId);
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public DedupLongMetric createDedup(String name, boolean autoPublish) {
        int metricId = ++counter;
        SimpleLongMetric realMetric = new LongMetricImpl(name, metricId);
        DedupLongMetric result = new DedupLongMetric(realMetric);
        MetricRegistration registration = new MetricRegistrationImpl(name, metricId,
                nodeInfoProvider.getSessionName(), nodeInfoProvider.getNodeName());
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }
}
