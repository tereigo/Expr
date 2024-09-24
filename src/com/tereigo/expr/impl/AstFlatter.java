package com.tereigo.expr.impl;

import com.tereigo.expr.variant.MutableVariant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Experimental: we use flat array-like structure for nodes (see FlatAST)
 */
final class AstFlatter implements Expr.Visitor<FlatExpr> {
    private List<FlatExpr.BaseExpr> nodes = new ArrayList<>();
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
        final FlatExpr.Binary newNode = new FlatExpr.Binary(expr.operator, nodeIndex, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.left);
        queue.add(expr.right);

        return newNode;
    }

    @Override
    public FlatExpr visitInOperator(final Expr.InOperator expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.InOperator newNode = new FlatExpr.InOperator(expr.operator, nodeIndex, expr.values.size() + 1, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.operand);
        enqueueArgs(expr.values);

        return newNode;
    }

    @Override
    public FlatExpr visitWithinOperator(final Expr.WithinOperator expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.WithinOperator newNode = new FlatExpr.WithinOperator(expr.operator, nodeIndex, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.operand);
        queue.add(expr.min);
        queue.add(expr.max);

        return newNode;
    }

    @Override
    public FlatExpr visitBetweenOperator(final Expr.BetweenOperator expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.BetweenOperator newNode = new FlatExpr.BetweenOperator(expr.operator, nodeIndex, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.operand);
        queue.add(expr.min);
        queue.add(expr.max);

        return newNode;
    }

    @Override
    public FlatExpr visitGroupingExpr(final Expr.Grouping expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.Grouping newNode = new FlatExpr.Grouping(nodeIndex, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.expression);

        return newNode;
    }

    @Override
    public FlatExpr visitLiteralExpr(final Expr.Literal expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.Literal newNode = new FlatExpr.Literal(nodeIndex, expr.result);
        nodes.add(newNode);

        return newNode;
    }

    @Override
    public FlatExpr visitLogicalExpr(final Expr.Logical expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.Logical newNode = new FlatExpr.Logical(expr.operator, nodeIndex, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.left);
        queue.add(expr.right);

        return newNode;
    }

    @Override
    public FlatExpr visitTernaryExpr(final Expr.Ternary expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.Ternary newNode = new FlatExpr.Ternary(expr.operator, nodeIndex, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.condition);
        queue.add(expr.trueExpr);
        queue.add(expr.falseExpr);

        return newNode;
    }

    @Override
    public FlatExpr visitUnaryExpr(final Expr.Unary expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.Unary newNode = new FlatExpr.Unary(expr.operator, nodeIndex, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.expression);

        return newNode;
    }

    @Override
    public FlatExpr visitIdentifierExpr(final Expr.Identifier expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.Identifier newNode = new FlatExpr.Identifier(expr.operator, nodeIndex);
        nodes.add(newNode);

        return newNode;
    }

    @Override
    public FlatExpr visitResolvedIdentifierExpr(final Expr.ResolvedIdentifier expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.ResolvedIdentifier newNode = new FlatExpr.ResolvedIdentifier(expr.operator, nodeIndex, expr.function);
        nodes.add(newNode);

        return newNode;
    }

    @Override
    public FlatExpr visitCallExpr(final Expr.Call expr) {
        final short nodeIndex = (short)nodes.size();
        final int startChild = expr.args.size() > 0 ? nodes.size() + queue.size() + 1 : -1;
        final FlatExpr.Call newNode = new FlatExpr.Call(expr.operator, nodeIndex, expr.args.size(), startChild);
        nodes.add(newNode);

        enqueueArgs(expr.args);

        return newNode;
    }

    @Override
    public FlatExpr visitResolvedCallExpr(final Expr.ResolvedCall expr) {
        final short nodeIndex = (short)nodes.size();
        final int startChild = expr.args.size() > 0 ? nodes.size() + queue.size() + 1 : -1;
        final FlatExpr.ResolvedCall newNode = new FlatExpr.ResolvedCall(expr.operator, nodeIndex, expr.args.size(), startChild, expr.function);
        nodes.add(newNode);

        enqueueArgs(expr.args);

        return newNode;
    }

    @Override
    public FlatExpr visitObjectCallExpr(final Expr.ObjectCall expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.ObjectCall newNode = new FlatExpr.ObjectCall(expr.operator, nodeIndex, expr.args.size() + 1, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.object);
        enqueueArgs(expr.args);

        return newNode;
    }

    @Override
    public FlatExpr visitResolvedObjectCallExpr(final Expr.ResolvedObjectCall expr) {
        final short nodeIndex = (short)nodes.size();
        final FlatExpr.ResolvedObjectCall newNode = new FlatExpr.ResolvedObjectCall(expr.operator, nodeIndex, expr.args.size() + 1, nodes.size() + queue.size() + 1, expr.function);
        nodes.add(newNode);

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
