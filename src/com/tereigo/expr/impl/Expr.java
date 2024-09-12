package com.tereigo.expr.impl;

import com.tereigo.expr.function.Function0;
import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;
import com.tereigo.expr.variant.VariantFactory;

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
    R visitResolvedIdentifierExpr(ResolvedIdentifier expr); // external value
    R visitCallExpr(Call expr);         // function
    R visitResolvedCallExpr(ResolvedCall expr);         // function
    R visitObjectCallExpr(ObjectCall expr); // call method from the object
    R visitResolvedObjectCallExpr(ResolvedObjectCall expr); // call method from the object
  }

  static abstract class BaseExpr extends Expr {
    final Token operator;
    // result of the evaluation of this expression
    final MutableVariant result = VariantFactory.createEmpty();

    BaseExpr(final Token operator) {
      this.operator = operator;
    }
  }

  static class Binary extends BaseExpr {
    final Expr left;
    final Expr right;

    Binary(final Expr left, final Token operator, final Expr right) {
      super(operator);
      this.left = left;
      this.right = right;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }
  }

  static class InOperator extends BaseExpr {
    final Expr operand;
    final List<Expr> values;

    InOperator(final Expr operand, final Token operator, final List<Expr> values) {
      super(operator);
      this.operand = operand;
      this.values = values;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitInOperator(this);
    }
  }

  abstract static class RangeOperator extends BaseExpr {
    final Expr operand;
    final Expr min;
    final Expr max;

    RangeOperator(final Expr operand, final Token operator, final Expr min, final Expr max) {
      super(operator);
      this.operand = operand;
      this.min = min;
      this.max = max;
    }
  }

  static class WithinOperator extends RangeOperator {
    WithinOperator(final Expr operand, final Token operator, final Expr min, final Expr max) {
      super(operand, operator, min, max);
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitWithinOperator(this);
    }
  }

  static class BetweenOperator extends RangeOperator {
    BetweenOperator(final Expr operand, final Token operator, final Expr min, final Expr max) {
      super(operand, operator, min, max);
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitBetweenOperator(this);
    }
  }

  static class Grouping extends Expr {
    // Grouping is really a proxy to the underlying expression so it doesn't require Variant to store the result
    final Expr expression;

    Grouping(final Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }
  }

  static class Literal extends Expr {
    static final Literal BOOL_TRUE = new Expr.Literal(true);
    static final Literal BOOL_FALSE = new Expr.Literal(false);

    static final Literal PI = new Expr.Literal(Math.PI);
    static final Literal E = new Expr.Literal(Math.E);

    // for Literal it's Variant because it's immutable
    final Variant result;

    Literal(final double value) {
      this.result = VariantFactory.createImmutableDouble(value);
    }

    Literal(final long value) {
      this.result = VariantFactory.createImmutableLong(value);
    }

    private Literal(final boolean value) {
      this.result = VariantFactory.createImmutableBoolean(value);
    }

    Literal(final String value) {
      this.result = VariantFactory.createImmutableString(value);
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }
  }

  static class Logical extends BaseExpr {
    final Expr left;
    final Expr right;

    Logical(final Expr left, final Token operator, final Expr right) {
      super(operator);
      this.left = left;
      this.right = right;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }
  }

  static class Ternary extends Expr {
    final Token operator;
    final Expr condition;
    final Expr trueExpr;
    final Expr falseExpr;

    Ternary(final Token operator, final Expr condition, final Expr trueExpr, final Expr falseExpr) {
      this.operator = operator;
      this.condition = condition;
      this.trueExpr = trueExpr;
      this.falseExpr = falseExpr;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitTernaryExpr(this);
    }
  }

  static class Unary extends BaseExpr {
    final Expr expression;

    Unary(final Token operator, final Expr expression) {
      super(operator);
      this.expression = expression;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }
  }

  static class Identifier extends BaseExpr {
    Identifier(final Token name) {
      super(name);
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitIdentifierExpr(this);
    }
  }

  static class ResolvedIdentifier extends BaseExpr {
    final Function0 function;

    ResolvedIdentifier(final Token name, final Function0 function) {
      super(name);
      this.function = function;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitResolvedIdentifierExpr(this);
    }
  }

  static class Call extends BaseExpr {
    final List<Expr> args;

    Call(final Token name, final List<Expr> args) {
      super(name);
      this.args = args;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }
  }

  static class ResolvedCall extends BaseExpr {
    final Object function;
    final List<Expr> args;

    ResolvedCall(final Token name, final Object function, final List<Expr> args) {
      super(name);
      this.function = function;
      this.args = args;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitResolvedCallExpr(this);
    }
  }

  static class ObjectCall extends Call {
    final Expr object;

    ObjectCall(final Expr object, final Token name, final List<Expr> args) {
      super(name, args);
      this.object = object;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitObjectCallExpr(this);
    }
  }

  static class ResolvedObjectCall extends ResolvedCall {
    final Expr object;

    ResolvedObjectCall(final Expr object, final Token name, final Object function, final List<Expr> args) {
      super(name, function, args);
      this.object = object;
    }

    @Override
    <R> R accept(final Visitor<R> visitor) {
      return visitor.visitResolvedObjectCallExpr(this);
    }
  }
}
