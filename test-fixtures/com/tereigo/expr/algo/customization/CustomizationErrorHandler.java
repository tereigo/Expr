package com.tereigo.expr.algo.customization;

public interface CustomizationErrorHandler {

    void onNodeError(final String ruleName, final String msg);

    void onOrderError(final String ruleName, final OrderFieldSupplier order, final String msg);

}
