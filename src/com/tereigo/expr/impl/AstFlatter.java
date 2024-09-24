package com.tereigo.expr.impl;

import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.VariantFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Experimental: we use flat array-like structure for nodes (see FlatAST)
 */
final class AstFlatter implements Expr.Visitor<FlatExpr> {
    private List<FlatExpr> nodes = new ArrayList<>();
    private List<Token> tokens = new ArrayList<>();
    private List<Integer> numChildren = new ArrayList<>();
    private List<Integer> startChild = new ArrayList<>();
    private List<MutableVariant> results = new ArrayList<>();
    private final Queue<Expr> queue = new LinkedList<>();

    FlatAST flatten(final ASTRoot root) {

        nodes.clear();
        tokens.clear();
        numChildren.clear();
        startChild.clear();
        results.clear();
        queue.clear();

        queue.offer(root.expr());
        while (!queue.isEmpty()) {
            final Expr node = queue.poll();
            evaluate(node);
        }

        return new FlatAST(nodes, numChildren, startChild, results);
    }

    private FlatExpr evaluate(final Expr expr) {
        return expr.accept(this);
    }

    @Override
    public FlatExpr visitBinaryExpr(final Expr.Binary expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Binary(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(2);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.left);
        queue.add(expr.right);

        return newNode;
    }

    @Override
    public FlatExpr visitInOperator(final Expr.InOperator expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.InOperator(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(expr.values.size() + 1);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.operand);
        enqueueArgs(expr.values);
        return newNode;
    }

    @Override
    public FlatExpr visitWithinOperator(final Expr.WithinOperator expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.WithinOperator(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(3);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.operand);
        queue.add(expr.min);
        queue.add(expr.max);

        return newNode;
    }

    @Override
    public FlatExpr visitBetweenOperator(final Expr.BetweenOperator expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.BetweenOperator(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(3);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.operand);
        queue.add(expr.min);
        queue.add(expr.max);
        return newNode;
    }

    @Override
    public FlatExpr visitGroupingExpr(final Expr.Grouping expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Grouping(nodeIndex);
        nodes.add(newNode);
        tokens.add(null);
        numChildren.add(1);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.expression);

        return newNode;
    }

    @Override
    public FlatExpr visitLiteralExpr(final Expr.Literal expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Literal(nodeIndex);
        nodes.add(newNode);
        tokens.add(null);
        numChildren.add(0);
        startChild.add(-1);
        results.add(VariantFactory.clone(expr.result));

        return newNode;
    }

    @Override
    public FlatExpr visitLogicalExpr(final Expr.Logical expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Logical(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(2);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.left);
        queue.add(expr.right);

        return newNode;
    }

    @Override
    public FlatExpr visitTernaryExpr(final Expr.Ternary expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Ternary(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(3);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.condition);
        queue.add(expr.trueExpr);
        queue.add(expr.falseExpr);

        return newNode;
    }

    @Override
    public FlatExpr visitUnaryExpr(final Expr.Unary expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Unary(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(1);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.expression);

        return newNode;
    }

    @Override
    public FlatExpr visitIdentifierExpr(final Expr.Identifier expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Identifier(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(0);
        startChild.add(-1);
        results.add(VariantFactory.createEmpty());

        return newNode;
    }

    @Override
    public FlatExpr visitResolvedIdentifierExpr(final Expr.ResolvedIdentifier expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.ResolvedIdentifier(expr.operator, expr.function, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(0);
        startChild.add(-1);
        results.add(VariantFactory.createEmpty());

        return newNode;
    }

    @Override
    public FlatExpr visitCallExpr(final Expr.Call expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.Call(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(expr.args.size());
        if (expr.args.size() > 0) {
            startChild.add(nodes.size() + queue.size());
            enqueueArgs(expr.args);
        } else {
            startChild.add(-1);
        }
        results.add(VariantFactory.createEmpty());
        return newNode;
    }

    @Override
    public FlatExpr visitResolvedCallExpr(final Expr.ResolvedCall expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.ResolvedCall(expr.operator, expr.function, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(expr.args.size());
        if (expr.args.size() > 0) {
            startChild.add(nodes.size() + queue.size());
            enqueueArgs(expr.args);
        } else {
            startChild.add(-1);
        }
        results.add(VariantFactory.createEmpty());
        return newNode;
    }

    @Override
    public FlatExpr visitObjectCallExpr(final Expr.ObjectCall expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.ObjectCall(expr.operator, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(expr.args.size() + 1);
        startChild.add(nodes.size() + queue.size());

        queue.add(expr.object);
        enqueueArgs(expr.args);
        results.add(VariantFactory.createEmpty());
        return newNode;
    }

    @Override
    public FlatExpr visitResolvedObjectCallExpr(final Expr.ResolvedObjectCall expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr newNode = new FlatExpr.ResolvedObjectCall(expr.operator, expr.function, nodeIndex);
        nodes.add(newNode);
        tokens.add(expr.operator);
        numChildren.add(expr.args.size() + 1);
        startChild.add(nodes.size() + queue.size());
        results.add(VariantFactory.createEmpty());

        queue.add(expr.object);
        enqueueArgs(expr.args);

        return newNode;
    }

    private void enqueueArgs(final List<Expr> args) {
        for (int i = 0; i < args.size(); i++) {
            queue.add(args.get(i));
        }
    }
}
