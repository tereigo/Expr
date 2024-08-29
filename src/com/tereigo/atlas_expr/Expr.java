package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.variant.Variant;
import com.tereigo.atlas_expr.variant.VariantFactory;

import java.util.List;

abstract class Expr {

  abstract <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);     // ==, !=, >, >=, <, <=, +, -, *, /
    R visitInOperator(InOperator expr); // in [...]
    R visitWithinOperator(WithinOperator expr); // within [...] - including boundaries
    R visitBetweenOperator(BetweenOperator expr); // between [...] - excluding boundaries
    R visitGroupingExpr(Grouping expr); // ()
    R visitLiteralExpr(Literal expr);   // long, double, string, boolean, ByteBuffer values
    R visitLogicalExpr(Logical expr);   // or, and
    R visitTernaryExpr(Ternary expr);   // boolExpr ? trueExpr : falseExpr
    R visitUnaryExpr(Unary expr);       // -, not
    R visitIdentifierExpr(Identifier expr); // external value
    R visitCallExpr(Call expr);         // function
    R visitObjectCallExpr(ObjectCall expr); // call method from the object
  }

  static abstract class BaseExpr extends Expr {
    final Token operator;
    // result of the evaluation of this expression
    final MutableVariant result = VariantFactory.createEmpty();

    BaseExpr(Token operator) {
      this.operator = operator;
    }
  }

  static class Binary extends BaseExpr {
    final Expr left;
    final Expr right;

    Binary(Expr left, Token operator, Expr right) {
      super(operator);
      this.left = left;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }
  }

  static class InOperator extends BaseExpr {
    final Expr operand;
    final List<Variant> values;

    InOperator(Expr operand, Token operator, List<Variant> values) {
      super(operator);
      this.operand = operand;
      this.values = values;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitInOperator(this);
    }
  }

  static class WithinOperator extends BaseExpr {
    final Expr operand;
    final Expr min;
    final Expr max;

    WithinOperator(Expr operand, Token operator, Expr min, Expr max) {
      super(operator);
      this.operand = operand;
      this.min = min;
      this.max = max;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWithinOperator(this);
    }
  }

  static class BetweenOperator extends BaseExpr {
    final Expr operand;
    final Expr min;
    final Expr max;

    BetweenOperator(Expr operand, Token operator, Expr min, Expr max) {
      super(operator);
      this.operand = operand;
      this.min = min;
      this.max = max;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBetweenOperator(this);
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

  static class Logical extends BaseExpr {
    final Expr left;
    final Expr right;

    Logical(Expr left, Token operator, Expr right) {
      super(operator);
      this.left = left;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }
  }

  static class Ternary extends Expr {
    final Token operator;
    final Expr condition;
    final Expr trueExpr;
    final Expr falseExpr;

    Ternary(Token operator, Expr condition, Expr trueExpr, Expr falseExpr) {
      this.operator = operator;
      this.condition = condition;
      this.trueExpr = trueExpr;
      this.falseExpr = falseExpr;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitTernaryExpr(this);
    }
  }

  static class Unary extends BaseExpr {
    final Expr expression;

    Unary(Token operator, Expr expression) {
      super(operator);
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }
  }

  static class Identifier extends BaseExpr {
    Identifier(Token name) {
      super(name);
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIdentifierExpr(this);
    }
  }

  static class Call extends BaseExpr {
    final List<Expr> args;

    Call(Token name, List<Expr> args) {
      super(name);
      this.args = args;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }
  }

  static class ObjectCall extends Call {
    final Expr object;

    ObjectCall(Expr object, Token name, List<Expr> args) {
      super(name, args);
      this.object = object;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitObjectCallExpr(this);
    }
  }
}
