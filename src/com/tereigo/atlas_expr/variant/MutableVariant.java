package com.tereigo.atlas_expr.variant;

import com.tereigo.atlas_expr.function.BooleanConsumer;
import com.tereigo.atlas_expr.function.ByteBufferConsumer;
import com.tereigo.atlas_expr.function.ExprContextConsumer;
import com.tereigo.atlas_expr.function.ObjectConsumer;
import com.tereigo.atlas_expr.function.StringConsumer;

import java.util.function.DoubleConsumer;
import java.util.function.LongConsumer;

public interface MutableVariant extends Variant,
        LongConsumer, DoubleConsumer, BooleanConsumer, StringConsumer,
        ByteBufferConsumer, ExprContextConsumer, ObjectConsumer {
}
