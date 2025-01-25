package com.tereigo.expr.dedup.metric;

public interface MutableLongMetric extends LongMetric {

    void set(final long value);

    default void increment(long count) {
        System.out.println("Incrementing metric: " + getName() + " by " + count);
        set(get() + count);
    }
}
