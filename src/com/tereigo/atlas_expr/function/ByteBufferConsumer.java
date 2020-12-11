package com.tereigo.atlas_expr.function;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ByteBufferConsumer {

    void accept(ByteBuffer value);
}