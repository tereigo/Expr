package com.tereigo.expr.falcon.utils;

public final class PriceUtils {
    public static double ltod(final long v) {
        return v / 1e6;
    }

    public static long dtol(final double v) {
        return (long) (v * 1e6);
    }
}
