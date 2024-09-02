package com.tereigo.expr.utils;

public class PriceUtils {
    public static double ltod(long v) {
        return v / 1e6;
    }

    public static double dtol(double v) {
        return v * 1e6;
    }
}
