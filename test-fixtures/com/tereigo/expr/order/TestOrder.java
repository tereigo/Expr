package com.tereigo.expr.order;

import java.nio.ByteBuffer;

public class TestOrder {
    private final String ric;
    private final long productId;
    private final boolean enabled;
    private final ByteBuffer tuid;

    public TestOrder(String ric, long productId, boolean enabled, ByteBuffer tuid) {
        this.ric = ric;
        this.productId = productId;
        this.enabled = enabled;
        this.tuid = tuid;
    }

    public String ric() {
        return ric;
    }

    public long productId() {
        return productId;
    }

    public boolean enabled() {
        return enabled;
    }

    public ByteBuffer tuid() {
        return tuid;
    }
}
