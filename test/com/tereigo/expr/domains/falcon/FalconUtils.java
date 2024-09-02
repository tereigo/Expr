package com.tereigo.expr.domains.falcon;

import com.tereigo.expr.falcon.utils.ReferenceDataCache;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;

public class FalconUtils {
    private static FalconUtils INSTANCE;
    private final ReferenceDataCache refData;

    private FalconUtils(final ReferenceDataCache refData) {
        this.refData = refData;
    }

    public ByteBuffer getTuidByClientId(final long clientId) {
        final ByteBuffer tuid = refData.getTuidByClientId((int)clientId);
        return tuid != null ? tuid : ByteBufferUtils.EMPTY_BUFFER;
    }

    public ByteBuffer getRicByProductId(final long productId, final ByteBuffer ricHolder) {
        final ByteBuffer ric = refData.getRicByProductId(productId);
        if (ric == null) {
            ByteBufferUtils.setToEmpty(ricHolder);
            return ricHolder;
        }
        ricHolder.clear();
        ByteBufferUtils.deepCopy(ric, ricHolder);
        return ricHolder;
    }

    public static void init(final ReferenceDataCache refData) {
        INSTANCE = new FalconUtils(refData);
    }

    public static FalconUtils get() {
        return INSTANCE;
    }
}
