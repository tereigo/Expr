package com.tereigo.expr.algo.customization;

import com.tereigo.expr.domains.falcon.FalconUtils;
import com.tereigo.expr.domains.order.OrderFieldResolver;
import com.tereigo.expr.falcon.utils.OrderPrice;
import com.tereigo.expr.falcon.utils.PriceUtils;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;

public class OrderFieldSupplierWrapper implements OrderFieldResolver {
    private final ByteBuffer ricHolder = ByteBuffer.allocate(64);
    private OrderFieldSupplier order;
    private ByteBuffer tuid;

    public void setOrder(final OrderFieldSupplier order) {
        this.order = order;
        ByteBufferUtils.setToEmpty(this.ricHolder);
        this.tuid = null;
    }

    public OrderFieldSupplier getOrder() {
        return order;
    }

    @Override
    public ByteBuffer ric() {
        if (ByteBufferUtils.isEmpty(ricHolder)) {
            return FalconUtils.get().getRicByProductId(order.productId(), ricHolder);
        }
        return ricHolder;
    }

    @Override
    public long productId() {
        return order.productId();
    }

    @Override
    public long clientId() {
        return order.clientId();
    }

    @Override
    public ByteBuffer tuid() {
        if (tuid == null) {
            tuid = FalconUtils.get().getTuidByClientId(order.clientId());
        }
        return tuid;
    }

    @Override
    public int quantity() {
        return order.quantity();
    }

    @Override
    public double price() {
        if (OrderPrice.isLimit(order.price())) {
            return PriceUtils.ltod(order.price());
        }
        return order.price();
    }
}
