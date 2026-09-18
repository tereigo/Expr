package com.tereigo.expr.falcon.utils;

import java.nio.ByteBuffer;

public interface ReferenceDataCache {
    ByteBuffer getTuidByClientId(int clientId);

    ByteBuffer getRicByProductId(long productId);
}
