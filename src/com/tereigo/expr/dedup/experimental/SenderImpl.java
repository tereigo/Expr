package com.tereigo.expr.dedup.experimental;


import com.tereigo.expr.dedup.metric.LongMetric;

public class SenderImpl implements Sender {

    @Override
    public void send(LongMetric metric) {
        if (metric instanceof DedupLongMetric2 dedup) {
            sendIfChanged(this, dedup);
        } else if (metric instanceof LongMetricProxy proxy) {
            send(proxy.getDelegate());
        } else {
            sendImpl(metric);
        }
    }

    public static void sendIfChanged(SenderImpl sender, DedupLongMetric2 metric) {
        if (metric.needsPublishing()) {
            sender.sendImpl(metric);
            metric.onPublished();
        }
    }

    public static void sendIfChanged(SenderImpl sender, DedupMetric<LongMetric> metric) {
        if (metric.needsPublishing()) {
            sender.sendImpl(metric.getMetric());
            metric.onPublished();
        }
    }

    @Override
    public void send(DedupLongMetric2 metric) {
        sendIfChanged(this, metric);
    }

    @Override
    public void send(LongMetricProxy proxy) {
        send(proxy.getDelegate());
    }

    @Override
    public void send(DedupMetric<LongMetric> metric) {
        sendIfChanged(this, metric);
    }

    void sendImpl(LongMetric metric) {
        System.out.println("Sending metric: " + metric.getName() + "=" + metric.get());
    }
}
