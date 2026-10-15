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

    // -9223372036854775808.0: Long.MIN_VALUE is a power of two, so it round-trips through double exactly.
    private static final double LONG_MIN_VALUE_AS_DOUBLE = Long.MIN_VALUE;
    // 2^63: the first double value too large to equal any long. Long.MAX_VALUE itself has no exact
    // double representation and rounds UP to this value, so the valid range is upper-bound exclusive.
    private static final double LONG_RANGE_UPPER_BOUND_EXCLUSIVE = 9223372036854775808.0;

    // Whether d falls within the range of values a long could exactly equal. Also correctly (and
    // incidentally) excludes NaN and +-Infinity, since every relational operator against them is false.
    private static boolean isInLongRange(final double d) {
        return d >= LONG_MIN_VALUE_AS_DOUBLE && d < LONG_RANGE_UPPER_BOUND_EXCLUSIVE;
    }

    // Exact long/double equality check. Naively converting l to double (as left.getAsNumber() /
    // right.getAsNumber() do) silently loses precision for magnitudes beyond 2^53 - e.g.
    // 9007199254740993L would round to 9007199254740992.0 and incorrectly compare equal to it.
    // A double can only equal a long if it holds a whole number within isInLongRange(), in which
    // case the (long) cast below is always exact (no rounding/clamping), so this never loses precision.
    private static boolean longEqualsDouble(final long l, final double d) {
        return isInLongRange(d) && d == Math.floor(d) && l == (long) d;
    }

    // Exact 3-way comparison of a long and a FINITE double, without ever converting l to double.
    // Returns <0, 0, or >0 as l is numerically less than, equal to, or greater than d.
    // Precondition: d must not be NaN - every caller must check Double.isNaN(d) first and treat it
    // as "false" for every relational operator (==, <, <=, >, >=), matching plain double semantics;
    // this method does not special-case NaN itself since a single int can't represent "unordered".
    private static int compareLongToDouble(final long l, final double d) {
        if (d >= LONG_RANGE_UPPER_BOUND_EXCLUSIVE) {
            return -1; // d >= 2^63: greater than every representable long
        }
        if (d < LONG_MIN_VALUE_AS_DOUBLE) {
            return 1; // d < Long.MIN_VALUE: less than every representable long
        }
        final double flo = Math.floor(d);
        final long whole = (long) flo; // exact: flo is a whole number within [Long.MIN_VALUE, 2^63)
        if (l != whole) {
            return Long.compare(l, whole);
        }
        // l == floor(d): if d has a fractional remainder, its true value exceeds l
        return (d > flo) ? -1 : 0;
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
        if (isLong(left) && isDouble(right)) {
            return longEqualsDouble(left.getAsLong(), right.getAsDouble());
        }
        if (isDouble(left) && isLong(right)) {
            return longEqualsDouble(right.getAsLong(), left.getAsDouble());
        }
        if (isDouble(left) && isDouble(right)) {
            return epsilonEquals(left.getAsDouble(), right.getAsDouble());
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
        if (isLong(left) && isLong(right)) {
            return left.getAsLong() > right.getAsLong();
        }
        if (isLong(left) && isDouble(right)) {
            final double d = right.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(left.getAsLong(), d) > 0;
        }
        if (isDouble(left) && isLong(right)) {
            final double d = left.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(right.getAsLong(), d) < 0;
        }
        if (isDouble(left) && isDouble(right)) {
            return left.getAsDouble() > right.getAsDouble();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isGreaterOrEqualNumbers(final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            return left.getAsLong() >= right.getAsLong();
        }
        if (isLong(left) && isDouble(right)) {
            final double d = right.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(left.getAsLong(), d) >= 0;
        }
        if (isDouble(left) && isLong(right)) {
            final double d = left.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(right.getAsLong(), d) <= 0;
        }
        if (isDouble(left) && isDouble(right)) {
            return left.getAsDouble() >= right.getAsDouble();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isLessNumbers(final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            return left.getAsLong() < right.getAsLong();
        }
        if (isLong(left) && isDouble(right)) {
            final double d = right.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(left.getAsLong(), d) < 0;
        }
        if (isDouble(left) && isLong(right)) {
            final double d = left.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(right.getAsLong(), d) > 0;
        }
        if (isDouble(left) && isDouble(right)) {
            return left.getAsDouble() < right.getAsDouble();
        }
        throw new RuntimeException("Operands must be numbers");
    }

    public static boolean isLessOrEqualNumbers(final Variant left, final Variant right) {
        if (isLong(left) && isLong(right)) {
            return left.getAsLong() <= right.getAsLong();
        }
        if (isLong(left) && isDouble(right)) {
            final double d = right.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(left.getAsLong(), d) <= 0;
        }
        if (isDouble(left) && isLong(right)) {
            final double d = left.getAsDouble();
            return !Double.isNaN(d) && compareLongToDouble(right.getAsLong(), d) >= 0;
        }
        if (isDouble(left) && isDouble(right)) {
            return left.getAsDouble() <= right.getAsDouble();
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
