package com.tereigo.expr.falcon;

import com.tereigo.expr.falcon.utils.ReferenceDataCache;

import java.nio.ByteBuffer;

public class OrderFieldResolver {
    private final ReferenceDataCache refData;
    private Order order;

    public OrderFieldResolver(ReferenceDataCache refData) {
        this.refData = refData;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ByteBuffer ric() {
        return refData.getRicByProductId(order.productId());
    }

    public long productId() {
        return order.productId();
    }

    public int clientId() {
        return order.clientId();
    }

    public ByteBuffer tuid() {
        return refData.getTuidByClientId(order.clientId());
    }
}
