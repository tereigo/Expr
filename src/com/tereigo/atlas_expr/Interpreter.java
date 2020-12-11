package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils;
import com.tereigo.atlas_expr.variant.Variant;
import com.tereigo.atlas_expr.variant.VariantUtils;

import static com.tereigo.atlas_expr.atlas.utils.AlgoUtils.epsilonEquals;

/*
  Evaluates the expressions defined in Expr class using the provided ExprEnvironment
 */
final class Interpreter implements Expr.Visitor<Variant> {

  private final Expr expression;
  private final ExprEnvironment env;

  Interpreter(final Expr expression) {
    this.expression = expression;
    this.env = ExprEnvironment.EMPTY;
  }

  Interpreter(final Expr expression, final ExprEnvironment env) {
    this.expression = expression;
    this.env = env;
  }

  Variant evaluate() {
    return evaluate(expression);
  }

  private Variant evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Variant visitBinaryExpr(Expr.Binary expr) {
    Variant left = evaluate(expr.left);
    Variant right = evaluate(expr.right);

    switch (expr.operator.type) {
      case EQUAL_EQUAL:
        expr.result.accept(isEqual(expr.operator, left, right));
        break;
      case NOT_EQUAL:
        expr.result.accept(!isEqual(expr.operator, left, right));
        break;
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        expr.result.accept(isGreaterNumbers(left, right));
        break;
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        expr.result.accept(isGreaterOrEqualNumbers(left, right));
        break;
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        expr.result.accept(isLessNumbers(left, right));
        break;
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        expr.result.accept(isLessOrEqualNumbers(left, right));
        break;
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        subtractNumbers(expr.result, left, right);
        break;
      case PLUS:
        checkNumberOperands(expr.operator, left, right);
        addNumbers(expr.result, left, right);
        break;
        // NOTICE: We don't allow String concatenation because it produces garbage
//        if (isString(left) && isString(right)) {
//          expr.result.accept(left.getAsString() + right.getAsString());
//          break;
//        }
//        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
      case DIV:
        checkNumberOperands(expr.operator, left, right);
        divideNumbers(expr.result, left, right);
        break;
      case MUL:
        checkNumberOperands(expr.operator, left, right);
        multiplyNumbers(expr.result, left, right);
        break;
      case MODULUS:
        checkLongOperands(expr.operator, left, right);
        modulusNumbers(expr.result, left, right);
        break;
    }
    return expr.result;
  }

  @Override
  public Variant visitInOperator(Expr.InOperator expr) {
    Variant operand = evaluate(expr.operand);
    for (int i = 0; i < expr.values.size(); i++) {
      checkOperandTypes(expr.operator, operand, expr.values.get(i));
      if (operand.equals(expr.values.get(i))) {
        expr.result.accept(true);
        return expr.result;
      }
    }
    expr.result.accept(false);
    return expr.result;
  }

