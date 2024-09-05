package com.tereigo.expr;

import java.util.List;

/*
  For expression "1.0 + 2" produces the following output: "(+ 1.0 2)"
 */
final class AstPolishPrinter implements Expr.Visitor<String> {

  String print(ASTRoot root) {
      return root.expr().accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitInOperator(Expr.InOperator expr) {
    StringBuilder builder = new StringBuilder();

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
  public String visitWithinOperator(Expr.WithinOperator expr) {
    return formatRangeOperator(expr);
  }

  @Override
  public String visitBetweenOperator(Expr.BetweenOperator expr) {
    return formatRangeOperator(expr);
  }

  private String formatRangeOperator(Expr.RangeOperator expr) {
    StringBuilder builder = new StringBuilder();

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
  public String visitGroupingExpr(Expr.Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    return expr.result.getAsObject().toString();
  }

  @Override
  public String visitLogicalExpr(Expr.Logical expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitTernaryExpr(Expr.Ternary expr) {
    return expr.condition.accept(this) +
            parenthesize(expr.operator.lexeme, expr.trueExpr, expr.falseExpr);
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.expression);
  }

  @Override
  public String visitIdentifierExpr(Expr.Identifier expr) {
    return expr.operator.lexeme;
  }

  @Override
  public String visitCallExpr(Expr.Call expr) {
    return "call " + expr.operator.lexeme + formatParams(expr.args);
  }

  @Override
  public String visitObjectCallExpr(Expr.ObjectCall expr) {
    return "obj call " + expr.object.accept(this) + "." + expr.operator.lexeme + formatParams(expr.args);
  }

  private String formatParams(List<Expr> list) {
    StringBuilder builder = new StringBuilder();
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

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }

}
