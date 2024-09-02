package com.tereigo.expr.domains.order;

import com.tereigo.expr.domains.falcon.FalconUtils;
import com.tereigo.expr.falcon.msg.EqOrderInstMsgRo;
import com.tereigo.expr.utils.ByteBufferUtils;
import com.tereigo.expr.utils.OrderPrice;
import com.tereigo.expr.utils.PriceUtils;

import java.nio.ByteBuffer;

public class OrderFieldResolverImpl implements OrderFieldResolver {
    private EqOrderInstMsgRo order;
    private final ByteBuffer ricHolder = ByteBuffer.allocate(64);
    private ByteBuffer tuid;

    public void setOrder(final EqOrderInstMsgRo order) {
        this.order = order;
        ByteBufferUtils.setToEmpty(this.ricHolder);
        this.tuid = null;
    }

    public EqOrderInstMsgRo getOrder() {
        return order;
    }

    @Override
    public ByteBuffer ric() {
        if (ByteBufferUtils.isEmpty(ricHolder)) {
            return FalconUtils.get().getRicByProductId(order.getEquityProductId(), ricHolder);
        }
        return ricHolder;
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
    public ByteBuffer tuid() {
        if (tuid == null) {
            tuid = FalconUtils.get().getTuidByClientId(order.getClientId());
        }
        return tuid;
    }

    @Override
    public int quantity() {
        return order.getQuantity();
    }

    @Override
    public double price() {
        if (OrderPrice.isLimit(order.getLimitPrice())) {
            return PriceUtils.ltod(order.getLimitPrice());
        }
        return order.getLimitPrice();
    }
}
