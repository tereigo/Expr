package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils;
import com.tereigo.atlas_expr.function.Function0;
import com.tereigo.atlas_expr.function.Function1;
import com.tereigo.atlas_expr.function.Function2;
import com.tereigo.atlas_expr.function.Function3;
import com.tereigo.atlas_expr.function.Function4;
import com.tereigo.atlas_expr.function.Function5;
import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.variant.Variant;

import java.util.List;

import static com.tereigo.atlas_expr.atlas.utils.AlgoUtils.epsilonEquals;
import static com.tereigo.atlas_expr.variant.VariantUtils.isBoolean;
import static com.tereigo.atlas_expr.variant.VariantUtils.isByteBuffer;
import static com.tereigo.atlas_expr.variant.VariantUtils.isDouble;
import static com.tereigo.atlas_expr.variant.VariantUtils.isExprContext;
import static com.tereigo.atlas_expr.variant.VariantUtils.isLong;
import static com.tereigo.atlas_expr.variant.VariantUtils.isString;

/*
  Evaluates the expressions defined in Expr class using the provided ExprContext
 */
final class Interpreter implements Expr.Visitor<Variant> {
  private final Expr expression;
  private final ExprContextCombined ctx = new ExprContextCombined();

  Interpreter(final Expr expression) {
    this.expression = expression;
  }

  Variant evaluate() {
    this.ctx.init(ExprContextNative.INSTANCE);
    return evaluate(expression);
  }

