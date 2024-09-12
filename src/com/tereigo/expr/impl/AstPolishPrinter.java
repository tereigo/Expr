package com.tereigo.expr.impl;

import java.util.List;

/*
  For expression "1.0 + 2" produces the following output: "(+ 1.0 2)"
 */
final class AstPolishPrinter implements Expr.Visitor<String> {

    String print(final ASTRoot root) {
        return root.expr().accept(this);
    }

    @Override
    public String visitBinaryExpr(final Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitInOperator(final Expr.InOperator expr) {
        final StringBuilder builder = new StringBuilder();

        builder.append("(").append(expr.operator.lexeme);
        builder.append(" ");
        builder.append(expr.operand.accept(this));
        builder.append(" [");
        for (int i = 0; i < expr.values.size(); i++) {
            builder.append(expr.values.get(i).accept(this));
            if (i < expr.values.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("])");

        return builder.toString();
    }

    @Override
    public String visitWithinOperator(final Expr.WithinOperator expr) {
        return formatRangeOperator(expr);
    }

    @Override
    public String visitBetweenOperator(final Expr.BetweenOperator expr) {
        return formatRangeOperator(expr);
    }

    private String formatRangeOperator(final Expr.RangeOperator expr) {
        final StringBuilder builder = new StringBuilder();

        builder.append("(").append(expr.operator.lexeme);
        builder.append(" ");
        builder.append(expr.operand.accept(this));
        builder.append(" [");
        builder.append(expr.min.accept(this));
        builder.append(", ");
        builder.append(expr.max.accept(this));
        builder.append("])");

        return builder.toString();
    }

    @Override
    public String visitGroupingExpr(final Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(final Expr.Literal expr) {
        return expr.result.getAsObject().toString();
    }

    @Override
    public String visitLogicalExpr(final Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitTernaryExpr(final Expr.Ternary expr) {
        return expr.condition.accept(this) +
                parenthesize(expr.operator.lexeme, expr.trueExpr, expr.falseExpr);
    }

    @Override
    public String visitUnaryExpr(final Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.expression);
    }

    @Override
    public String visitIdentifierExpr(final Expr.Identifier expr) {
        return expr.operator.lexeme;
    }

    @Override
    public String visitResolvedIdentifierExpr(final Expr.ResolvedIdentifier expr) {
        return expr.operator.lexeme;
    }

    @Override
    public String visitCallExpr(final Expr.Call expr) {
        return "call " + expr.operator.lexeme + formatParams(expr.args);
    }

    @Override
    public String visitResolvedCallExpr(final Expr.ResolvedCall expr) {
        return "call " + expr.operator.lexeme + formatParams(expr.args);
    }

    @Override
    public String visitObjectCallExpr(final Expr.ObjectCall expr) {
        return "obj call " + expr.object.accept(this) + "." + expr.operator.lexeme + formatParams(expr.args);
    }

    @Override
    public String visitResolvedObjectCallExpr(final Expr.ResolvedObjectCall expr) {
        return "obj call " + expr.object.accept(this) + "." + expr.operator.lexeme + formatParams(expr.args);
    }

    private String formatParams(final List<Expr> list) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i).accept(this));
            if (i < list.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    private String parenthesize(final String name, final Expr... exprs) {
        final StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (final Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
