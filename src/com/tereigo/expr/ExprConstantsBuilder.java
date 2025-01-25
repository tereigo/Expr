package com.tereigo.expr;

import java.util.Map;

public interface ExprConstantsBuilder {

    ExprConstantsBuilder addBool(final String name, final boolean value);

    ExprConstantsBuilder addLong(final String name, final long value);

    ExprConstantsBuilder addDouble(final String name, final double value);

    ExprConstantsBuilder addString(final String name, final String value);

    Map<String, ?> build();
}
