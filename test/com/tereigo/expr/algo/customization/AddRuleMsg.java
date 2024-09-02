package com.tereigo.expr.algo.customization;

public class AddRuleMsg {
    final public String nodePredicate;
    final public String rulePredicate;
    final public boolean enabled;
    final public CustomizationAction action;

    public AddRuleMsg(String nodePredicate, String rulePredicate, boolean enabled, CustomizationAction action) {
        this.nodePredicate = nodePredicate;
        this.rulePredicate = rulePredicate;
        this.enabled = enabled;
        this.action = action;
    }
}
