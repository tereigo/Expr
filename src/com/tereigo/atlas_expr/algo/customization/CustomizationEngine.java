package com.tereigo.atlas_expr.algo.customization;

import com.tereigo.atlas_expr.ExprContextFactory;
import com.tereigo.atlas_expr.ExprEvaluator;
import com.tereigo.atlas_expr.MutableExprContext;
import com.tereigo.atlas_expr.atlas.AddRuleMsg;
import com.tereigo.atlas_expr.atlas.AlgoExprContext;
import com.tereigo.atlas_expr.atlas.AlgoExprContextEnricher;
import com.tereigo.atlas_expr.atlas.AtlasExprContext;
import com.tereigo.atlas_expr.atlas.AtlasExprContextEnricher;
import com.tereigo.atlas_expr.atlas.CustomizationAction;
import com.tereigo.atlas_expr.atlas.Order;
import com.tereigo.atlas_expr.atlas.OrderExprContext;
import com.tereigo.atlas_expr.atlas.OrderFieldResolver;
import com.tereigo.atlas_expr.atlas.utils.ReferenceDataCache;

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
                     AtlasExprContextEnricher atlasEnricher,
                     AlgoExprContextEnricher algoEnricher,
                     CustomizationErrorHandler errorHandler) {

        initNodeContext(atlasEnricher, algoEnricher);
        initRuleContext(refData, atlasEnricher, algoEnricher);
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

    private void initNodeContext(AtlasExprContextEnricher atlasEnricher, AlgoExprContextEnricher algoEnricher) {
        nodeContext.enrich(atlasEnricher, algoEnricher);
        nodeContext.defineExprContext("atlas", AtlasExprContext::get);
        nodeContext.defineExprContext("algo", AlgoExprContext::get);
    }

    private void initRuleContext(ReferenceDataCache refData, AtlasExprContextEnricher atlasEnricher, AlgoExprContextEnricher algoEnricher) {
        orderFieldResolver = new OrderFieldResolver(refData);

        ruleContext.defineExprContext("atlas", AtlasExprContext::get);
        ruleContext.defineExprContext("algo", AlgoExprContext::get);
        final OrderExprContext orderExprContext = new OrderExprContext(orderFieldResolver);
        ruleContext.defineExprContext("order", () -> orderExprContext);

        ruleContext.enrich(atlasEnricher, algoEnricher);
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
