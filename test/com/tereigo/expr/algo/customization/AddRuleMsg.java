package com.tereigo.expr.algo.customization;

public class AddRuleMsg {
    final public String name;
    final public String nodePredicate;
    final public String rulePredicate;
    final public boolean enabled;
    final public CustomizationAction action;

    public AddRuleMsg(String name, String nodePredicate, String rulePredicate, boolean enabled, CustomizationAction action) {
        this.name = name;
        this.nodePredicate = nodePredicate;
        this.rulePredicate = rulePredicate;
        this.enabled = enabled;
        this.action = action;
    }

    //  we use "action" as "name" just to simplify test code
    public AddRuleMsg(String nodePredicate, String rulePredicate, boolean enabled, CustomizationAction action) {
        this(action.toString(), nodePredicate, rulePredicate, enabled, action);
    }
}

