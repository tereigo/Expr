package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.function.Function0;
import com.tereigo.atlas_expr.function.Function1;
import com.tereigo.atlas_expr.function.Function2;
import com.tereigo.atlas_expr.function.Function3;
import com.tereigo.atlas_expr.function.Function4;
import com.tereigo.atlas_expr.function.Function5;
import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.variant.Variant;
import com.tereigo.atlas_expr.variant.VariantUtils;

import java.util.List;

import static com.tereigo.atlas_expr.ExceptionUtils.getExceptionMsg;
import static com.tereigo.atlas_expr.variant.VariantUtils.isBoolean;
import static com.tereigo.atlas_expr.variant.VariantUtils.isExprContext;

/*
  Evaluates the expressions defined in Expr class using the provided ExprContext
 */
final class ExprInterpreter implements Expr.Visitor<Variant> {
  private final Expr expression;
  private final ExprContextCombined ctx = new ExprContextCombined();

  ExprInterpreter(final Expr expression) {
    this.expression = expression;
  }

  Variant evaluate() {
    this.ctx.init(ExprContextNative.get());
    return evaluate(expression);
  }

  Variant evaluate(final ExprContext ctx) {
    this.ctx.init(ExprContextNative.get(), ctx);
    return evaluate(expression);
  }

  private Variant evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Variant visitBinaryExpr(Expr.Binary expr) {
    Variant left = evaluate(expr.left);
    Variant right = evaluate(expr.right);

    try {
      switch (expr.operator.type) {
        case EQUAL_EQUAL:
          expr.result.accept(VariantUtils.isEqual(left, right));
          break;
        case NOT_EQUAL:
          expr.result.accept(!VariantUtils.isEqual(left, right));
          break;
        case GREATER:
          expr.result.accept(VariantUtils.isGreaterNumbers(left, right));
          break;
        case GREATER_EQUAL:
          expr.result.accept(VariantUtils.isGreaterOrEqualNumbers(left, right));
          break;
        case LESS:
          expr.result.accept(VariantUtils.isLessNumbers(left, right));
          break;
        case LESS_EQUAL:
          expr.result.accept(VariantUtils.isLessOrEqualNumbers(left, right));
          break;
        case MINUS:
          VariantUtils.subtractNumbers(expr.result, left, right);
          break;
        case PLUS:
          VariantUtils.addNumbers(expr.result, left, right);
          break;
        // NOTICE: We don't allow String concatenation because it produces garbage
//        if (isString(left) && isString(right)) {
//          expr.result.accept(left.getAsString() + right.getAsString());
//          break;
//        }
//        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
        case DIV:
          VariantUtils.divideNumbers(expr.result, left, right);
          break;
        case MUL:
          VariantUtils.multiplyNumbers(expr.result, left, right);
          break;
        case MODULUS:
          VariantUtils.modulusNumbers(expr.result, left, right);
          break;
      }
      return expr.result;
    } catch (RuntimeException ex) {
      throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
    }
  }

  @Override
  public Variant visitInOperator(Expr.InOperator expr) {
    Variant operand = evaluate(expr.operand);
    try {
      for (int i = 0; i < expr.values.size(); i++) {
        if (VariantUtils.isEqual(operand, expr.values.get(i))) {
          expr.result.accept(true);
          return expr.result;
        }
      }
    } catch (RuntimeException ex) {
      throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
    }
    expr.result.accept(false);
    return expr.result;
  }

  @Override
  public Variant visitWithinOperator(Expr.WithinOperator expr) {
    Variant operand = evaluate(expr.operand);
    try {
      final Variant minVal = evaluate(expr.min);
      final Variant maxVal = evaluate(expr.max);
      final Variant min = VariantUtils.min(minVal, maxVal);
      final Variant max = VariantUtils.max(minVal, maxVal);
      if (VariantUtils.isGreaterOrEqualNumbers(operand, min) && VariantUtils.isLessOrEqualNumbers(operand, max)) {
        expr.result.accept(true);
        return expr.result;
      }
    } catch (RuntimeException ex) {
      throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
    }
    expr.result.accept(false);
    return expr.result;
  }

  @Override
  public Variant visitBetweenOperator(Expr.BetweenOperator expr) {
    Variant operand = evaluate(expr.operand);
    try {
      final Variant minVal = evaluate(expr.min);
      final Variant maxVal = evaluate(expr.max);
      final Variant min = VariantUtils.min(minVal, maxVal);
      final Variant max = VariantUtils.max(minVal, maxVal);
      if (VariantUtils.isGreaterNumbers(operand, min) && VariantUtils.isLessNumbers(operand, max)) {
        expr.result.accept(true);
        return expr.result;
      }
    } catch (RuntimeException ex) {
      throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
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
      throw new RuntimeError(expr.operator, "ClassCastException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(castEx));
    }
    catch (RuntimeException runtimeEx) {
      throw new RuntimeError(expr.operator, "RuntimeException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(runtimeEx));
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
      throw new RuntimeError(token, "ClassCastException in function '" + token.lexeme + "': " + getExceptionMsg(castEx));
    } catch (RuntimeException runtimeEx) {
      throw new RuntimeError(token, "RuntimeException in function '" + token.lexeme + "': " + getExceptionMsg(runtimeEx));
    }
    return result;
  }

  private void negateNumber(Token token, MutableVariant result, Variant operand) {
    try {
      VariantUtils.negateNumber(result, operand);
    } catch (RuntimeException ex) {
      throw new RuntimeError(token, getExceptionMsg(ex), ex);
    }
  }

  private void checkBoolOperand(Token operator, Variant operand) {
    if (isBoolean(operand)) {
      return;
    }
    throw new RuntimeError(operator, "Operand must be a boolean");
  }

}
