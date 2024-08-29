package com.tereigo.atlas_expr.variant;

import com.tereigo.atlas_expr.ExprType;
import com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils;

import static com.tereigo.atlas_expr.atlas.utils.AlgoUtils.epsilonEquals;

public final class VariantUtils {

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

    public static boolean isBoolean(Variant operand) {
        return operand.exprType() == ExprType.BOOL;
    }

    public static boolean isExprContext(Variant operand) {
        return operand.exprType() == ExprType.EXPR_CONTEXT;
    }

    public static boolean isObject(Variant operand) {
        return operand.exprType() == ExprType.OBJECT;
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

    public static void negateNumber(MutableVariant result, Variant operand) {
        if (isDouble(operand)) {
            result.accept(-operand.getAsDouble());
            return;
        }
        if (isLong(operand)) {
            result.accept(-operand.getAsLong());
            return;
        }
        throw new RuntimeException("Operand must be a number");
    }
}
