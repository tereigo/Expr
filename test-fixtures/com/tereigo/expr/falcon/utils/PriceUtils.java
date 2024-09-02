package com.tereigo.expr.falcon.utils;

public final class PriceUtils {
    public static double ltod(long v) {
        return v / 1e6;
    }

    public static double dtol(double v) {
        return v * 1e6;
    }
}
