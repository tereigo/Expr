package com.tereigo.expr.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Experimental: we use flat array-like structure for nodes (see FlatAST)
 */
final class AstFlatter implements Expr.Visitor<FlatExpr> {
    private List<FlatExpr.BaseExpr> nodes = new ArrayList<>();
    private final Queue<Expr> queue = new LinkedList<>();

    FlatAST flatten(final ASTRoot root) {

        nodes.clear();
        queue.clear();

        queue.offer(root.expr());
        while (!queue.isEmpty()) {
            final Expr node = queue.poll();
            evaluate(node);
        }

        return new FlatAST(nodes);
    }

    private FlatExpr evaluate(final Expr expr) {
        return expr.accept(this);
    }

    @Override
    public FlatExpr visitBinaryExpr(final Expr.Binary expr) {
        final FlatExpr.Binary newNode = new FlatExpr.Binary(expr.operator, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.left);
        queue.add(expr.right);

        return newNode;
    }

    @Override
    public FlatExpr visitInOperator(final Expr.InOperator expr) {
        final FlatExpr.InOperator newNode = new FlatExpr.InOperator(expr.operator, expr.values.size() + 1, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.operand);
        enqueueArgs(expr.values);

        return newNode;
    }

    @Override
    public FlatExpr visitWithinOperator(final Expr.WithinOperator expr) {
        final FlatExpr.WithinOperator newNode = new FlatExpr.WithinOperator(expr.operator, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.operand);
        queue.add(expr.min);
        queue.add(expr.max);

        return newNode;
    }

    @Override
    public FlatExpr visitBetweenOperator(final Expr.BetweenOperator expr) {
        final FlatExpr.BetweenOperator newNode = new FlatExpr.BetweenOperator(expr.operator, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.operand);
        queue.add(expr.min);
        queue.add(expr.max);

        return newNode;
    }

    @Override
    public FlatExpr visitLiteralExpr(final Expr.Literal expr) {
        final FlatExpr.Literal newNode = new FlatExpr.Literal(expr.result);
        nodes.add(newNode);

        return newNode;
    }

    @Override
    public FlatExpr visitLogicalExpr(final Expr.Logical expr) {
        final FlatExpr.Logical newNode = new FlatExpr.Logical(expr.operator, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.left);
        queue.add(expr.right);

        return newNode;
    }

    @Override
    public FlatExpr visitTernaryExpr(final Expr.Ternary expr) {
        final FlatExpr.Ternary newNode = new FlatExpr.Ternary(expr.operator, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.condition);
        queue.add(expr.trueExpr);
        queue.add(expr.falseExpr);

        return newNode;
    }

    @Override
    public FlatExpr visitUnaryExpr(final Expr.Unary expr) {
        final FlatExpr.Unary newNode = new FlatExpr.Unary(expr.operator, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.expression);

        return newNode;
    }

    @Override
    public FlatExpr visitIdentifierExpr(final Expr.Identifier expr) {
        final FlatExpr.Identifier newNode = new FlatExpr.Identifier(expr.operator);
        nodes.add(newNode);

        return newNode;
    }

    @Override
    public FlatExpr visitResolvedIdentifierExpr(final Expr.ResolvedIdentifier expr) {
        final FlatExpr.ResolvedIdentifier newNode = new FlatExpr.ResolvedIdentifier(expr.operator, expr.function);
        nodes.add(newNode);

        return newNode;
    }

    @Override
    public FlatExpr visitCallExpr(final Expr.Call expr) {
        final int startChild = expr.args.size() > 0 ? nodes.size() + queue.size() + 1 : -1;
        final FlatExpr.Call newNode = new FlatExpr.Call(expr.operator, expr.args.size(), startChild);
        nodes.add(newNode);

        enqueueArgs(expr.args);

        return newNode;
    }

    @Override
    public FlatExpr visitResolvedCallExpr(final Expr.ResolvedCall expr) {
        final int startChild = expr.args.size() > 0 ? nodes.size() + queue.size() + 1 : -1;
        final FlatExpr.ResolvedCall newNode = new FlatExpr.ResolvedCall(expr.operator, expr.args.size(), startChild, expr.function);
        nodes.add(newNode);

        enqueueArgs(expr.args);

        return newNode;
    }

    @Override
    public FlatExpr visitObjectCallExpr(final Expr.ObjectCall expr) {
        final FlatExpr.ObjectCall newNode = new FlatExpr.ObjectCall(expr.operator, expr.args.size() + 1, nodes.size() + queue.size() + 1);
        nodes.add(newNode);

        queue.add(expr.object);
        enqueueArgs(expr.args);

        return newNode;
    }

    @Override
    public FlatExpr visitResolvedObjectCallExpr(final Expr.ResolvedObjectCall expr) {
        final FlatExpr.ResolvedObjectCall newNode = new FlatExpr.ResolvedObjectCall(expr.operator, expr.args.size() + 1, nodes.size() + queue.size() + 1, expr.function);
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
