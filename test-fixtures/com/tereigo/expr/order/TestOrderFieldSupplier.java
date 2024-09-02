package com.tereigo.expr.order;

import com.tereigo.expr.algo.customization.OrderFieldSupplier;

public class TestOrderFieldSupplier implements OrderFieldSupplier {
    private final long productId;
    private final long clientId;
    private final int qty;
    private final long price;
    private String actions = "";

    public TestOrderFieldSupplier() {
        this(0, 0, 0, 0);
    }

    public TestOrderFieldSupplier(long productId, long clientId) {
        this(productId, clientId, 0, 0);
    }

    public TestOrderFieldSupplier(long productId, long clientId, int qty, long price) {
        this.productId = productId;
        this.clientId = clientId;
        this.qty = qty;
        this.price = price;
    }

    @Override
    public long productId() {
        return productId;
    }

    @Override
    public long clientId() {
        return clientId;
    }

    @Override
    public int quantity() {
        return qty;
    }

    @Override
    public long price() {
        return price;
    }

    public String actions() {
        return actions;
    }

    public void applyAction(String action) {
        if (!actions.isEmpty()) {
            actions += ',';
        }
        actions += action;
    }
}

