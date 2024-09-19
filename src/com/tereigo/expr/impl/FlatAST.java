package com.tereigo.expr.impl;

import java.util.List;

public class FlatAST {
    private final Expr[] nodes;
    private final byte[] numChildren;
    private final short[] startChild;

    public FlatAST(final List<Expr> tempNodes,
                   final List<Integer> tempNumChildren,
                   final List<Integer> tempStartChild) {
        nodes = new Expr[tempNodes.size()];
        for (int i = 0; i < tempNodes.size(); i++) {
            nodes[i] = tempNodes.get(i);
        }
        numChildren = new byte[tempNumChildren.size()];
        for (int i = 0; i < tempNumChildren.size(); i++) {
            numChildren[i] = tempNumChildren.get(i).byteValue();
        }
        startChild = new short[tempStartChild.size()];
        for (int i = 0; i < tempStartChild.size(); i++) {
            startChild[i] = tempStartChild.get(i).byteValue();
        }
    }

    public Expr[] getNodes() {
        return nodes;
    }

    public byte[] getNumChildren() {
        return numChildren;
    }

    public short[] getStartChild() {
        return startChild;
    }
}
