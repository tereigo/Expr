package com.tereigo.atlas_expr.atlas.utils;

@FunctionalInterface
public interface ByteComparator {
    int compare(byte lhs, byte rhs);
}
