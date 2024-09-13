package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.function.Function0;
import com.tereigo.expr.function.Function1;
import com.tereigo.expr.function.Function2;
import com.tereigo.expr.function.Function3;
import com.tereigo.expr.function.Function4;
import com.tereigo.expr.function.Function5;
import com.tereigo.expr.variant.MutableVariant;
import com.tereigo.expr.variant.Variant;
import com.tereigo.expr.variant.VariantUtils;

import java.util.List;

import static com.tereigo.expr.impl.ExceptionUtils.getExceptionMsg;
import static com.tereigo.expr.variant.VariantUtils.isBoolean;
import static com.tereigo.expr.variant.VariantUtils.isExprContext;

/*
  Evaluates the expressions defined in Expr class using the provided ExprContext
 */
final class ExprInterpreter implements Expr.Visitor<Variant> {
    private final Expr expression;
    private ExprContext ctx;

    ExprInterpreter(final Expr expression) {
        this.expression = expression;
    }

    Variant evaluate(final ExprContext ctx) {
        this.ctx = ctx;
        return evaluate(expression);
    }

    private Variant evaluate(final Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Variant visitBinaryExpr(final Expr.Binary expr) {
        final Variant left = evaluate(expr.left);
        final Variant right = evaluate(expr.right);
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
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
    }

    @Override
    public Variant visitInOperator(final Expr.InOperator expr) {
        final Variant operand = evaluate(expr.operand);
        try {
            for (int i = 0; i < expr.values.size(); i++) {
                final Variant value = evaluate(expr.values.get(i));
                if (VariantUtils.isEqual(operand, value)) {
                    expr.result.accept(true);
                    return expr.result;
                }
            }
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
        expr.result.accept(false);
        return expr.result;
    }

    @Override
    public Variant visitWithinOperator(final Expr.WithinOperator expr) {
        final Variant operand = evaluate(expr.operand);
        try {
            final Variant minVal = evaluate(expr.min);
            final Variant maxVal = evaluate(expr.max);
            final Variant min = VariantUtils.min(minVal, maxVal);
            final Variant max = VariantUtils.max(minVal, maxVal);
            if (VariantUtils.isGreaterOrEqualNumbers(operand, min) && VariantUtils.isLessOrEqualNumbers(operand, max)) {
                expr.result.accept(true);
                return expr.result;
            }
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
        expr.result.accept(false);
        return expr.result;
    }

    @Override
    public Variant visitBetweenOperator(final Expr.BetweenOperator expr) {
        final Variant operand = evaluate(expr.operand);
        try {
            final Variant minVal = evaluate(expr.min);
            final Variant maxVal = evaluate(expr.max);
            final Variant min = VariantUtils.min(minVal, maxVal);
            final Variant max = VariantUtils.max(minVal, maxVal);
            if (VariantUtils.isGreaterNumbers(operand, min) && VariantUtils.isLessNumbers(operand, max)) {
                expr.result.accept(true);
                return expr.result;
            }
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
        expr.result.accept(false);
        return expr.result;
    }

    @Override
    public Variant visitGroupingExpr(final Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Variant visitLiteralExpr(final Expr.Literal expr) {
        return expr.result;
    }

    @Override
    public Variant visitLogicalExpr(final Expr.Logical expr) {
        final Variant leftVar = evaluate(expr.left);
        checkBoolOperand(expr.operator, leftVar);
        final boolean left = leftVar.getAsBoolean();
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
        final Variant rightVar = evaluate(expr.right);
        checkBoolOperand(expr.operator, rightVar);
        expr.result.accept(rightVar.getAsBoolean());
        return expr.result;
    }

    @Override
    public Variant visitTernaryExpr(final Expr.Ternary expr) {
        final Variant conditionVar = evaluate(expr.condition);
        checkBoolOperand(expr.operator, conditionVar);
        final boolean condition = conditionVar.getAsBoolean();
        return evaluate(condition ? expr.trueExpr : expr.falseExpr);
    }

    @Override
    public Variant visitUnaryExpr(final Expr.Unary expr) {
        final Variant result = evaluate(expr.expression);
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
    public Variant visitIdentifierExpr(final Expr.Identifier expr) {
        final Variant res = ctx.get(expr.operator.lexeme, expr.result);
        if (res == null) {
            throw new RuntimeError(expr.operator, "Unknown identifier '" + expr.operator.lexeme + "'");
        }
        return res;
    }

    @Override
    public Variant visitResolvedIdentifierExpr(final Expr.ResolvedIdentifier expr) {
        expr.function.call(expr.result);
        return expr.result;
    }

    @Override
    public Variant visitCallExpr(final Expr.Call expr) {
        final Object funcObj = ctx.getFunction(expr.operator.lexeme);
        return callFunction(expr.result, expr.operator, funcObj, expr.args);
    }

    @Override
    public Variant visitResolvedCallExpr(final Expr.ResolvedCall expr) {
        return callFunction(expr.result, expr.operator, expr.function, expr.args);
    }

    @Override
    public Variant visitObjectCallExpr(final Expr.ObjectCall expr) {
        final Variant objResult = evaluate(expr.object);
        // if it's an object call from ExprContext
        if (isExprContext(objResult)) {
            // then fetch the function from that ExprContext
            final Object funcObj = objResult.getAsExprContext().getFunction(expr.operator.lexeme);
            return callFunction(expr.result, expr.operator, funcObj, expr.args);
        }
        // otherwise it's a normal/native function call -> get the function from the global context
        final Object funcObj = ctx.getFunction(expr.operator.lexeme);
        if (funcObj == null) {
            throw new RuntimeError(expr.operator, "Unknown function '" + expr.operator.lexeme + "'");
        }

        try {
            // expr.args.size()+1 - because we add the resolved "this" as a second parameter (objResult)
            switch (expr.args.size() + 1) {
                case 1:
                    ((Function1) funcObj).call(expr.result, objResult);
                    break;
                case 2:
                    ((Function2) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)));
                    break;
                case 3:
                    ((Function3) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)));
                    break;
                case 4:
                    ((Function4) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)), evaluate(expr.args.get(2)));
                    break;
                case 5:
                    ((Function5) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)), evaluate(expr.args.get(2)), evaluate(expr.args.get(3)));
                    break;
            }
        } catch (final ClassCastException castEx) {
            throw new RuntimeError(expr.operator, "ClassCastException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(castEx));
        } catch (final RuntimeException runtimeEx) {
            throw new RuntimeError(expr.operator, "RuntimeException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(runtimeEx));
        }
        return expr.result;
    }

    @Override
    public Variant visitResolvedObjectCallExpr(final Expr.ResolvedObjectCall expr) {
        final Variant objResult = evaluate(expr.object);
        // if it's an object call from ExprContext
        if (isExprContext(objResult)) {
            return callFunction(expr.result, expr.operator, expr.function, expr.args);
        }
        // otherwise it's a normal/native function call -> get the function from the global context
        final Object funcObj = expr.function;
        try {
            // expr.args.size()+1 - because we add the resolved "this" as a second parameter (objResult)
            switch (expr.args.size() + 1) {
                case 1:
                    ((Function1) funcObj).call(expr.result, objResult);
                    break;
                case 2:
                    ((Function2) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)));
                    break;
                case 3:
                    ((Function3) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)));
                    break;
                case 4:
                    ((Function4) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)), evaluate(expr.args.get(2)));
                    break;
                case 5:
                    ((Function5) funcObj).call(expr.result, objResult, evaluate(expr.args.get(0)), evaluate(expr.args.get(1)), evaluate(expr.args.get(2)), evaluate(expr.args.get(3)));
                    break;
            }
        } catch (final ClassCastException castEx) {
            throw new RuntimeError(expr.operator, "ClassCastException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(castEx));
        } catch (final RuntimeException runtimeEx) {
            throw new RuntimeError(expr.operator, "RuntimeException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(runtimeEx));
        }
        return expr.result;
    }

    private Variant callFunction(final MutableVariant result, final Token token, final Object funcObj, final List<Expr> args) {
        if (funcObj == null) {
            throw new RuntimeError(token, "Unknown function '" + token.lexeme + "'");
        }
        try {
            // cast to the appropriate function type depending on the number of args, evaluate all arguments and call the function
            switch (args.size()) {
                case 0:
                    ((Function0) funcObj).call(result);
                    break;
                case 1:
                    ((Function1) funcObj).call(result, evaluate(args.get(0)));
                    break;
                case 2:
                    ((Function2) funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1)));
                    break;
                case 3:
                    ((Function3) funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1)), evaluate(args.get(2)));
                    break;
                case 4:
                    ((Function4) funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1)), evaluate(args.get(2)), evaluate(args.get(3)));
                    break;
                case 5:
                    ((Function5) funcObj).call(result, evaluate(args.get(0)), evaluate(args.get(1)), evaluate(args.get(2)), evaluate(args.get(3)), evaluate(args.get(4)));
                    break;
            }
        } catch (final ClassCastException castEx) {
            throw new RuntimeError(token, "ClassCastException in function '" + token.lexeme + "': " + getExceptionMsg(castEx));
        } catch (final RuntimeException runtimeEx) {
            throw new RuntimeError(token, "RuntimeException in function '" + token.lexeme + "': " + getExceptionMsg(runtimeEx));
        }
        return result;
    }

    private void negateNumber(final Token token, final MutableVariant result, final Variant operand) {
        try {
            VariantUtils.negateNumber(result, operand);
        } catch (final RuntimeException ex) {
            throw new RuntimeError(token, getExceptionMsg(ex), ex);
        }
    }

    private void checkBoolOperand(final Token operator, final Variant operand) {
        if (isBoolean(operand)) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a boolean");
    }

}
