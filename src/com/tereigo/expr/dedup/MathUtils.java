package com.tereigo.expr.dedup;

public class MathUtils {
    public static long randomBetween(long min, long max) {
        return (long)(min + Math.random() * (max - min));
    }
}
