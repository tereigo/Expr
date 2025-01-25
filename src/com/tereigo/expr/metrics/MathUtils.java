package com.tereigo.expr.metrics;

public class MathUtils {
    public static long randomBetween(long min, long max) {
        return (long)(min + Math.random() * (max - min));
    }
}
