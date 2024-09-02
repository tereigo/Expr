package com.tereigo.expr.algo.customization;

import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.MutableExprContext;
import com.tereigo.expr.falcon.AlgoExprContext;
import com.tereigo.expr.falcon.AlgoExprContextEnricher;
import com.tereigo.expr.falcon.FalconExprContext;
import com.tereigo.expr.falcon.FalconExprContextEnricher;
import com.tereigo.expr.falcon.Order;
import com.tereigo.expr.falcon.OrderExprContext;
import com.tereigo.expr.falcon.OrderFieldResolver;
import com.tereigo.expr.falcon.utils.ReferenceDataCache;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class CustomizationEngine {
    private final MutableExprContext nodeContext = ExprContextFactory.create();

    private OrderFieldResolver orderFieldResolver;
    private final MutableExprContext ruleContext = ExprContextFactory.create();

    private final List<RuleRecord> rules = new ArrayList<>();
    private CustomizationErrorHandler errorHandler;

    public void init(ReferenceDataCache refData,
                     FalconExprContextEnricher falconEnricher,
                     AlgoExprContextEnricher algoEnricher,
                     CustomizationErrorHandler errorHandler) {

        initNodeContext(falconEnricher, algoEnricher);
        initRuleContext(refData, falconEnricher, algoEnricher);
        this.errorHandler = errorHandler;
    }

    public void onAddRuleMsg(AddRuleMsg addRuleMsg) {
        try {
            final ExprEvaluator nodeEvaluator = new ExprEvaluator(addRuleMsg.nodePredicate);
            boolean result = nodeEvaluator.evaluateBool(nodeContext);
            if (result) {
                rules.add(new RuleRecord(new ExprEvaluator(addRuleMsg.rulePredicate), addRuleMsg.enabled, addRuleMsg.action));
            }
        } catch (RuntimeException ex) {
            errorHandler.onError("NodePredicate", ex.getMessage());
        }
    }

    public void onNewOrder(Order order) {
        orderFieldResolver.setOrder(order);
        for (int i = 0; i < rules.size(); i++) {
            final RuleRecord rule = rules.get(i);
            if (rule.enabled) {
                try {
                    boolean result = rule.evaluator.evaluateBool(ruleContext);
                    if (result) {
                        rule.action.apply(order);
                    }
                }
                catch (RuntimeException ex) {
                    errorHandler.onError("RulePredicate", ex.getMessage());
                }
            }
        }
    }

    int getRulesCount() {
        return rules.size();
    }

    private void initNodeContext(FalconExprContextEnricher falconEnricher, AlgoExprContextEnricher algoEnricher) {
        nodeContext.enrich(falconEnricher, algoEnricher);
        nodeContext.defineExprContext("falcon", FalconExprContext::get);
        nodeContext.defineExprContext("algo", AlgoExprContext::get);
    }

    private void initRuleContext(ReferenceDataCache refData, FalconExprContextEnricher falconEnricher, AlgoExprContextEnricher algoEnricher) {
        orderFieldResolver = new OrderFieldResolver(refData);

        ruleContext.defineExprContext("falcon", FalconExprContext::get);
        ruleContext.defineExprContext("algo", AlgoExprContext::get);
        final OrderExprContext orderExprContext = new OrderExprContext(orderFieldResolver);
        ruleContext.defineExprContext("order", () -> orderExprContext);

        ruleContext.enrich(falconEnricher, algoEnricher);
        registerOrderFunctions(ruleContext);
    }

    public void registerOrderFunctions(MutableExprContext ctx) {
        ctx.defineLong("productId", orderFieldResolver::productId);
        ctx.defineByteBuffer("ric", orderFieldResolver::ric);
        ctx.defineByteBuffer("tuid", orderFieldResolver::tuid);
    }

    private static final class RuleRecord {
        final ExprEvaluator evaluator;
        final boolean enabled;
        final CustomizationAction action;

        private RuleRecord(ExprEvaluator evaluator, boolean enabled, CustomizationAction action) {
            this.evaluator = evaluator;
            this.enabled = enabled;
            this.action = action;
        }
    }
}
