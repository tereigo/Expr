package com.tereigo.expr.impl;

import java.util.List;

/*
  For expression "1.0 + 2" produces the following output:
  +
  │
  ├── 1.0
  │
  ├── 2
 */
final class AstHierarchyPrinter implements Expr.Visitor<String> {
    private int level = 0;

    private static String generateIdent(final int level) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level * 4; i++) {
            if (i % 4 == 0) {
                builder.append('│');
            } else {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    String print(final ASTRoot root) {
        level = 0;
        return root.expr().accept(this);
    }

    @Override
    public String visitBinaryExpr(final Expr.Binary expr) {
        return formatExpr(expr.operator, expr.left, expr.right);
    }

    @Override
    public String visitInOperator(final Expr.InOperator expr) {
        final StringBuilder builder = new StringBuilder();
        final String ident = generateIdent(level);
        builder.append(expr.operator.lexeme).append("\n");
        builder.append(ident).append('│').append("\n");
        level++;
        builder.append(ident).append("├── ").append(expr.operand.accept(this)).append("\n");
        builder.append(ident).append('│').append("\n");
        builder.append(ident).append("├── ").append(formatList(expr.values));
        level--;
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
        final String ident = generateIdent(level);
        builder.append(expr.operator.lexeme).append("\n");
        builder.append(ident).append('│').append("\n");
        level++;
        builder.append(ident).append("├── ").append(expr.operand.accept(this)).append("\n");
        builder.append(ident).append('│').append("\n");
        builder.append(ident).append("├── [").append(expr.min.accept(this)).append(", ").append(expr.max.accept(this)).append("]");
        level--;
        return builder.toString();
    }

    @Override
    public String visitLiteralExpr(final Expr.Literal expr) {
        return expr.result.getAsObject().toString();
    }

    @Override
    public String visitLogicalExpr(final Expr.Logical expr) {
        return formatExpr(expr.operator, expr.left, expr.right);
    }

    @Override
    public String visitTernaryExpr(final Expr.Ternary expr) {
        final StringBuilder builder = new StringBuilder();
        final String ident = generateIdent(level);
        builder.append(expr.operator.lexeme).append("\n");
        builder.append(ident).append('│').append("\n");
        level++;
        builder.append(ident).append("├── ").append(expr.condition.accept(this)).append("\n");
        builder.append(ident).append('│').append("\n");
        builder.append(ident).append("├── ").append(expr.trueExpr.accept(this)).append("\n");
        builder.append(ident).append('│').append("\n");
        builder.append(ident).append("├── ").append(expr.falseExpr.accept(this));
        level--;
        return builder.toString();
    }

    @Override
    public String visitUnaryExpr(final Expr.Unary expr) {
        return formatExpr(expr.operator, expr.expression, null);
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

    private String formatExpr(final Token token, final Expr left, final Expr right) {
        final StringBuilder builder = new StringBuilder();
        final String ident = generateIdent(level);
        builder.append(token.lexeme).append("\n");
        builder.append(ident).append('│').append("\n");
        level++;
        builder.append(ident).append("├── ").append(left.accept(this));
        if (right != null) {
            builder.append("\n");
            builder.append(ident).append('│').append("\n");
            builder.append(ident).append("├── ").append(right.accept(this));
        }
        level--;
        return builder.toString();
    }

    private String formatList(final List<Expr> list) {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i).accept(this));
            if (i < list.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
