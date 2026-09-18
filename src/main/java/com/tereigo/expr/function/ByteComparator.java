package com.tereigo.expr.function;

@FunctionalInterface
public interface ByteComparator {
    int compare(byte lhs, byte rhs);
}
