package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.variant.Variant;
import com.tereigo.atlas_expr.variant.VariantFactory;
import com.tereigo.atlas_expr.variant.VariantImpl;

import java.util.List;

abstract class Expr {

  abstract <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);     // ==, !=, >, >=, <, <=, +, -, *, /
    R visitInOperator(InOperator expr); // in [...]
    R visitGroupingExpr(Grouping expr); // ()
    R visitLiteralExpr(Literal expr);   // long, double, string, boolean values
    R visitLogicalExpr(Logical expr);   // or, and
    R visitUnaryExpr(Unary expr);       // -, not
    R visitIdentifierExpr(Identifier expr);
    R visitCallExpr(Expr.Call expr);
  }

  static class Binary extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;
    // result of the evaluation of this expression
    final MutableVariant result = VariantFactory.createEmpty();

    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }
  }

  static class InOperator extends Expr {
    final Expr operand;
    final Token operator;
    final List<Variant> values;
    // result of the evaluation of this expression
    final MutableVariant result = VariantFactory.createEmpty();

    InOperator(Expr operand, Token operator, List<Variant> values) {
      this.operand = operand;
      this.operator = operator;
      this.values = values;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitInOperator(this);
    }
  }

  static class Grouping extends Expr {
    // Grouping is really a proxy to the underlying expression so it doesn't require Variant to store the result

    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    final Expr expression;
  }

  static class Literal extends Expr {
    static final Literal BOOL_TRUE = new Expr.Literal(true);
    static final Literal BOOL_FALSE = new Expr.Literal(false);

    // for Literal it's Variant because it's immutable
    final Variant result;

    Literal(double value) {
      this.result = VariantFactory.createImmutableDouble(value);
    }

    Literal(long value) {
      this.result = VariantFactory.createImmutableLong(value);
    }

    private Literal(boolean value) {
      this.result = VariantFactory.createImmutableBoolean(value);
    }

    Literal(String value) {
      this.result = VariantFactory.createImmutableString(value);
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }
  }

  static class Logical extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;
    // result of the evaluation of this expression
    final MutableVariant result = VariantFactory.createEmpty();

    Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }
  }

  static class Unary extends Expr {
    final Token operator;
    final Expr expression;
    // result of the evaluation of this expression
    final MutableVariant result = VariantFactory.createEmpty();

    Unary(Token operator, Expr expression) {
      this.operator = operator;
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }
  }

  static class Identifier extends Expr {
    final Token name;
    // result of the evaluation of this expression
    final MutableVariant result = VariantFactory.createEmpty();

    Identifier(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIdentifierExpr(this);
    }
  }

  static class Call extends Expr {
    final Token name;
    final List<Expr> args;
    final VariantImpl result = VariantFactory.createEmpty();

    Call(Token name, List<Expr> args) {
      this.name = name;
      this.args = args;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }
  }

}
