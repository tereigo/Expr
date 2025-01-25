package com.tereigo.expr.dedup.producer;

import com.tereigo.expr.dedup.metric.DedupLongMetric;
import com.tereigo.expr.dedup.metric.MutableLongMetric;

public interface Factory {

    MutableLongMetric create(String name, boolean autoPublish);

    DedupLongMetric createDedup(String name, boolean autoPublish);
}
