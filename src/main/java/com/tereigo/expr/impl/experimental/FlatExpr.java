package com.tereigo.expr.impl.experimental;

import com.tereigo.expr.function.Function0;
import com.tereigo.expr.impl.Token;
import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;
import com.tereigo.expr.variant.VariantFactory;

/**
 * Experimental: we use flat array-like structure for nodes (see FlatAST)
 *
 * It doesn't demonstrate any performance improvements
 *
 */
abstract class FlatExpr {

    abstract <R> R accept(Visitor<R> visitor);

    interface Visitor<R> {
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

    static abstract class BaseExpr extends FlatExpr {
        final Token operator;
        final byte numChildren;
        final short startChild;
        final MutableVariant result;

        BaseExpr(final Token operator) {
            this(operator, (byte)0, (short)-1);
        }

        BaseExpr(final Token operator, final Variant result) {
            this.operator = operator;
            this.numChildren = (byte)0;
            this.startChild = (short)-1;
            this.result = VariantFactory.clone(result);
        }

        BaseExpr(final Token operator, final int numChildren, final int startChild) {
            this.operator = operator;
            this.numChildren = (byte)numChildren;
            this.startChild = (short)startChild;
            this.result = VariantFactory.createEmpty();
        }
    }

    static class Binary extends BaseExpr {

        Binary(final Token operator, final int startChild) {
            super(operator, 2, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    static class InOperator extends BaseExpr {

        InOperator(final Token operator, final int numChildren, final int startChild) {
            super(operator, numChildren, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitInOperator(this);
        }
    }

    abstract static class RangeOperator extends BaseExpr {

        RangeOperator(final Token operator, final int numChildren, final int startChild) {
            super(operator, numChildren, startChild);
        }
    }

    static class WithinOperator extends RangeOperator {
        WithinOperator(final Token operator, final int startChild) {
            super(operator, 3, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitWithinOperator(this);
        }
    }

    static class BetweenOperator extends RangeOperator {
        BetweenOperator(final Token operator, final int startChild) {
            super(operator, 3, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitBetweenOperator(this);
        }
    }

    abstract static class StaticRangeOperator extends BaseExpr {
        final Variant min;
        final Variant max;

        StaticRangeOperator(final Token operator, final int numChildren, final int startChild, final Variant min, final Variant max) {
            super(operator, numChildren, startChild);
            this.min = min;
            this.max = max;
        }
    }

    static class StaticWithinOperator extends StaticRangeOperator {
        StaticWithinOperator(final Token operator, final int startChild, final Variant min, final Variant max) {
            super(operator, 1, startChild, min, max);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitStaticWithinOperator(this);
        }
    }

    static class StaticBetweenOperator extends StaticRangeOperator {
        StaticBetweenOperator(final Token operator, final int startChild, final Variant min, final Variant max) {
            super(operator, 1, startChild, min, max);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitStaticBetweenOperator(this);
        }
    }

    static class Literal extends BaseExpr {

        Literal(final Variant result) {
            super(null, result);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    static class Logical extends BaseExpr {
        Logical(final Token operator, final int startChild) {
            super(operator, 2, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    static class Ternary extends BaseExpr {
        Ternary(final Token operator, final int startChild) {
            super(operator, 3, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitTernaryExpr(this);
        }
    }

    static class Unary extends BaseExpr {

        Unary(final Token operator, final int startChild) {
            super(operator, 1, startChild);
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

        Call(final Token name, final int numChildren, final int startChild) {
            super(name, numChildren, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    static class ResolvedCall extends BaseExpr {
        final Object function;

        ResolvedCall(final Token name, final int numChildren, final int startChild, final Object function) {
            super(name, numChildren, startChild);
            this.function = function;
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedCallExpr(this);
        }
    }

    static class ObjectCall extends Call {

        ObjectCall(final Token name, final int numChildren, final int startChild) {
            super(name, numChildren, startChild);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitObjectCallExpr(this);
        }
    }

    static class ResolvedObjectCall extends ResolvedCall {

        ResolvedObjectCall(final Token name, final int numChildren, final int startChild, final Object function) {
            super(name, numChildren, startChild, function);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedObjectCallExpr(this);
        }
    }
}
