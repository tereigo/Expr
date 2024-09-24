package com.tereigo.expr.impl;

import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.VariantFactory;

import java.util.List;

public class FlatAST {
    private final FlatExpr[] nodes;
    private final byte[] numChildren;
    private final short[] startChild;
    private final MutableVariant[] results;

    public FlatAST(final List<FlatExpr> tempNodes,
                   final List<Integer> tempNumChildren,
                   final List<Integer> tempStartChild,
                   final List<MutableVariant> tempResults) {
        nodes = new FlatExpr[tempNodes.size()];
        for (int i = 0; i < tempNodes.size(); i++) {
            nodes[i] = tempNodes.get(i);
        }
        numChildren = new byte[tempNumChildren.size()];
        for (int i = 0; i < tempNumChildren.size(); i++) {
            numChildren[i] = tempNumChildren.get(i).byteValue();
        }
        startChild = new short[tempStartChild.size()];
        for (int i = 0; i < tempStartChild.size(); i++) {
            startChild[i] = tempStartChild.get(i).shortValue();
        }
        results = new MutableVariant[tempNodes.size()];
        for (int i = 0; i < tempNodes.size(); i++) {
            results[i] = VariantFactory.clone(tempResults.get(i));
        }
    }

    public FlatExpr[] getNodes() {
        return nodes;
    }

//    public FlatExpr getNode(final short pos) {
//        return nodes[pos];
//    }

    public MutableVariant getResult(final short pos) {
        return results[pos];
    }

    public byte[] getNumChildren() {
        return numChildren;
    }

    public short[] getStartChild() {
        return startChild;
    }
}
