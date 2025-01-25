package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.*;

import java.util.ArrayList;
import java.util.List;

public class DelayedFactory implements MetricFactory {
    private final MetricFactoryImpl factory;
    private final List<LongRecord> longRecords = new ArrayList<>();
    private final List<DedupLongRecord> dedupLongRecords = new ArrayList<>();
    private boolean ready = false;

    public DelayedFactory(MetricFactoryImpl factory) {
        this.factory = factory;
    }

    @Override
    public SimpleLongMetric create(String name, boolean autoPublish) {
        if (ready) {
            return factory.create(name, autoPublish);
        } else {
            LongMetricProxy proxy = new LongMetricProxy(new LongMetricImpl(name));
            longRecords.add(new LongRecord(name, autoPublish, proxy));
            return proxy;
        }
    }

    @Override
    public DedupLongMetric createDedup(String name, boolean autoPublish) {
        if (ready) {
            return factory.createDedup(name, autoPublish);
        } else {
            DedupLongMetricProxy proxy = new DedupLongMetricProxy(new DedupDummyLongMetric(new LongMetricImpl(name)));
            dedupLongRecords.add(new DedupLongRecord(name, autoPublish, proxy));
            return proxy;
        }
    }

    public void onReady() {
        ready = true;
        for (LongRecord rec : longRecords) {
            SimpleLongMetric realMetric = factory.create(rec.name, rec.autoPublish());
            rec.proxy.reset(realMetric);
        }
        for (DedupLongRecord rec : dedupLongRecords) {
            DedupLongMetric realMetric = factory.createDedup(rec.name, rec.autoPublish());
            rec.proxy.reset(realMetric);
        }
    }

    record LongRecord(String name, boolean autoPublish, LongMetricProxy proxy) { }
    record DedupLongRecord(String name, boolean autoPublish, DedupLongMetricProxy proxy) { }
}
