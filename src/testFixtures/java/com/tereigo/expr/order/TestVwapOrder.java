package com.tereigo.expr.order;

import com.tereigo.expr.falcon.msg.VwapOrderInstMsgRo;

public class TestVwapOrder implements VwapOrderInstMsgRo {
    private long productId;
    private long clientId;
    private int qty;
    private long price;
    private double volumeLimit;

    private TestVwapOrder() {
    }

    public static TestVwapOrder create() {
        return new TestVwapOrder();
    }

    public TestVwapOrder withProductId(final long productId) {
        this.productId = productId;
        return this;
    }

    public TestVwapOrder withClientId(final long clientId) {
        this.clientId = clientId;
        return this;
    }

    public TestVwapOrder withQty(final int qty) {
        this.qty = qty;
        return this;
    }

    public TestVwapOrder withPrice(final long price) {
        this.price = price;
        return this;
    }

    public TestVwapOrder withVolumeLimit(final double volumeLimit) {
        this.volumeLimit = volumeLimit;
        return this;
    }

    @Override
    public double getVolumeLimit() {
        return volumeLimit;
    }

    @Override
    public long getEquityProductId() {
        return productId;
    }

    @Override
    public long getClientId() {
        return clientId;
    }

    @Override
    public int getQuantity() {
        return qty;
    }

    @Override
    public long getLimitPrice() {
        return price;
    }
}
