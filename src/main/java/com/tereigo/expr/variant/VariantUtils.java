package com.tereigo.expr.variant;

import com.tereigo.expr.utils.ByteBufferUtils;

public final class VariantUtils {
    private static final double EPSILON = 1e-6;

    private VariantUtils() {
    }

    public static boolean isEmpty(final Variant operand) {
        return operand.exprType() == null;
    }

    public static boolean isNumber(final Variant operand) {
        return isDouble(operand) || isLong(operand);
    }

    public static boolean isLong(final Variant operand) {
        return operand.exprType() == ExprType.LONG;
    }

    public static boolean isDouble(final Variant operand) {
        return operand.exprType() == ExprType.DOUBLE;
    }

    public static boolean isString(final Variant operand) {
        return operand.exprType() == ExprType.STRING;
    }

    public static boolean isByteBuffer(final Variant operand) {
        return operand.exprType() == ExprType.BYTE_BUFFER;
    }

    public static boolean isStringOrByteBuffer(final Variant operand) {
        return isString(operand) || isByteBuffer(operand);
    }

    public static boolean isBoolean(final Variant operand) {
        return operand.exprType() == ExprType.BOOL;
    }

    public static boolean isExprContext(final Variant operand) {
        return operand.exprType() == ExprType.EXPR_CONTEXT;
    }

    public static boolean isObject(final Variant operand) {
        return operand.exprType() == ExprType.OBJECT;
    }

    public static boolean epsilonEquals(final double val1, final double val2) {
        return Math.abs(val1 - val2) < EPSILON;
    }

    public static boolean isEqual(final Variant left, final Variant right) {
        if (isEmpty(left) && isEmpty(right)) {
            return true;
        }

        if (isEmpty(left) || isEmpty(right)) {
            return false;
        }

        if (isString(left) && isString(right)) {
            if (left.getAsString() == null && right.getAsString() == null) {
                return true;
            }
            if (left.getAsString() == null || right.getAsString() == null) {
                return false;
            }
            return left.equals(right);
        }
        if (isByteBuffer(left) && isByteBuffer(right)) {
            // it should be the same as:
            // return left.getAsByteBuffer().equals(right.getAsByteBuffer());
            return ByteBufferUtils.equals(left.getAsByteBuffer(), right.getAsByteBuffer());
        }
        if (isByteBuffer(left) && isString(right)) {
            return ByteBufferUtils.equals(left.getAsByteBuffer(), right.getAsString());
        }
        if (isString(left) && isByteBuffer(right)) {
            return ByteBufferUtils.equals(right.getAsByteBuffer(), left.getAsString());
        }
        if (isLong(left) && isLong(right)) {
            return left.getAsLong() == right.getAsLong();
        }
        if (isNumber(left) && isNumber(right)) {
            return epsilonEquals(left.getAsNumber(), right.getAsNumber());
        }
        if (isBoolean(left) && isBoolean(right)) {
            return left.getAsBoolean() == right.getAsBoolean();
        }
        throw new RuntimeException("Operands of different types cannot be compared: " + left.exprType() + " and " + right.exprType());
    }

    public static Variant min(final Variant left, final Variant right) {
        return isGreaterNumbers(left, right) ? right : left;
    }

    public static Variant max(final Variant left, final Variant right) {
        return isGreaterNumbers(left, right) ? left : right;
    }

    public static boolean isGreaterNumbers(final Variant left, final Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() > right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isGreaterOrEqualNumbers(final Variant left, final Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() >= right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isLessNumbers(final Variant left, final Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() < right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isLessOrEqualNumbers(final Variant left, final Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() <= right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static void addNumbers(final MutableVariant result, final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            result.accept(Math.addExact(left.getAsLong(), right.getAsLong()));
        } else if (isNumber(left) && isNumber(right)) {
            result.accept(left.getAsNumber() + right.getAsNumber());
        } else {
            throw new RuntimeException("Operands must be numbers");
        }
    }

    public static void subtractNumbers(final MutableVariant result, final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            result.accept(Math.subtractExact(left.getAsLong(), right.getAsLong()));
        } else if (isNumber(left) && isNumber(right)) {
            result.accept(left.getAsNumber() - right.getAsNumber());
        } else {
            throw new RuntimeException("Operands must be numbers");
        }
    }

    public static void divideNumbers(final MutableVariant result, final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            if (right.getAsLong() == 0) {
                throw new RuntimeException("Division by zero");
            }
            result.accept(left.getAsLong() / right.getAsLong());
        } else if (isNumber(left) && isNumber(right)) {
            result.accept(left.getAsNumber() / right.getAsNumber());
        } else {
            throw new RuntimeException("Operands must be numbers");
        }
    }

    public static void multiplyNumbers(final MutableVariant result, final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            result.accept(Math.multiplyExact(left.getAsLong(), right.getAsLong()));
        } else if (isNumber(left) && isNumber(right)) {
            result.accept(left.getAsNumber() * right.getAsNumber());
        } else {
            throw new RuntimeException("Operands must be numbers");
        }
    }

    public static void modulusNumbers(final MutableVariant result, final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            if (right.getAsLong() == 0) {
                throw new RuntimeException("Division by zero");
            }
            result.accept(left.getAsLong() % right.getAsLong());
            return;
        }
        throw new RuntimeException("Operands must be long numbers");
    }

    public static void negateNumber(final MutableVariant result, final Variant operand) {
        if (isDouble(operand)) {
            result.accept(-operand.getAsDouble());
            return;
        }
        if (isLong(operand)) {
            result.accept(Math.negateExact(operand.getAsLong()));
            return;
        }
        throw new RuntimeException("Operand must be a number");
    }
}
