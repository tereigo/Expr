package com.tereigo.expr.falcon.utils;

public class ArrayIntObjMap<T> {
    private T[] values;

    public ArrayIntObjMap(final int maxKey) {
        this.values = (T[]) new Object[maxKey];
    }

    public void put(final int key, final T value) {
        if (key >= values.length) {
            final T[] newValues = (T[]) new Object[key];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
        values[key] = value;
    }

    public T get(final int key) {
        return (key < values.length) ? values[key] : null;
    }
}
