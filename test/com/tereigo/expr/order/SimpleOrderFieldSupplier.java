package com.tereigo.expr.order;

import java.nio.ByteBuffer;

public class SimpleOrderFieldSupplier {
    private TestOrder order;

    public void setOrder(TestOrder order) {
        this.order = order;
    }

    public String ric() {
        return order.ric();
    }

    public long productId() {
        return order.productId();
    }

    public ByteBuffer tuid() {
        return order.tuid();
    }

    public boolean enabled() {
        return order.enabled();
    }
}
