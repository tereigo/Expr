package com.tereigo.atlas_expr.atlas;

public class Order {
    private final long productId;
    private final int clientId;
    private String actions = "";

    public Order(long productId, int clientId) {
        this.productId = productId;
        this.clientId = clientId;
    }

    public long productId() {
        return productId;
    }

    public int clientId() {
        return clientId;
    }

    public String actions() {
        return actions;
    }

    public void apply(CustomizationAction action) {
        if (!actions.isEmpty()) {
            actions += ',';
        }
        actions += action;
    }
}
