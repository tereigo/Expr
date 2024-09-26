package com.tereigo.expr.impl;

import java.util.List;

public class FlatAST {
    private final FlatExpr.BaseExpr[] nodes;

    public FlatAST(final List<FlatExpr.BaseExpr> tempNodes) {
        nodes = new FlatExpr.BaseExpr[tempNodes.size()];
        for (int i = 0; i < tempNodes.size(); i++) {
            nodes[i] = tempNodes.get(i);
        }
    }

    public FlatExpr[] getNodes() {
        return nodes;
    }
}
