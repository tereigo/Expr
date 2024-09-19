package com.tereigo.expr.impl;

import java.util.ArrayList;
import java.util.List;

final class AstFlatter implements Expr.Visitor<Expr> {
    private List<Expr> nodes = new ArrayList<>();
    private List<Integer> numChildren = new ArrayList<>();
    private List<Integer> startChild = new ArrayList<>();

    FlatAST flatten(final ASTRoot root) {

        nodes.clear();
        numChildren.clear();
        startChild.clear();

        addNode(root.expr());
        root.expr().accept(this);

        return new FlatAST(nodes, numChildren, startChild);
    }

    private Expr evaluate(final Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Expr visitBinaryExpr(final Expr.Binary expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, 2);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.left);
        addNode(expr.right);

        evaluate(expr.left);
        evaluate(expr.right);
        return expr;
    }

    @Override
    public Expr visitInOperator(final Expr.InOperator expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, expr.values.size() + 1);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.operand);
        for (int i = 0; i < expr.values.size(); i++) {
            addNode(expr.values.get(i));
        }

        evaluate(expr.operand);
        evaluateArgs(expr.values);
        return expr;
    }

    @Override
    public Expr visitWithinOperator(final Expr.WithinOperator expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, 3);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.operand);
        addNode(expr.min);
        addNode(expr.max);

        evaluate(expr.operand);
        evaluate(expr.min);
        evaluate(expr.max);
        return expr;
    }

    @Override
    public Expr visitBetweenOperator(final Expr.BetweenOperator expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, 3);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.operand);
        addNode(expr.min);
        addNode(expr.max);

        evaluate(expr.operand);
        evaluate(expr.min);
        evaluate(expr.max);
        return expr;
    }

    @Override
    public Expr visitGroupingExpr(final Expr.Grouping expr) {
        return expr;
    }

    @Override
    public Expr visitLiteralExpr(final Expr.Literal expr) {
        return expr;
    }

    @Override
    public Expr visitLogicalExpr(final Expr.Logical expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, 2);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.left);
        addNode(expr.right);

        evaluate(expr.left);
        evaluate(expr.right);
        return expr;
    }

    @Override
    public Expr visitTernaryExpr(final Expr.Ternary expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, 3);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.condition);
        addNode(expr.trueExpr);
        addNode(expr.falseExpr);

        evaluate(expr.condition);
        evaluate(expr.trueExpr);
        evaluate(expr.falseExpr);
        return expr;
    }

    @Override
    public Expr visitUnaryExpr(final Expr.Unary expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, 1);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.expression);

        evaluate(expr.expression);
        return expr;
    }

    @Override
    public Expr visitIdentifierExpr(final Expr.Identifier expr) {
        return expr;
    }

    @Override
    public Expr visitResolvedIdentifierExpr(final Expr.ResolvedIdentifier expr) {
        // in theory this should never be called
        return expr;
    }

    @Override
    public Expr visitCallExpr(final Expr.Call expr) {
        if (expr.args.size() > 0) {
            final int parentIndex = findNode(expr);
            numChildren.set(parentIndex, expr.args.size());
            startChild.set(parentIndex, nodes.size());
            for (int i = 0; i < expr.args.size(); i++) {
                addNode(expr.args.get(i));
            }

            evaluateArgs(expr.args);
        }
        return expr;
    }

    @Override
    public Expr visitResolvedCallExpr(final Expr.ResolvedCall expr) {
        // in theory this should never be called
        return expr;
    }

    @Override
    public Expr visitObjectCallExpr(final Expr.ObjectCall expr) {
        final int parentIndex = findNode(expr);
        numChildren.set(parentIndex, expr.args.size() + 1);
        startChild.set(parentIndex, nodes.size());
        addNode(expr.object);
        for (int i = 0; i < expr.args.size(); i++) {
            addNode(expr.args.get(i));
        }

        evaluate(expr.object);
        evaluateArgs(expr.args);
        return expr;
    }

    @Override
    public Expr visitResolvedObjectCallExpr(final Expr.ResolvedObjectCall expr) {
        // in theory this should never be called
        return expr;
    }

    private void addNode(final Expr expr) {
        nodes.add(expr);
        numChildren.add(0);
        startChild.add(-1);
    }

    private int findNode(final Expr expr) {
        for (int j = 0; j < nodes.size(); j++) {
            if (nodes.get(j) == expr) {
                return j;
            }
        }
        return -1;
    }

    private void evaluateArgs(final List<Expr> args) {
        for (int i = 0; i < args.size(); i++) {
            evaluate(args.get(i));
        }
    }
}
