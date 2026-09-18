package com.tereigo.expr.function;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ByteBufferSupplier {

    ByteBuffer getAsByteBuffer();

}
