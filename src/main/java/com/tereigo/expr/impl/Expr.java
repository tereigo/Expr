package com.tereigo.expr.impl;

import com.tereigo.expr.ExprConstant;
import com.tereigo.expr.function.Function0;
import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;
import com.tereigo.expr.variant.VariantFactory;

import java.util.List;

public abstract class Expr {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);     // ==, !=, >, >=, <, <=, +, -, *, /

        R visitInOperator(InOperator expr); // in [...]

        R visitWithinOperator(WithinOperator expr); // within [...] - including boundaries

        R visitBetweenOperator(BetweenOperator expr); // between [...] - excluding boundaries

        R visitStaticWithinOperator(StaticWithinOperator expr); // within [...] - including boundaries

        R visitStaticBetweenOperator(StaticBetweenOperator expr); // between [...] - excluding boundaries

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

    public abstract static class BaseExpr extends Expr {
        public final Token operator;
        // result of the evaluation of this expression
        public final MutableVariant result = VariantFactory.createEmpty();

        BaseExpr(final Token operator) {
            this.operator = operator;
        }
    }

    public static class Binary extends BaseExpr {
        public final Expr left;
        public final Expr right;

        Binary(final Expr left, final Token operator, final Expr right) {
            super(operator);
            this.left = left;
            this.right = right;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    public static class InOperator extends BaseExpr {
        public final Expr operand;
        public final List<Expr> values;

        InOperator(final Expr operand, final Token operator, final List<Expr> values) {
            super(operator);
            this.operand = operand;
            this.values = values;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitInOperator(this);
        }
    }

    public abstract static class RangeOperator extends BaseExpr {
        public final Expr operand;
        public final Expr min;
        public final Expr max;

        RangeOperator(final Expr operand, final Token operator, final Expr min, final Expr max) {
            super(operator);
            this.operand = operand;
            this.min = min;
            this.max = max;
        }
    }

    public static class WithinOperator extends RangeOperator {
        WithinOperator(final Expr operand, final Token operator, final Expr min, final Expr max) {
            super(operand, operator, min, max);
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitWithinOperator(this);
        }
    }

    public static class BetweenOperator extends RangeOperator {
        BetweenOperator(final Expr operand, final Token operator, final Expr min, final Expr max) {
            super(operand, operator, min, max);
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitBetweenOperator(this);
        }
    }

    public abstract static class StaticRangeOperator extends BaseExpr {
        public final Expr operand;
        public final Variant min;
        public final Variant max;

        StaticRangeOperator(final Expr operand, final Token operator, final Variant min, final Variant max) {
            super(operator);
            this.operand = operand;
            this.min = min;
            this.max = max;
        }
    }

    public static class StaticWithinOperator extends StaticRangeOperator {
        StaticWithinOperator(final Expr operand, final Token operator, final Variant min, final Variant max) {
            super(operand, operator, min, max);
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitStaticWithinOperator(this);
        }
    }

    public static class StaticBetweenOperator extends StaticRangeOperator {
        StaticBetweenOperator(final Expr operand, final Token operator, final Variant min, final Variant max) {
            super(operand, operator, min, max);
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitStaticBetweenOperator(this);
        }
    }

    public static class Literal extends Expr implements ExprConstant {
        static final Literal BOOL_TRUE = new Expr.Literal(true);
        static final Literal BOOL_FALSE = new Expr.Literal(false);

        static final Literal PI = new Expr.Literal(Math.PI);
        static final Literal E = new Expr.Literal(Math.E);

        // for Literal it's Variant because it's immutable
        public final Variant result;

        public Literal(final double value) {
            this.result = VariantFactory.createImmutableDouble(value);
        }

        public Literal(final long value) {
            this.result = VariantFactory.createImmutableLong(value);
        }

        public Literal(final boolean value) {
            this.result = VariantFactory.createImmutableBoolean(value);
        }

        public Literal(final String value) {
            this.result = VariantFactory.createImmutableString(value);
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    public static class Logical extends BaseExpr {
        public final Expr left;
        public final Expr right;

        Logical(final Expr left, final Token operator, final Expr right) {
            super(operator);
            this.left = left;
            this.right = right;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    public static class Ternary extends Expr {
        public final Token operator;
        public final Expr condition;
        public final Expr trueExpr;
        public final Expr falseExpr;

        Ternary(final Token operator, final Expr condition, final Expr trueExpr, final Expr falseExpr) {
            this.operator = operator;
            this.condition = condition;
            this.trueExpr = trueExpr;
            this.falseExpr = falseExpr;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitTernaryExpr(this);
        }
    }

    public static class Unary extends BaseExpr {
        public final Expr expression;

        Unary(final Token operator, final Expr expression) {
            super(operator);
            this.expression = expression;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    public static class Identifier extends BaseExpr {
        Identifier(final Token name) {
            super(name);
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitIdentifierExpr(this);
        }
    }

    public static class ResolvedIdentifier extends BaseExpr {
        public final Function0 function;

        ResolvedIdentifier(final Token name, final Function0 function) {
            super(name);
            this.function = function;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedIdentifierExpr(this);
        }
    }

    public static class Call extends BaseExpr {
        public final List<Expr> args;

        Call(final Token name, final List<Expr> args) {
            super(name);
            this.args = args;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    public static class ResolvedCall extends BaseExpr {
        public final Object function;
        public final List<Expr> args;

        ResolvedCall(final Token name, final Object function, final List<Expr> args) {
            super(name);
            this.function = function;
            this.args = args;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedCallExpr(this);
        }
    }

    public static class ObjectCall extends Call {
        public final Expr object;

        ObjectCall(final Expr object, final Token name, final List<Expr> args) {
            super(name, args);
            this.object = object;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitObjectCallExpr(this);
        }
    }

    public static class ResolvedObjectCall extends ResolvedCall {
        public final Expr object;

        ResolvedObjectCall(final Expr object, final Token name, final Object function, final List<Expr> args) {
            super(name, function, args);
            this.object = object;
        }

        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedObjectCallExpr(this);
        }
    }
}
