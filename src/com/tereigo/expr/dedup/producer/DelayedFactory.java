package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.LongMetricImpl;
import com.tereigo.expr.dedup.metric.LongMetricProxy;
import com.tereigo.expr.dedup.metric.MutableLongMetric;

import java.util.ArrayList;
import java.util.List;

public class DelayedFactory implements Factory {
    private final FactoryImpl factory;
    private final List<LongRecord> longRecords = new ArrayList<>();
    private boolean ready = false;

    public DelayedFactory(FactoryImpl factory) {
        this.factory = factory;
    }

    @Override
    public MutableLongMetric create(String name, boolean autoPublish) {
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
            DedupLongMetric proxy = new DedupLongMetric(new LongMetricImpl(name));
            longRecords.add(new LongRecord(name, autoPublish, proxy));
            return proxy;
        }
    }

    public void onReady() {
        ready = true;
        for (LongRecord rec : longRecords) {
            if (rec.proxy instanceof DedupLongMetric) {
                DedupLongMetric realMetric = factory.createDedup(rec.name, rec.autoPublish());
                rec.proxy.reset(realMetric);
            } else {
                MutableLongMetric realMetric = factory.create(rec.name, rec.autoPublish());
                rec.proxy.reset(realMetric);
            }
        }
    }

    record LongRecord(String name, boolean autoPublish, LongMetricProxy proxy) { }
}
