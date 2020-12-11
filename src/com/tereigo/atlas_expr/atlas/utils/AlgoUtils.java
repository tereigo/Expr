package com.tereigo.atlas_expr.atlas.utils;

public class AlgoUtils {
    private static final double EPSILON = 1e-6;

    public static boolean epsilonEquals(double val1, double val2) {
        return Math.abs(val1 - val2) < EPSILON;
    }
}
