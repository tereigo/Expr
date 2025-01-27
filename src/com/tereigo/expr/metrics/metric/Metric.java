package com.tereigo.expr.metrics.metric;

import java.util.List;
import java.util.function.IntSupplier;

public interface Metric {
    String getName();
    int getMetricId();

    default List<IntSupplier> getKeys() {
        return List.of();
    }
}
