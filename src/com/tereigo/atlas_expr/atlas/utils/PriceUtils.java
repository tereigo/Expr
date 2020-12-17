package com.tereigo.atlas_expr.atlas.utils;

public class PriceUtils {
    public static double ltod(long v) {
        return v / 1e6;
    }

    public static double dtol(double v) {
        return v * 1e6;
    }
}
