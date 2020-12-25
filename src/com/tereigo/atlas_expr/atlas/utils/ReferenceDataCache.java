package com.tereigo.atlas_expr.atlas.utils;

import java.nio.ByteBuffer;

public interface ReferenceDataCache {
    ByteBuffer getTuidByClientId(int clientId);
    ByteBuffer getRicByProductId(long productId);
}
