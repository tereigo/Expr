package com.tereigo.expr.variant;

import com.tereigo.expr.ExprType;
import com.tereigo.expr.utils.ByteBufferUtils;

public final class VariantUtils {
    private static final double EPSILON = 1e-6;

    private VariantUtils() {}

    public static boolean isEmpty(Variant operand) {
        return operand.exprType() == null;
    }

    public static boolean isNumber(Variant operand) {
        return isDouble(operand) || isLong(operand);
    }

    public static boolean isLong(Variant operand) {
        return operand.exprType() == ExprType.LONG;
    }

    public static boolean isDouble(Variant operand) {
        return operand.exprType() == ExprType.DOUBLE;
    }

    public static boolean isString(Variant operand) {
        return operand.exprType() == ExprType.STRING;
    }

    public static boolean isByteBuffer(Variant operand) {
        return operand.exprType() == ExprType.BYTE_BUFFER;
    }

    public static boolean isStringOrByteBuffer(Variant operand) {
        return isString(operand) || isByteBuffer(operand);
    }

    public static boolean isBoolean(Variant operand) {
        return operand.exprType() == ExprType.BOOL;
    }

    public static boolean isExprContext(Variant operand) {
        return operand.exprType() == ExprType.EXPR_CONTEXT;
    }

    public static boolean isObject(Variant operand) {
        return operand.exprType() == ExprType.OBJECT;
    }

    public static boolean epsilonEquals(double val1, double val2) {
        return Math.abs(val1 - val2) < EPSILON;
    }

    public static boolean isEqual(Variant left, Variant right) {
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

    public static Variant min(Variant left, Variant right) {
        return isGreaterNumbers(left, right) ? right : left;
    }

    public static Variant max(Variant left, Variant right) {
        return isGreaterNumbers(left, right) ? left : right;
    }

    public static boolean isGreaterNumbers(Variant left, Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() > right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isGreaterOrEqualNumbers(Variant left, Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() >= right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isLessNumbers(Variant left, Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() < right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isLessOrEqualNumbers(Variant left, Variant right) {
        if (isNumber(left) && isNumber(right)) {
            return left.getAsNumber() <= right.getAsNumber();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static void addNumbers(MutableVariant result, Variant left, Variant right) {
        if (isLong(left) && isLong(right)) {
            result.accept(Math.addExact(left.getAsLong(), right.getAsLong()));
        } else if (isNumber(left) && isNumber(right)) {
            result.accept(left.getAsNumber() + right.getAsNumber());
        } else {
            throw new RuntimeException("Operands must be numbers");
        }
    }

    public static void subtractNumbers( MutableVariant result, Variant left, Variant right) {
        if (isLong(left) && isLong(right)) {
            result.accept(Math.subtractExact(left.getAsLong(), right.getAsLong()));
        } else if (isNumber(left) && isNumber(right)) {
            result.accept(left.getAsNumber() - right.getAsNumber());
        } else {
            throw new RuntimeException("Operands must be numbers");
        }
    }

    public static void divideNumbers(MutableVariant result, Variant left, Variant right) {
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

    public static void multiplyNumbers(MutableVariant result, Variant left, Variant right) {
        if (isLong(left) && isLong(right)) {
            result.accept(Math.multiplyExact(left.getAsLong(), right.getAsLong()));
        } else if (isNumber(left) && isNumber(right)) {
            result.accept(left.getAsNumber() * right.getAsNumber());
        } else {
            throw new RuntimeException("Operands must be numbers");
        }
    }

    public static void modulusNumbers(MutableVariant result, Variant left, Variant right) {
        if (isLong(left) && isLong(right)) {
            if (right.getAsLong() == 0) {
                throw new RuntimeException("Division by zero");
            }
            result.accept(left.getAsLong() % right.getAsLong());
            return;
        }
        throw new RuntimeException("Operands must be long numbers");
    }

    public static void negateNumber(MutableVariant result, Variant operand) {
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
