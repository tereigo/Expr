package com.tereigo.expr.impl;

import com.tereigo.expr.function.Function0;

abstract class FlatExpr {

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

    static abstract class BaseExpr extends FlatExpr {
        final Token operator;
        final short pos;

        BaseExpr(final Token operator, final short pos) {
            this.operator = operator;
            this.pos = pos;
        }
    }

    static class Binary extends BaseExpr {

        Binary(final Token operator, final short pos) {
            super(operator, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    static class InOperator extends BaseExpr {

        InOperator(final Token operator, final short pos) {
            super(operator, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitInOperator(this);
        }
    }

    abstract static class RangeOperator extends BaseExpr {

        RangeOperator(final Token operator, final short pos) {
            super(operator, pos);
        }
    }

    static class WithinOperator extends RangeOperator {
        WithinOperator(final Token operator, final short pos) {
            super(operator, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitWithinOperator(this);
        }
    }

    static class BetweenOperator extends RangeOperator {
        BetweenOperator(final Token operator, final short pos) {
            super(operator, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitBetweenOperator(this);
        }
    }

    static class Grouping extends FlatExpr {
        final short pos;

        Grouping(final short pos) {
            this.pos = pos;
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    static class Literal extends FlatExpr {
        final short pos;

        Literal(final short pos) {
            this.pos = pos;
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    static class Logical extends BaseExpr {
        Logical(final Token operator, final short pos) {
            super(operator, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    static class Ternary extends BaseExpr {
        Ternary(final Token operator, final short pos) {
            super(operator, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitTernaryExpr(this);
        }
    }

    static class Unary extends BaseExpr {

        Unary(final Token operator, final short pos) {
            super(operator, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    static class Identifier extends BaseExpr {
        Identifier(final Token name, final short pos) {
            super(name, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitIdentifierExpr(this);
        }
    }

    static class ResolvedIdentifier extends BaseExpr {
        final Function0 function;

        ResolvedIdentifier(final Token name, final Function0 function, final short pos) {
            super(name, pos);
            this.function = function;
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedIdentifierExpr(this);
        }
    }

    static class Call extends BaseExpr {

        Call(final Token name, final short pos) {
            super(name, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    static class ResolvedCall extends BaseExpr {
        final Object function;

        ResolvedCall(final Token name, final Object function, final short pos) {
            super(name, pos);
            this.function = function;
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedCallExpr(this);
        }
    }

    static class ObjectCall extends Call {

        ObjectCall(final Token name, final short pos) {
            super(name, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitObjectCallExpr(this);
        }
    }

    static class ResolvedObjectCall extends ResolvedCall {

        ResolvedObjectCall(final Token name, final Object function, final short pos) {
            super(name, function, pos);
        }

        @Override
        <R> R accept(final Visitor<R> visitor) {
            return visitor.visitResolvedObjectCallExpr(this);
        }
    }
}
