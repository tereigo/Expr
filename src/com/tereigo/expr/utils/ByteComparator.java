package com.tereigo.expr.utils;

@FunctionalInterface
public interface ByteComparator {
    int compare(byte lhs, byte rhs);
}
