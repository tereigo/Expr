package com.tereigo.expr.falcon.utils;

import java.nio.ByteBuffer;

public class ReferenceDataCacheImpl implements ReferenceDataCache {
    private final ArrayIntObjMap<ByteBuffer> tuids;
    private final ArrayIntObjMap<ByteBuffer> rics;

    public ReferenceDataCacheImpl() {
        tuids = new ArrayIntObjMap<>(1000);
        rics = new ArrayIntObjMap<>(1000);
    }

    public void addTuid(final int clientId, final ByteBuffer tuid) {
        tuids.put(clientId, tuid);
    }

    public void addRic(final long productId, final ByteBuffer ric) {
        rics.put((int) productId, ric);
    }

    @Override
    public ByteBuffer getTuidByClientId(final int clientId) {
        return tuids.get(clientId);
    }

    @Override
    public ByteBuffer getRicByProductId(final long productId) {
        return rics.get((int) productId);
    }
}
