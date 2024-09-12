package com.tereigo.expr.domains.order;

import com.tereigo.expr.domains.falcon.FalconUtils;
import com.tereigo.expr.falcon.msg.EqOrderInstMsgRo;
import com.tereigo.expr.falcon.utils.OrderPrice;
import com.tereigo.expr.falcon.utils.PriceUtils;
import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;

public class OrderFieldResolverImpl implements OrderFieldResolver {
    private final ByteBuffer ricHolder = ByteBuffer.allocate(64);
    private EqOrderInstMsgRo order;
    private ByteBuffer tuid;

    public EqOrderInstMsgRo getOrder() {
        return order;
    }

    public void setOrder(final EqOrderInstMsgRo order) {
        this.order = order;
        ByteBufferUtils.setToEmpty(this.ricHolder);
        this.tuid = null;
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
