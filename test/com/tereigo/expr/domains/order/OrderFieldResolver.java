package com.tereigo.expr.domains.order;

import java.nio.ByteBuffer;

public interface OrderFieldResolver {

    ByteBuffer ric();

    long productId();

    long clientId();

    ByteBuffer tuid();

    int quantity();

    double price();

}
