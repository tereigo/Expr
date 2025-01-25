package com.tereigo.expr.metrics.producer.impl;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.SimpleMetric;
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
import com.tereigo.expr.metrics.producer.MetricFactory;

import java.util.ArrayList;
import java.util.List;

public class DelayedFactory implements MetricFactory {
    private final MetricFactoryImpl factory;
    private final List<SimpleRecord> simpleRecords = new ArrayList<>();
    private final List<DedupBoolRecord> dedupBoolRecords = new ArrayList<>();
    private final List<DedupLongRecord> dedupLongRecords = new ArrayList<>();
    private boolean ready = false;

    public DelayedFactory(MetricFactoryImpl factory) {
        this.factory = factory;
    }

    @Override
    public SimpleBoolMetric createBool(String name, boolean autoPublish) {
        if (ready) {
            return factory.createBool(name, autoPublish);
        } else {
            BoolMetricProxy proxy = new BoolMetricProxy(new BoolMetricImpl(name));
            simpleRecords.add(new SimpleRecord(name, autoPublish, proxy));
            return proxy;
        }
    }

    @Override
    public DedupBoolMetric createBoolDedup(String name, boolean autoPublish) {
        if (ready) {
            return factory.createBoolDedup(name, autoPublish);
        } else {
            DedupBoolMetricProxy proxy = new DedupBoolMetricProxy(new DedupDummyBoolMetric(new BoolMetricImpl(name)));
            dedupBoolRecords.add(new DedupBoolRecord(name, autoPublish, proxy));
            return proxy;
        }
    }

    @Override
    public SimpleLongMetric createLong(String name, boolean autoPublish) {
        if (ready) {
            return factory.createLong(name, autoPublish);
        } else {
            LongMetricProxy proxy = new LongMetricProxy(new LongMetricImpl(name));
            simpleRecords.add(new SimpleRecord(name, autoPublish, proxy));
            return proxy;
        }
    }

    @Override
    public DedupLongMetric createLongDedup(String name, boolean autoPublish) {
        if (ready) {
            return factory.createLongDedup(name, autoPublish);
        } else {
            DedupLongMetricProxy proxy = new DedupLongMetricProxy(new DedupDummyLongMetric(new LongMetricImpl(name)));
            dedupLongRecords.add(new DedupLongRecord(name, autoPublish, proxy));
            return proxy;
        }
    }

    public void onReady() {
        ready = true;
        for (SimpleRecord rec : simpleRecords) {
            switch (rec.proxy) {
                case BoolMetricProxy boolProxy -> boolProxy.reset(factory.createBool(rec.name, rec.autoPublish()));
                case LongMetricProxy longProxy -> longProxy.reset(factory.createLong(rec.name, rec.autoPublish()));
                default -> throw new IllegalArgumentException("Unknown proxy type: " + rec.proxy.getClass());
            }
        }
        for (DedupBoolRecord rec : dedupBoolRecords) {
            DedupBoolMetric realMetric = factory.createBoolDedup(rec.name, rec.autoPublish());
            rec.proxy.reset(realMetric);
        }
        for (DedupLongRecord rec : dedupLongRecords) {
            DedupLongMetric realMetric = factory.createLongDedup(rec.name, rec.autoPublish());
            rec.proxy.reset(realMetric);
        }
    }

    record SimpleRecord(String name, boolean autoPublish, SimpleMetric proxy) { }

    record DedupBoolRecord(String name, boolean autoPublish, DedupBoolMetricProxy proxy) { }
    record DedupLongRecord(String name, boolean autoPublish, DedupLongMetricProxy proxy) { }
}
