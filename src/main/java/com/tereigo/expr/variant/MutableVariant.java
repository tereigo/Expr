package com.tereigo.expr.variant;

import com.tereigo.expr.function.BooleanConsumer;
import com.tereigo.expr.function.ByteBufferConsumer;
import com.tereigo.expr.function.DoubleConsumer;
import com.tereigo.expr.function.ExprContextConsumer;
import com.tereigo.expr.function.LongConsumer;
import com.tereigo.expr.function.StringConsumer;

public interface MutableVariant extends Variant,
        LongConsumer, DoubleConsumer, BooleanConsumer, StringConsumer,
        ByteBufferConsumer, ExprContextConsumer {
}
