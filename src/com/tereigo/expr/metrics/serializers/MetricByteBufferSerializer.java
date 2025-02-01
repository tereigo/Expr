package com.tereigo.expr.metrics.serializers;

import com.tereigo.expr.metrics.metric.BoolMetric;
import com.tereigo.expr.metrics.metric.LongMetric;
import com.tereigo.expr.metrics.metric.Metric;
import com.tereigo.expr.metrics.metric.MetricRegistration;
import com.tereigo.expr.metrics.producer.MetricSerializer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.IntSupplier;

public class MetricByteBufferSerializer implements MetricSerializer<ByteBuffer> {
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    @Override
    public ByteBuffer serialize(int nodeId, MetricRegistration registration) {
        // TODO: Implement this method
        return buffer;
    }

    @Override
    public ByteBuffer serialize(int nodeId, BoolMetric metric) {
        writeCommon(nodeId, MetricType.BOOL, metric);
        buffer.put(metric.get() ? (byte)1 : (byte)0);
        return buffer;
    }

    @Override
    public ByteBuffer serialize(int nodeId, LongMetric metric) {
        writeCommon(nodeId, MetricType.LONG, metric);
        buffer.putLong(metric.get());
        return buffer;
    }

    private ByteBuffer writeCommon(int nodeId, MetricType type, Metric metric) {
        buffer.clear();
        buffer.putShort((short)nodeId);
        buffer.put((byte)type.ordinal());
        buffer.putInt(metric.getMetricId());
        List<IntSupplier> keys = metric.getKeys();
        buffer.put((byte)keys.size());
        for (int i = 0; i < keys.size(); i++) {
            buffer.putInt(keys.get(i).getAsInt());
        }
        return buffer;
    }
}
