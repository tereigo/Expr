package com.tereigo.atlas_expr.algo.customization;

import com.google.common.collect.Lists;
import com.tereigo.atlas_expr.ExprContextFactory;
import com.tereigo.atlas_expr.ExprEvaluator;
import com.tereigo.atlas_expr.MutableExprContext;
import com.tereigo.atlas_expr.atlas.AddRuleMsg;
import com.tereigo.atlas_expr.atlas.AlgoExprContextEnricher;
import com.tereigo.atlas_expr.atlas.AtlasExprContextEnricher;
import com.tereigo.atlas_expr.atlas.CustomizationAction;
import com.tereigo.atlas_expr.atlas.Order;
import com.tereigo.atlas_expr.atlas.OrderFieldResolver;
import com.tereigo.atlas_expr.atlas.utils.ReferenceDataCache;

import java.util.List;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class CustomizationEngine {
    private final MutableExprContext algoExprContext = ExprContextFactory.create();

    private OrderFieldResolver orderFieldResolver;
    private final MutableExprContext orderExprContext = ExprContextFactory.create();

    private final List<RuleRecord> rules = Lists.newArrayList();
    private CustomizationErrorHandler errorHandler;

    public void init(ReferenceDataCache refData,
                     AtlasExprContextEnricher atlasEnricher,
                     AlgoExprContextEnricher algoEnricher,
                     CustomizationErrorHandler errorHandler) {

        algoExprContext.enrich(atlasEnricher, algoEnricher);

        orderFieldResolver = new OrderFieldResolver(refData);
        orderExprContext.enrich(atlasEnricher, algoEnricher);
        registerOrderFunctions(orderExprContext);

        this.errorHandler = errorHandler;
    }

    public void registerOrderFunctions(MutableExprContext ctx) {
        ctx.defineLong("$productId", orderFieldResolver::productId);
        ctx.defineByteBuffer("$ric", orderFieldResolver::ric);
        ctx.defineByteBuffer("$tuid", orderFieldResolver::tuid);
    }

    public void onAddRuleMsg(AddRuleMsg addRuleMsg) {
        try {
            final ExprEvaluator nodeEvaluator = new ExprEvaluator(addRuleMsg.nodePredicate);
            boolean result = nodeEvaluator.evaluateBool(algoExprContext);
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
                    boolean result = rule.evaluator.evaluateBool(orderExprContext);
                    if (result) {
                        order.apply(rule.action);
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
