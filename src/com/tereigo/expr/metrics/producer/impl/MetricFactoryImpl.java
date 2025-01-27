package com.tereigo.expr.metrics.producer.impl;

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
import com.tereigo.expr.metrics.params.BoolParam;
import com.tereigo.expr.metrics.producer.MetricFactory;
import com.tereigo.expr.metrics.producer.RegistrationListener;
import com.tereigo.expr.metrics.serializers.MetricType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

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
    public SimpleBoolMetric create(BoolParam param, boolean initialValue, Builder builder) {
        int metricId = ++counter;
        List<String> keyNames = createKeyNames(builder);
        List<IntSupplier> keys = createKeys(builder);
        MetricRegistration registration = new MetricRegistrationImpl(param.getName(), MetricType.BOOL, metricId,
                nodeInfoProvider.getNodeName(), createTags(builder), keyNames);
        SimpleBoolMetric result = new BoolMetricImpl(param.getName(), metricId, initialValue, keys);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, builder.isAutoPublish(), result);
        return result;
    }

    public SimpleBoolMetric createBool(final String name, Map<String, String> tags, Map<String, IntSupplier> keys, boolean autoPublish) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, MetricType.BOOL, metricId,
                nodeInfoProvider.getNodeName(), Map.of(), List.of());
        SimpleBoolMetric result = new BoolMetricImpl(name, metricId, false, keys.values());
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public DedupBoolMetric createBoolDedup(String name, boolean autoPublish, Map<String, IntSupplier> keys) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, MetricType.BOOL, metricId,
                nodeInfoProvider.getNodeName(), Map.of(), List.of());
        SimpleBoolMetric realMetric = new BoolMetricImpl(name, metricId, false, keys.values());
        DedupBoolMetric result = new DedupBoolMetricImpl(realMetric);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public SimpleLongMetric createLong(final String name, boolean autoPublish, Map<String, IntSupplier> keys) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, MetricType.LONG, metricId,
                nodeInfoProvider.getNodeName(), Map.of(), List.of());
        SimpleLongMetric result = new LongMetricImpl(name, metricId, keys.values());
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    @Override
    public DedupLongMetric createLongDedup(String name, boolean autoPublish, Map<String, IntSupplier> keys) {
        int metricId = ++counter;
        MetricRegistration registration = new MetricRegistrationImpl(name, MetricType.LONG, metricId,
                nodeInfoProvider.getNodeName(), Map.of(), List.of());
        SimpleLongMetric realMetric = new LongMetricImpl(name, metricId, keys.values());
        DedupLongMetric result = new DedupLongMetricImpl(realMetric);
        listener.onMetricRegistration(nodeInfoProvider.getNodeId(), registration, autoPublish, result);
        return result;
    }

    private @NotNull Map<String, String> createTags(Builder builder) {
        Map<String, String> tags = new HashMap<>(builder.getTags());
        tags.put("session", nodeInfoProvider.getSessionName());
        return tags;
    }

    private static @NotNull List<String> createKeyNames(Builder builder) {
        ArrayList<String> result = new ArrayList<>();
        builder.getKeys().forEach((key, value) -> result.add(key));
        return List.copyOf(result);
    }

    private static @NotNull List<IntSupplier> createKeys(Builder builder) {
        ArrayList<IntSupplier> result = new ArrayList<>();
        builder.getKeys().forEach((key, value) -> result.add(value));
        return List.copyOf(result);
    }
}