  Variant evaluate(final ExprContext ctx) {
    this.ctx.init(ExprContextNative.INSTANCE, ctx);
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
        expr.result.accept(isGreaterNumbers(expr.operator, left, right));
        break;
      case GREATER_EQUAL:
        expr.result.accept(isGreaterOrEqualNumbers(expr.operator, left, right));
        break;
      case LESS:
        expr.result.accept(isLessNumbers(expr.operator, left, right));
        break;
      case LESS_EQUAL:
        expr.result.accept(isLessOrEqualNumbers(expr.operator, left, right));
        break;
      case MINUS:
        subtractNumbers(expr.operator, expr.result, left, right);
        break;
      case PLUS:
        addNumbers(expr.operator, expr.result, left, right);
        break;
        // NOTICE: We don't allow String concatenation because it produces garbage
//        if (isString(left) && isString(right)) {
//          expr.result.accept(left.getAsString() + right.getAsString());
//          break;
//        }
//        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
      case DIV:
        divideNumbers(expr.operator, expr.result, left, right);
        break;
      case MUL:
        multiplyNumbers(expr.operator, expr.result, left, right);
        break;
      case MODULUS:
        modulusNumbers(expr.operator, expr.result, left, right);
        break;
    }
    return expr.result;
  }

  @Override
  public Variant visitInOperator(Expr.InOperator expr) {
    Variant operand = evaluate(expr.operand);
    for (int i = 0; i < expr.values.size(); i++) {
      if (isEqual(expr.operator, operand, expr.values.get(i))) {
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
      throw new RuntimeError(expr.operator, "Unexpected logical expression type: " + expr.operator.type);
    }
    Variant rightVar = evaluate(expr.right);
    checkBoolOperand(expr.operator, rightVar);
    boolean right = rightVar.getAsBoolean();
    expr.result.accept(right);
    return expr.result;
  }

  @Override
  public Variant visitTernaryExpr(Expr.Ternary expr) {
    Variant conditionVar = evaluate(expr.condition);
    checkBoolOperand(expr.operator, conditionVar);
    final boolean condition = conditionVar.getAsBoolean();
    return evaluate(condition ? expr.trueExpr : expr.falseExpr);
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
        negateNumber(expr.operator, expr.result, result);
        break;
    }
    return expr.result;
  }

  @Override
  public Variant visitIdentifierExpr(Expr.Identifier expr) {
    MutableVariant res = ctx.get(expr.operator.lexeme, expr.result);
    if (res == null) {
      throw new RuntimeError(expr.operator, "Unknown identifier '" + expr.operator.lexeme + "'");
    }
    return res;
  }

  @Override
  public Variant visitCallExpr(Expr.Call expr) {
    Object funcObj = ctx.getFunction(expr.operator.lexeme);
    return callFunction(expr.result, expr.operator, funcObj, expr.args);
  }

  @Override
  public Variant visitObjectCallExpr(Expr.ObjectCall expr) {
    Variant objResult = evaluate(expr.object);
    // if it's an object call from ExprContext
    if (isExprContext(objResult)) {
      // then fetch the function from that ExprContext
      Object funcObj = objResult.getAsExprContext().getFunction(expr.operator.lexeme);
      return callFunction(expr.result, expr.operator, funcObj, expr.args);
    }
    // otherwise it's a normal/native function call -> get the function from the global context
    Object funcObj = ctx.getFunction(expr.operator.lexeme);
    if (funcObj == null) {
      throw new RuntimeError(expr.operator, "Unknown function '" + expr.operator.lexeme + "'");
    }

    try {
      // expr.args.size()+1 - because we add the resolved "this" as a second parameter (objResult)
      switch (expr.args.size() + 1) {
        case 1: ((Function1)funcObj).call(expr.result, objResult); break;
        case 2: ((Function2)funcObj).call(expr.result, objResult, evaluate(expr.args.get(0))); break;
        case 3: ((Function3)funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1))); break;
        case 4: ((Function4)funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)), evaluate(expr.args.get(2))); break;
        case 5: ((Function5)funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)), evaluate(expr.args.get(2)), evaluate(expr.args.get(3))); break;
      }
    } catch (ClassCastException castEx) {
      throw new RuntimeError(expr.operator, "ClassCastException in function '" + expr.operator.lexeme + "': " + castEx.getMessage());
    }
    catch (RuntimeException runtimeEx) {
      throw new RuntimeError(expr.operator, "RuntimeException in function '" + expr.operator.lexeme + "': " + runtimeEx.getMessage());
    }
    return expr.result;
  }

  private Variant callFunction(MutableVariant result, Token token, Object funcObj, List<Expr> args) {
    if (funcObj == null) {
      throw new RuntimeError(token, "Unknown function '" + token.lexeme + "'");
    }
    try {
      // cast to the appropriate function type depending on the number of args, evaluate all arguments and call the function
      switch (args.size()) {
        case 0: ((Function0)funcObj).call(result); break;
        case 1: ((Function1)funcObj).call(result, evaluate(args.get(0))); break;
        case 2: ((Function2)funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1))); break;
        case 3: ((Function3)funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1)), evaluate(args.get(2))); break;
        case 4: ((Function4)funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1)), evaluate(args.get(2)), evaluate(args.get(3))); break;
        case 5: ((Function5)funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1)), evaluate(args.get(2)), evaluate(args.get(3)), evaluate(args.get(4))); break;
      }
    } catch (ClassCastException castEx) {
      throw new RuntimeError(token, "ClassCastException in function '" + token.lexeme + "': " + castEx.getMessage());
    }
    catch (RuntimeException runtimeEx) {
      throw new RuntimeError(token, "RuntimeException in function '" + token.lexeme + "': " + runtimeEx.getMessage());
    }
    return result;
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

  private boolean isGreaterNumbers(Token token, Variant left, Variant right) {
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
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private boolean isGreaterOrEqualNumbers(Token token, Variant left, Variant right) {
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
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private boolean isLessNumbers(Token token, Variant left, Variant right) {
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
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private boolean isLessOrEqualNumbers(Token token, Variant left, Variant right) {
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
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private void addNumbers(Token token, MutableVariant result, Variant left, Variant right) {
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
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private void subtractNumbers(Token token, MutableVariant result, Variant left, Variant right) {
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
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private void divideNumbers(Token token, MutableVariant result, Variant left, Variant right) {
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
      if (right.getAsLong() == 0) {
        throw new RuntimeError(token, "Division by zero");
      }
      result.accept(left.getAsLong() / right.getAsLong());
      return;
    }
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private void multiplyNumbers(Token token, MutableVariant result, Variant left, Variant right) {
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
    throw new RuntimeError(token, "Operands must be numbers");
  }

  private void modulusNumbers(Token token, MutableVariant result, Variant left, Variant right) {
    if (isLong(left) && isLong(right)) {
      result.accept(left.getAsLong() % right.getAsLong());
      return;
    }
    throw new RuntimeError(token, "Operands must be long numbers");
  }

  private void negateNumber(Token token, MutableVariant result, Variant operand) {
    if (isDouble(operand)) {
      result.accept(-operand.getAsDouble());
      return;
    }
    if (isLong(operand)) {
      result.accept(-operand.getAsLong());
      return;
    }
    throw new RuntimeError(token, "Operand must be a number");
  }

  private void checkBoolOperand(Token operator, Variant operand) {
    if (isBoolean(operand)) {
      return;
    }
    throw new RuntimeError(operator, "Operand must be a boolean");
  }

}
