package com.tereigo.expr.algo.customization;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.ExprEvaluatorWithContext;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.FalconExprContextBuilder;
import com.tereigo.expr.domains.FalconExprDomains;
import com.tereigo.expr.domains.algo.AlgoDataProvider;
import com.tereigo.expr.domains.falcon.FalconDataProvider;
import com.tereigo.expr.domains.order.OrderDomain;
import com.tereigo.expr.domains.order.OrderExprContextCreator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class CustomizationEngine {
    private final ExprContext nodeContext;

    private final OrderFieldSupplierWrapper orderFieldResolver;
    private final ExprContext ruleContext;

    private final List<RuleRecord> rules = new ArrayList<>();
    private final CustomizationReportHandler reportHandler;
    private final StringBuilder appliedRules = new StringBuilder(256);
    private final CustomizationErrorHandler errorHandler;

    public CustomizationEngine(final FalconDataProvider falcon,
                               final AlgoDataProvider algo,
                               final OrderExprContextCreator additionalOrderExprCtxCreator,
                               final CustomizationReportHandler reportHandler,
                               final CustomizationErrorHandler errorHandler) {

        // initialize the required Expression domains
        OrderDomain.init(falcon.getRefData());

        nodeContext = FalconExprDomains.falconAlgo(falcon, algo).getAsExprContext();
        orderFieldResolver = new OrderFieldSupplierWrapper();
        final MutableExprContext mutableRuleContext = FalconExprContextBuilder.start().falcon(falcon).algo(algo).orderWithShortcuts(orderFieldResolver).build();
        additionalOrderExprCtxCreator.enrich(orderFieldResolver, mutableRuleContext);
        ruleContext = mutableRuleContext.getAsExprContext();
        this.reportHandler = reportHandler;
        this.errorHandler = errorHandler;
    }

    public void onAddRuleMsg(final AddRuleMsg addRuleMsg) {
        try {
            final ExprEvaluatorWithContext nodeEvaluator = ExprEvaluatorFactory.create(addRuleMsg.nodePredicate);
            final boolean isApplicable = nodeEvaluator.evaluateBool(nodeContext);
            if (isApplicable) {
                rules.add(new RuleRecord(addRuleMsg.name, ExprEvaluatorFactory.create(addRuleMsg.rulePredicate), addRuleMsg.enabled, addRuleMsg.action));
            }
        } catch (final RuntimeException ex) {
            errorHandler.onNodeError(addRuleMsg.name, ex.getMessage());
        }
    }

    public void onNewOrder(final OrderFieldSupplier order) {
        appliedRules.setLength(0);
        orderFieldResolver.setOrder(order);
        for (int i = 0; i < rules.size(); i++) {
            final RuleRecord rule = rules.get(i);
            if (rule.enabled) {
                try {
                    final boolean result = rule.evaluator.evaluateBool(ruleContext);
                    if (result) {
                        final long actionResult = rule.action.apply(null, order);
                        // TODO: call this function
                        //reportHandler.onAppliedRule(order, rule.name, rule.action, actionResult);
                        appendAppliedRule(rule);
                    }
                }
                catch (final RuntimeException ex) {
                    errorHandler.onOrderError(rule.name, order, ex.getMessage());
                }
            }
        }
        reportHandler.onOrderReport(order, appliedRules);
    }

    private void appendAppliedRule(final RuleRecord rule) {
        if (appliedRules.length() > 0) {
            appliedRules.append(',');
        }
        appliedRules.append(rule.name);
    }

    // TODO: @VisibleForTesting
    int getRulesCount() {
        return rules.size();
    }

    private static final class RuleRecord {
        final String name;
        final ExprEvaluatorWithContext evaluator;
        final boolean enabled;
        final CustomizationAction action;

        private RuleRecord(final String name, final ExprEvaluatorWithContext evaluator, final boolean enabled, final CustomizationAction action) {
            this.name = name;
            this.evaluator = evaluator;
            this.enabled = enabled;
            this.action = action;
        }
    }
}
