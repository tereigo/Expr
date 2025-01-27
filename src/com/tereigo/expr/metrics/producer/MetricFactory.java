package com.tereigo.expr.metrics.producer;

import com.tereigo.expr.metrics.metric.SimpleBoolMetric;
import com.tereigo.expr.metrics.metric.SimpleLongMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupBoolMetric;
import com.tereigo.expr.metrics.metric.dedup.DedupLongMetric;
import com.tereigo.expr.metrics.params.BoolParam;

import java.util.Map;
import java.util.function.IntSupplier;

public interface MetricFactory {

    SimpleBoolMetric create(BoolParam param, boolean initialValue, Builder builder);

    default SimpleBoolMetric create(BoolParam param, boolean initialValue) {
        return create(param, initialValue, DEFAULT);
    }

    default SimpleBoolMetric create(BoolParam param, Builder builder) {
        return create(param, false, builder);
    }

    default SimpleBoolMetric create(BoolParam param) {
        return create(param, false, DEFAULT);
    }

    DedupBoolMetric createBoolDedup(String name, boolean autoPublish, Map<String, IntSupplier> keys);

    default DedupBoolMetric createBoolDedup(String name, boolean autoPublish) {
        return createBoolDedup(name, autoPublish, Map.of());
    }

    SimpleLongMetric createLong(String name, boolean autoPublish, Map<String, IntSupplier> keys);

    default SimpleLongMetric createLong(String name, boolean autoPublish) {
        return createLong(name, autoPublish, Map.of());
    }

    DedupLongMetric createLongDedup(String name, boolean autoPublish, Map<String, IntSupplier> keys);

    default DedupLongMetric createLongDedup(String name, boolean autoPublish) {
        return createLongDedup(name, autoPublish, Map.of());
    }

    Builder DEFAULT = new Builder();

    static Builder builder() {
        return new Builder();
    }

    class Builder {
        private boolean autoPublish = true;
        private Map<String, String> tags = Map.of();
        private Map<String, IntSupplier> keys = Map.of();

        public Builder autoPublish(boolean autoPublish) {
            this.autoPublish = autoPublish;
            return this;
        }

        public Builder tags(Map<String, String> tags) {
            this.tags = Map.copyOf(tags);
            return this;
        }

        public Builder keys(Map<String, IntSupplier> keys) {
            this.keys = Map.copyOf(keys);
            return this;
        }

        public Map<String, IntSupplier> getKeys() {
            return keys;
        }

        public Map<String, String> getTags() {
            return tags;
        }

        public boolean isAutoPublish() {
            return autoPublish;
        }
    }
}
