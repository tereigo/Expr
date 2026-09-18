package com.tereigo.expr.algo.customization;

public interface CustomizationReportHandler {

    void onAppliedRule(final OrderFieldSupplier order,
                       final String ruleName,
                       final CustomizationAction action,
                       final long ruleActionResult);

    void onOrderReport(final OrderFieldSupplier order, final CharSequence appliedRules);
}
