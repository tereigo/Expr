package com.tereigo.atlas_expr.order;

import java.nio.ByteBuffer;

public class OrderFieldSupplier {
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    public String ric() {
        return order.ric();
    }

    public long productId() {
        return order.productId();
    }

    public boolean enabled() {
        return order.enabled();
    }

    public ByteBuffer tuid() {
        return order.tuid();
    }
}
