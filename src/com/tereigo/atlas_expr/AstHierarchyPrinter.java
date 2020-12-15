package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.Variant;

import java.util.List;

/*
  For expression "1.0 + 2" produces the following output:
  +
  │
  ├── 1.0
  │
  ├── 2"
 */
class AstHierarchyPrinter implements Expr.Visitor<String> {
  private int level = 0;

  String print(Expr expr) {
    level = 0;
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return formatExpr(expr.operator, expr.left, expr.right);
  }

  @Override
  public String visitInOperator(Expr.InOperator expr) {
    StringBuilder builder = new StringBuilder();
    String ident = generateIdent(level);
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
  public String visitGroupingExpr(Expr.Grouping expr) {
    return expr.expression.accept(this);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    return expr.result.getAsObject().toString();
  }

  @Override
  public String visitLogicalExpr(Expr.Logical expr) {
    return formatExpr(expr.operator, expr.left, expr.right);
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return formatExpr(expr.operator, expr.expression, null);
  }

  @Override
  public String visitIdentifierExpr(Expr.Identifier expr) {
    return expr.name.lexeme;
  }

  @Override
  public String visitCallExpr(Expr.Call expr) {
    return "call " + expr.name.lexeme + formatParams(expr.args);
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

  private String formatExpr(Token token, Expr left, Expr right) {
    StringBuilder builder = new StringBuilder();
    String ident = generateIdent(level);
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

  private static String formatList(List<Variant> list) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (int i = 0; i < list.size(); i++) {
      builder.append(list.get(i).getAsObject());
      if (i < list.size() - 1) {
        builder.append(", ");
      }
    }
    builder.append("]");
    return builder.toString();
  }

  private static String generateIdent(int level) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < level * 4; i++) {
      if (i % 4 == 0) {
        builder.append('│');
      } else {
        builder.append(' ');
      }
    }
    return builder.toString();
  }
}
