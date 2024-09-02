package com.tereigo.expr.function;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ByteBufferConsumer {

    void accept(ByteBuffer value);
}