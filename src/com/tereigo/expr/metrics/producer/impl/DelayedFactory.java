package com.tereigo.expr.metrics.producer.impl;

import com.tereigo.expr.metrics.metric.MetricProxy;
import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.metric.dedup.impl.DedupBoolMetricProxy;
import com.tereigo.expr.metrics.metric.dedup.impl.DedupDummyBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.impl.DedupDummyLongMetric;
import com.tereigo.expr.metrics.metric.dedup.impl.DedupLongMetricProxy;
import com.tereigo.expr.metrics.metric.impl.BoolMetricImpl;
import com.tereigo.expr.metrics.metric.impl.BoolMetricProxy;
import com.tereigo.expr.metrics.metric.impl.LongMetricImpl;
import com.tereigo.expr.metrics.metric.impl.LongMetricProxy;
import com.tereigo.expr.metrics.params.BoolParam;
import com.tereigo.expr.metrics.params.Param;
import com.tereigo.expr.metrics.producer.MetricFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

public class DelayedFactory implements MetricFactory {
    private final MetricFactoryImpl factory;
    private final List<MetricRecord> records = new ArrayList<>();
    private final List<ParamRecord> paramRecords = new ArrayList<>();
    private boolean ready = false;

    public DelayedFactory(MetricFactoryImpl factory) {
        this.factory = factory;
    }

    @Override
    public SimpleBoolMetric create(BoolParam param, boolean initialValue, Builder builder) {
        if (ready) {
            return factory.create(param, initialValue, builder);
        } else {
            BoolMetricProxy proxy = new BoolMetricProxy(new BoolMetricImpl(param.getName()));
            paramRecords.add(new ParamRecord(param, builder, proxy));
            return proxy;
        }
    }

    @Override
    public DedupBoolMetric createBoolDedup(String name, boolean autoPublish, Map<String, IntSupplier> keys) {
        if (ready) {
            return factory.createBoolDedup(name, autoPublish, keys);
        } else {
            DedupBoolMetricProxy proxy = new DedupBoolMetricProxy(new DedupDummyBoolMetric(new BoolMetricImpl(name)));
            records.add(new MetricRecord(name, autoPublish, Map.of(), keys, proxy));
            return proxy;
        }
    }

    @Override
    public SimpleLongMetric createLong(String name, boolean autoPublish, Map<String, IntSupplier> keys) {
        if (ready) {
            return factory.createLong(name, autoPublish, keys);
        } else {
            LongMetricProxy proxy = new LongMetricProxy(new LongMetricImpl(name));
            records.add(new MetricRecord(name, autoPublish, Map.of(), keys, proxy));
            return proxy;
        }
    }

    @Override
    public DedupLongMetric createLongDedup(String name, boolean autoPublish, Map<String, IntSupplier> keys) {
        if (ready) {
            return factory.createLongDedup(name, autoPublish, keys);
        } else {
            DedupLongMetricProxy proxy = new DedupLongMetricProxy(new DedupDummyLongMetric(new LongMetricImpl(name)));
            records.add(new MetricRecord(name, autoPublish, Map.of(), keys, proxy));
            return proxy;
        }
    }

    public void onReady() {
        ready = true;
        for (ParamRecord rec : paramRecords) {
            switch (rec.proxy) {
                // NOTICE: initial value should not matter because we initialized the dummy one with it
                // and we'll read the latest value of the dummy one to set the real metric
                case BoolMetricProxy boolProxy -> boolProxy.reset(factory.create((BoolParam)rec.param, false, rec.builder));
//                case LongMetricProxy longProxy -> longProxy.reset(factory.create((LongParam)rec.param, false, rec.builder));
//                case DedupBoolMetricProxy dedupBoolProxy -> dedupBoolProxy.reset(factory.createDedup((BoolParam)rec.param, false, rec.builder));
//                case DedupLongMetricProxy dedupLongProxy -> dedupLongProxy.reset(factory.createDedup((LongParam)rec.param, false, rec.builder));
                default -> throw new IllegalArgumentException("Unknown proxy type: " + rec.proxy.getClass());
            }
        }

        for (MetricRecord rec : records) {
            switch (rec.proxy) {
                case BoolMetricProxy boolProxy -> boolProxy.reset(factory.createBool(rec.name, rec.tags, rec.keys, rec.autoPublish));
                case LongMetricProxy longProxy -> longProxy.reset(factory.createLong(rec.name, rec.autoPublish, rec.keys));
                case DedupBoolMetricProxy dedupBoolProxy -> dedupBoolProxy.reset(factory.createBoolDedup(rec.name, rec.autoPublish, rec.keys));
                case DedupLongMetricProxy dedupLongProxy -> dedupLongProxy.reset(factory.createLongDedup(rec.name, rec.autoPublish, rec.keys));
                default -> throw new IllegalArgumentException("Unknown proxy type: " + rec.proxy.getClass());
            }
        }
     }

    record MetricRecord(String name, boolean autoPublish, Map<String, String> tags, Map<String, IntSupplier> keys, MetricProxy<?> proxy) { }
    record ParamRecord(Param param, Builder builder, MetricProxy<?> proxy) { }
}
