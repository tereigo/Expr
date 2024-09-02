package com.tereigo.expr.falcon.msg;

public interface EqOrderInstMsgRo {

    long getEquityProductId();

    long getClientId();

    int getQuantity();

    long getLimitPrice();
}
