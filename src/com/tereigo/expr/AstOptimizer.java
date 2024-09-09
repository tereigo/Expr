package com.tereigo.expr;

import com.tereigo.expr.function.Function0;

import java.util.ArrayList;
import java.util.List;

/**
 * AST tree optimization for the case when ExprContext is known at startup and it doesn't change for evaluation
 * We traverse AST tree with a given static context and resolve functions and identifiers and store them as resolved functions
 * In that case we don't have to do function lookups by name during evaluation
 */
final class AstOptimizer implements Expr.Visitor<Expr> {
  private final ExprContext ctx;

  AstOptimizer(ExprContext ctx) {
    this.ctx = ctx;
  }

  ASTRoot optimize(ASTRoot root) {
    return new ASTRoot(root.source(), root.expr().accept(this));
  }

  private Expr evaluate(final Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Expr visitBinaryExpr(Expr.Binary expr) {
    return new Expr.Binary(evaluate(expr.left), expr.operator, evaluate(expr.right));
  }

  @Override
  public Expr visitInOperator(Expr.InOperator expr) {
    return new Expr.InOperator(evaluate(expr.operand), expr.operator, convertArgs(expr.values));
  }

  @Override
  public Expr visitWithinOperator(Expr.WithinOperator expr) {
    return new Expr.WithinOperator(evaluate(expr.operand), expr.operator, evaluate(expr.min), evaluate(expr.max));
  }

  @Override
  public Expr visitBetweenOperator(Expr.BetweenOperator expr) {
    return new Expr.BetweenOperator(evaluate(expr.operand), expr.operator, evaluate(expr.min), evaluate(expr.max));
  }

  @Override
  public Expr visitGroupingExpr(Expr.Grouping expr) {
    // TODO: it feels like we can we return expr.expression here
    // This way we'll remove Grouping from AST
    return expr;
  }

  @Override
  public Expr visitLiteralExpr(Expr.Literal expr) {
    return expr;
  }

  @Override
  public Expr visitLogicalExpr(Expr.Logical expr) {
    return new Expr.Logical(evaluate(expr.left), expr.operator, evaluate(expr.right));
  }

  @Override
  public Expr visitTernaryExpr(Expr.Ternary expr) {
    return new Expr.Ternary(expr.operator, evaluate(expr.condition), evaluate(expr.trueExpr), evaluate(expr.falseExpr));
  }

  @Override
  public Expr visitUnaryExpr(Expr.Unary expr) {
    return new Expr.Unary(expr.operator, evaluate(expr.expression));
  }

  @Override
  public Expr visitIdentifierExpr(Expr.Identifier expr) {
    final Object funcObj = ctx.getFunction(expr.operator.lexeme);
    if (funcObj instanceof Function0) {
      return new Expr.ResolvedIdentifier(expr.operator, (Function0)funcObj);
    }
    // in theory this should never happen
    // but if the optimization fails then just keep the original expression
    return expr;
  }

  @Override
  public Expr visitResolvedIdentifierExpr(Expr.ResolvedIdentifier expr) {
    // in theory this should never be called
    return expr;
  }

  @Override
  public Expr visitCallExpr(Expr.Call expr) {
    final Object funcObj = ctx.getFunction(expr.operator.lexeme);
    if (funcObj != null) {
      return new Expr.ResolvedCall(expr.operator, funcObj, convertArgs(expr.args));
    }
    // in theory this should never happen
    // but if the optimization fails then just keep the original expression
    return expr;
  }

  @Override
  public Expr visitResolvedCallExpr(Expr.ResolvedCall expr) {
    // in theory this should never be called
    return expr;
  }

  @Override
  public Expr visitObjectCallExpr(Expr.ObjectCall expr) {
    final Expr objResult = evaluate(expr.object);
    // if it's an object call from ExprContext
    if (objResult instanceof Expr.ResolvedIdentifier) {
      ((Expr.ResolvedIdentifier)objResult).function.call(expr.result);
      if (expr.result.exprType() != ExprType.EXPR_CONTEXT) {
        // in theory this should never happen
        // but if the optimization fails then just keep the original expression
        return expr;
      }
      final Object funcObj = expr.result.getAsExprContext().getFunction(expr.operator.lexeme);
      if (funcObj == null) {
        // if the optimization fails then just keep the original expression
        return expr;
      }
      return new Expr.ResolvedObjectCall(objResult, expr.operator, funcObj, convertArgs(expr.args));
    }
    // otherwise it's a normal/native function call -> get the function from the global context
    final Object funcObj = ctx.getFunction(expr.operator.lexeme);
    if (funcObj == null) {
      // if the optimization fails then just keep the original expression
      return expr;
    }
    return new Expr.ResolvedObjectCall(objResult, expr.operator, funcObj, convertArgs(expr.args));
  }

  @Override
  public Expr visitResolvedObjectCallExpr(Expr.ResolvedObjectCall expr) {
    // in theory this should never be called
    return expr;
  }

  private List<Expr> convertArgs(final List<Expr> args) {
    final List<Expr> values = new ArrayList<>();
    for (int i = 0; i < args.size(); i++) {
      values.add(evaluate(args.get(i)));
    }
    return values;
  }
}
