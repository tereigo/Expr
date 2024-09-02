package com.tereigo.expr.order;

import com.tereigo.expr.falcon.msg.EqOrderInstMsgRo;

public class SampleOrderInstruction implements EqOrderInstMsgRo {
    private final long productId;
    private final long clientId;
    private final int qty;
    private final long price;
    private String actions = "";

    public SampleOrderInstruction(long productId, long clientId) {
        this(productId, clientId, 0, 0);
    }

    public SampleOrderInstruction(long productId, long clientId, int qty, long price) {
        this.productId = productId;
        this.clientId = clientId;
        this.qty = qty;
        this.price = price;
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
