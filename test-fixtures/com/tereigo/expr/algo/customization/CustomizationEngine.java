package com.tereigo.expr.algo.customization;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.domains.ExprContextBuilder;
import com.tereigo.expr.domains.ExprDomains;
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

        nodeContext = ExprDomains.falconAlgo(falcon, algo).getAsExprContext();
        orderFieldResolver = new OrderFieldSupplierWrapper();
        final MutableExprContext mutableRuleContext = ExprContextBuilder.start().falcon(falcon).algo(algo).orderWithShortcuts(orderFieldResolver).build();
        additionalOrderExprCtxCreator.enrich(orderFieldResolver, mutableRuleContext);
        ruleContext = mutableRuleContext.getAsExprContext();
        this.reportHandler = reportHandler;
        this.errorHandler = errorHandler;
    }

    public void onAddRuleMsg(AddRuleMsg addRuleMsg) {
        try {
            final ExprEvaluator nodeEvaluator = new ExprEvaluator(addRuleMsg.nodePredicate);
            boolean isApplicable = nodeEvaluator.evaluateBool(nodeContext);
            if (isApplicable) {
                rules.add(new RuleRecord(addRuleMsg.name, new ExprEvaluator(addRuleMsg.rulePredicate), addRuleMsg.enabled, addRuleMsg.action));
            }
        } catch (RuntimeException ex) {
            errorHandler.onNodeError(addRuleMsg.name, ex.getMessage());
        }
    }

    public void onNewOrder(OrderFieldSupplier order) {
        appliedRules.setLength(0);
        orderFieldResolver.setOrder(order);
        for (int i = 0; i < rules.size(); i++) {
            final RuleRecord rule = rules.get(i);
            if (rule.enabled) {
                try {
                    boolean result = rule.evaluator.evaluateBool(ruleContext);
                    if (result) {
                        final long actionResult = rule.action.apply(null, order);
                        // TODO: call this function
                        //reportHandler.onAppliedRule(order, rule.name, rule.action, actionResult);
                        appendAppliedRule(rule);
                    }
                }
                catch (RuntimeException ex) {
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
        final ExprEvaluator evaluator;
        final boolean enabled;
        final CustomizationAction action;

        private RuleRecord(String name, ExprEvaluator evaluator, boolean enabled, CustomizationAction action) {
            this.name = name;
            this.evaluator = evaluator;
            this.enabled = enabled;
            this.action = action;
        }
    }
}
