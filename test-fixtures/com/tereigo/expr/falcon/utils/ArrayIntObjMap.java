package com.tereigo.expr.falcon.utils;

public class ArrayIntObjMap<T> {
    private T[] values;

    public ArrayIntObjMap(int maxKey) {
        this.values = (T[])new Object[maxKey];
    }

    public void put(int key, T value) {
        if (key >= values.length) {
            T[] newValues = (T[])new Object[key];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
        values[key] = value;
    }

    public T get(int key) {
        return (key < values.length) ? values[key] : null;
    }
}
