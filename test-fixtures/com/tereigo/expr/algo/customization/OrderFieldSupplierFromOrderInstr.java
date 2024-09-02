package com.tereigo.expr.algo.customization;

import com.tereigo.expr.falcon.msg.EqOrderInstMsgRo;

public class OrderFieldSupplierFromOrderInstr implements OrderFieldSupplier {
    private EqOrderInstMsgRo order;

    public void setOrder(final EqOrderInstMsgRo order) {
        this.order = order;
    }

    public EqOrderInstMsgRo getOrder() {
        return order;
    }

    @Override
    public long productId() {
        return order.getEquityProductId();
    }

    @Override
    public long clientId() {
        return order.getClientId();
    }

    @Override
    public int quantity() {
        return order.getQuantity();
    }

    @Override
    public long price() {
        return order.getLimitPrice();
    }
}