  @Override
  public Variant visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }

  @Override
  public Variant visitLiteralExpr(Expr.Literal expr) {
    return expr.result;
  }

  @Override
  public Variant visitLogicalExpr(Expr.Logical expr) {
    Variant leftVar = evaluate(expr.left);
    checkBoolOperand(expr.operator, leftVar);
    boolean left = leftVar.getAsBoolean();
    if (expr.operator.type == TokenType.OR) {
      if (left) {
        expr.result.accept(true);
        return expr.result;
      }
    } else if (expr.operator.type == TokenType.AND) {
      if (!left) {
        expr.result.accept(false);
        return expr.result;
      }
    } else {
      throw new ParseError("Unexpected logical expression type: " + expr.operator.type);
    }
    Variant rightVar = evaluate(expr.right);
    checkBoolOperand(expr.operator, rightVar);
    boolean right = rightVar.getAsBoolean();
    expr.result.accept(right);
    return expr.result;
  }

  @Override
  public Variant visitUnaryExpr(Expr.Unary expr) {
    Variant result = evaluate(expr.expression);
    switch (expr.operator.type) {
      case NOT:
        checkBoolOperand(expr.operator, result);
        expr.result.accept(!result.getAsBoolean());
        break;
      case MINUS:
        checkNumberOperand(expr.operator, result);
        negateNumber(expr.result, result);
        break;
    }
    return expr.result;
  }

  @Override
  public Variant visitIdentifierExpr(Expr.Identifier expr) {
    return env.get(expr.name, expr.result);
  }

  private boolean isEqual(Token token, Variant left, Variant right) {
    if (isString(left) && isString(right)) {
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
    if (isDouble(left) && isDouble(right)) {
      return epsilonEquals(left.getAsDouble(), right.getAsDouble());
    }
    if (isDouble(left) && isLong(right)) {
      return epsilonEquals(left.getAsDouble(), right.getAsLong());
    }
    if (isLong(left) && isDouble(right)) {
      return epsilonEquals(left.getAsLong(), right.getAsDouble());
    }
    if (isLong(left) && isLong(right)) {
      return left.getAsLong() == right.getAsLong();
    }
    if (isBoolean(left) && isBoolean(right)) {
      return left.getAsBoolean() == right.getAsBoolean();
    }
    throw new RuntimeError(token, "Operands of different types cannot be compared: " + left.exprType() + " and " + right.exprType());
  }

  private boolean isGreaterNumbers(Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      return left.getAsDouble() > right.getAsDouble();
    }
    if (isDouble(left) && isLong(right)) {
      return left.getAsDouble() > right.getAsLong();
    }
    if (isLong(left) && isDouble(right)) {
      return left.getAsLong() > right.getAsDouble();
    }
    if (isLong(left) && isLong(right)) {
      return left.getAsLong() > right.getAsLong();
    }
    throw new RuntimeException("Unreachable");
  }

  private boolean isGreaterOrEqualNumbers(Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      return left.getAsDouble() >= right.getAsDouble();
    }
    if (isDouble(left) && isLong(right)) {
      return left.getAsDouble() >= right.getAsLong();
    }
    if (isLong(left) && isDouble(right)) {
      return left.getAsLong() >= right.getAsDouble();
    }
    if (isLong(left) && isLong(right)) {
      return left.getAsLong() >= right.getAsLong();
    }
    throw new RuntimeException("Unreachable");
  }

  private boolean isLessNumbers(Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      return left.getAsDouble() < right.getAsDouble();
    }
    if (isDouble(left) && isLong(right)) {
      return left.getAsDouble() < right.getAsLong();
    }
    if (isLong(left) && isDouble(right)) {
      return left.getAsLong() < right.getAsDouble();
    }
    if (isLong(left) && isLong(right)) {
      return left.getAsLong() < right.getAsLong();
    }
    throw new RuntimeException("Unreachable");
  }

  private boolean isLessOrEqualNumbers(Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      return left.getAsDouble() <= right.getAsDouble();
    }
    if (isDouble(left) && isLong(right)) {
      return left.getAsDouble() <= right.getAsLong();
    }
    if (isLong(left) && isDouble(right)) {
      return left.getAsLong() <= right.getAsDouble();
    }
    if (isLong(left) && isLong(right)) {
      return left.getAsLong() <= right.getAsLong();
    }
    throw new RuntimeException("Unreachable");
  }

  private void addNumbers(MutableVariant result, Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      result.accept(left.getAsDouble() + right.getAsDouble());
      return;
    }
    if (isDouble(left) && isLong(right)) {
      result.accept(left.getAsDouble() + right.getAsLong());
      return;
    }
    if (isLong(left) && isDouble(right)) {
      result.accept(left.getAsLong() + right.getAsDouble());
      return;
    }
    if (isLong(left) && isLong(right)) {
      result.accept(left.getAsLong() + right.getAsLong());
      return;
    }
    throw new RuntimeException("Unreachable");
  }

  private void subtractNumbers(MutableVariant result, Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      result.accept(left.getAsDouble() - right.getAsDouble());
      return;
    }
    if (isDouble(left) && isLong(right)) {
      result.accept(left.getAsDouble() - right.getAsLong());
      return;
    }
    if (isLong(left) && isDouble(right)) {
      result.accept(left.getAsLong() - right.getAsDouble());
      return;
    }
    if (isLong(left) && isLong(right)) {
      result.accept(left.getAsLong() - right.getAsLong());
      return;
    }
    throw new RuntimeException("Unreachable");
  }

  private void divideNumbers(MutableVariant result, Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      result.accept(left.getAsDouble() / right.getAsDouble());
      return;
    }
    if (isDouble(left) && isLong(right)) {
      result.accept(left.getAsDouble() / right.getAsLong());
      return;
    }
    if (isLong(left) && isDouble(right)) {
      result.accept(left.getAsLong() / right.getAsDouble());
      return;
    }
    if (isLong(left) && isLong(right)) {
      result.accept(left.getAsLong() / right.getAsLong());
      return;
    }
    throw new RuntimeException("Unreachable");
  }

  private void multiplyNumbers(MutableVariant result, Variant left, Variant right) {
    if (isDouble(left) && isDouble(right)) {
      result.accept(left.getAsDouble() * right.getAsDouble());
      return;
    }
    if (isDouble(left) && isLong(right)) {
      result.accept(left.getAsDouble() * right.getAsLong());
      return;
    }
    if (isLong(left) && isDouble(right)) {
      result.accept(left.getAsLong() * right.getAsDouble());
      return;
    }
    if (isLong(left) && isLong(right)) {
      result.accept(left.getAsLong() * right.getAsLong());
      return;
    }
    throw new RuntimeException("Unreachable");
  }

  private void modulusNumbers(MutableVariant result, Variant left, Variant right) {
    if (isLong(left) && isLong(right)) {
      result.accept(left.getAsLong() % right.getAsLong());
      return;
    }
    throw new RuntimeException("Unreachable");
  }

  private void negateNumber(MutableVariant result, Variant operand) {
    if (isDouble(operand)) {
      result.accept(-operand.getAsDouble());
      return;
    }
    if (isLong(operand)) {
      result.accept(-operand.getAsLong());
      return;
    }
    throw new RuntimeException("Unreachable");
  }

  private void checkBoolOperand(Token operator, Variant operand) {
    if (isBoolean(operand)) {
      return;
    }
    throw new RuntimeError(operator, "Operand must be a boolean");
  }

  private void checkNumberOperand(Token operator, Variant operand) {
    if (isNumber(operand)) {
      return;
    }
    throw new RuntimeError(operator, "Operand must be a number");
  }

  private void checkNumberOperands(Token operator, Variant left, Variant right) {
    if (isNumber(left) && isNumber(right)) {
      return;
    }
    throw new RuntimeError(operator, "Operands must be numbers");
  }

  private void checkLongOperands(Token operator, Variant left, Variant right) {
    if (isLong(left) && isLong(right)) {
      return;
    }
    throw new RuntimeError(operator, "Operands must be long numbers");
  }

  private void checkOperandTypes(Token operator, Variant left, Variant right) {
    if (VariantUtils.canCompare(left, right)) {
      return;
    }
    throw new RuntimeError(operator, "Operands of different types cannot be compared: " + left.exprType() + " and " + right.exprType());
  }

  private static boolean isNumber(Variant operand) {
    return isDouble(operand) || isLong(operand);
  }

  private static boolean isLong(Variant operand) {
    return operand.exprType() == ExprType.LONG;
  }

  private static boolean isDouble(Variant operand) {
    return operand.exprType() == ExprType.DOUBLE;
  }

  private static boolean isString(Variant operand) {
    return operand.exprType() == ExprType.STRING;
  }

  private static boolean isByteBuffer(Variant operand) {
    return operand.exprType() == ExprType.BYTE_BUFFER;
  }

  private static boolean isBoolean(Variant operand) {
    return operand.exprType() == ExprType.BOOL;
  }

}
