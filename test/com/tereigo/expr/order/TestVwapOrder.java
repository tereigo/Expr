package com.tereigo.expr.order;

import com.tereigo.expr.falcon.msg.VwapOrderInstMsgRo;

public class TestVwapOrder implements VwapOrderInstMsgRo {
    private long productId;
    private long clientId;
    private int qty;
    private long price;
    private double volumeLimit;

    private TestVwapOrder() {}

    public static TestVwapOrder create() {
        return new TestVwapOrder();
    }

    public TestVwapOrder withProductId(long productId) {
        this.productId = productId;
        return this;
    }

    public TestVwapOrder withClientId(long clientId) {
        this.clientId = clientId;
        return this;
    }

    public TestVwapOrder withQty(int qty) {
        this.qty = qty;
        return this;
    }

    public TestVwapOrder withPrice(long price) {
        this.price = price;
        return this;
    }

    public TestVwapOrder withVolumeLimit(double volumeLimit) {
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
