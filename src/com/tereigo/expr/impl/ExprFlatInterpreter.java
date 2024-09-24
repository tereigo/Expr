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

import static com.tereigo.expr.impl.ExceptionUtils.getExceptionMsg;
import static com.tereigo.expr.variant.VariantUtils.isBoolean;
import static com.tereigo.expr.variant.VariantUtils.isExprContext;

/*
  Evaluates the expressions defined in FlatExpr class using the provided ExprContext
 */
final class ExprFlatInterpreter implements FlatExpr.Visitor<Variant> {
    private final FlatAST flatAST;
    private ExprContext ctx;

    ExprFlatInterpreter(final FlatAST flatAST) {
        this.flatAST = flatAST;
    }

    Variant evaluate(final ExprContext ctx) {
        this.ctx = ctx;
        return evaluate(flatAST.getNode((short)0));
    }

    private Variant evaluate(final FlatExpr expr) {
        return expr.accept(this);
    }

    @Override
    public Variant visitBinaryExpr(final FlatExpr.Binary expr) {
        final Variant left = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        final Variant right = evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1)));
        final MutableVariant result = flatAST.getResult(expr.pos);
        try {
            switch (expr.operator.type) {
                case EQUAL_EQUAL:
                    result.accept(VariantUtils.isEqual(left, right));
                    break;
                case NOT_EQUAL:
                    result.accept(!VariantUtils.isEqual(left, right));
                    break;
                case GREATER:
                    result.accept(VariantUtils.isGreaterNumbers(left, right));
                    break;
                case GREATER_EQUAL:
                    result.accept(VariantUtils.isGreaterOrEqualNumbers(left, right));
                    break;
                case LESS:
                    result.accept(VariantUtils.isLessNumbers(left, right));
                    break;
                case LESS_EQUAL:
                    result.accept(VariantUtils.isLessOrEqualNumbers(left, right));
                    break;
                case MINUS:
                    VariantUtils.subtractNumbers(result, left, right);
                    break;
                case PLUS:
                    VariantUtils.addNumbers(result, left, right);
                    break;
                // NOTICE: We don't allow String concatenation because it produces garbage
                //  if (isString(left) && isString(right)) {
                //    expr.result.accept(left.getAsString() + right.getAsString());
                //    break;
                //  }
                //  throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
                case DIV:
                    VariantUtils.divideNumbers(result, left, right);
                    break;
                case MUL:
                    VariantUtils.multiplyNumbers(result, left, right);
                    break;
                case MODULUS:
                    VariantUtils.modulusNumbers(result, left, right);
                    break;
            }
            return result;
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
    }

    @Override
    public Variant visitInOperator(final FlatExpr.InOperator expr) {
        final Variant operand = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        final MutableVariant result = flatAST.getResult(expr.pos);
        try {
            for (int i = 1; i < flatAST.getNumChildren()[expr.pos]; i++) {
                final Variant value = evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + i)));
                if (VariantUtils.isEqual(operand, value)) {
                    result.accept(true);
                    return  result;
                }
            }
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
        result.accept(false);
        return result;
    }

    @Override
    public Variant visitWithinOperator(final FlatExpr.WithinOperator expr) {
        final Variant operand = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        final MutableVariant result = flatAST.getResult(expr.pos);
        try {
            final Variant minVal = evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1)));
            final Variant maxVal = evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2)));
            final Variant min = VariantUtils.min(minVal, maxVal);
            final Variant max = VariantUtils.max(minVal, maxVal);
            if (VariantUtils.isGreaterOrEqualNumbers(operand, min) && VariantUtils.isLessOrEqualNumbers(operand, max)) {
                result.accept(true);
                return result;
            }
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
        result.accept(false);
        return result;
    }

    @Override
    public Variant visitBetweenOperator(final FlatExpr.BetweenOperator expr) {
        final Variant operand = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        final MutableVariant result = flatAST.getResult(expr.pos);
        try {
            final Variant minVal = evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1)));
            final Variant maxVal = evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2)));
            final Variant min = VariantUtils.min(minVal, maxVal);
            final Variant max = VariantUtils.max(minVal, maxVal);
            if (VariantUtils.isGreaterNumbers(operand, min) && VariantUtils.isLessNumbers(operand, max)) {
                result.accept(true);
                return result;
            }
        } catch (final RuntimeException ex) {
            throw new RuntimeError(expr.operator, getExceptionMsg(ex), ex);
        }
        result.accept(false);
        return result;
    }

    @Override
    public Variant visitGroupingExpr(final FlatExpr.Grouping expr) {
        return evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
    }

    @Override
    public Variant visitLiteralExpr(final FlatExpr.Literal expr) {
        return flatAST.getResult(expr.pos);
    }

    @Override
    public Variant visitLogicalExpr(final FlatExpr.Logical expr) {
        final Variant leftVar = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        final MutableVariant result = flatAST.getResult(expr.pos);
        checkBoolOperand(expr.operator, leftVar);
        final boolean left = leftVar.getAsBoolean();
        if (expr.operator.type == TokenType.OR) {
            if (left) {
                result.accept(true);
                return result;
            }
        } else if (expr.operator.type == TokenType.AND) {
            if (!left) {
                result.accept(false);
                return result;
            }
        } else {
            throw new RuntimeError(expr.operator, "Unexpected logical expression type: " + expr.operator.type);
        }
        final Variant rightVar = evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1)));;
        checkBoolOperand(expr.operator, rightVar);
        result.accept(rightVar.getAsBoolean());
        return result;
    }

    @Override
    public Variant visitTernaryExpr(final FlatExpr.Ternary expr) {
        final Variant conditionVar = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        checkBoolOperand(expr.operator, conditionVar);
        final boolean condition = conditionVar.getAsBoolean();
        return evaluate(condition ? flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1)) : flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2)));
    }

    @Override
    public Variant visitUnaryExpr(final FlatExpr.Unary expr) {
        final Variant result = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        final MutableVariant res = flatAST.getResult(expr.pos);
        switch (expr.operator.type) {
            case NOT:
                checkBoolOperand(expr.operator, result);
                res.accept(!result.getAsBoolean());
                break;
            case MINUS:
                negateNumber(expr.operator, res, result);
                break;
        }
        return res;
    }

    @Override
    public Variant visitIdentifierExpr(final FlatExpr.Identifier expr) {
        final MutableVariant result = flatAST.getResult(expr.pos);
        final Variant res = ctx.get(expr.operator.lexeme, result);
        if (res == null) {
            throw new RuntimeError(expr.operator, "Unknown identifier '" + expr.operator.lexeme + "'");
        }
        return res;
    }

    @Override
    public Variant visitResolvedIdentifierExpr(final FlatExpr.ResolvedIdentifier expr) {
        final MutableVariant result = flatAST.getResult(expr.pos);
        expr.function.call(result);
        return result;
    }

    @Override
    public Variant visitCallExpr(final FlatExpr.Call expr) {
        final MutableVariant result = flatAST.getResult(expr.pos);
        final Object funcObj = ctx.getFunction(expr.operator.lexeme);
        return callFunction(result, expr.operator, funcObj, flatAST.getNodes(), flatAST.getStartChild()[expr.pos], flatAST.getNumChildren()[expr.pos]);
    }

    @Override
    public Variant visitResolvedCallExpr(final FlatExpr.ResolvedCall expr) {
        final MutableVariant result = flatAST.getResult(expr.pos);
        return callFunction(result, expr.operator, expr.function, flatAST.getNodes(), flatAST.getStartChild()[expr.pos], flatAST.getNumChildren()[expr.pos]);
    }

    @Override
    public Variant visitObjectCallExpr(final FlatExpr.ObjectCall expr) {
        final MutableVariant result = flatAST.getResult(expr.pos);
        final Variant objResult = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        // if it's an object call from ExprContext
        if (isExprContext(objResult)) {
            // then fetch the function from that ExprContext
            final Object funcObj = objResult.getAsExprContext().getFunction(expr.operator.lexeme);
            return callFunction(result, expr.operator, funcObj, flatAST.getNodes(), (short)(flatAST.getStartChild()[expr.pos] + 1), (byte)(flatAST.getNumChildren()[expr.pos] - 1));
        }
        // otherwise it's a normal/native function call -> get the function from the global context
        final Object funcObj = ctx.getFunction(expr.operator.lexeme);
        if (funcObj == null) {
            throw new RuntimeError(expr.operator, "Unknown function '" + expr.operator.lexeme + "'");
        }

        try {
            // expr.args.size()+1 - because we add the resolved "this" as a second parameter (objResult)
            switch (flatAST.getNumChildren()[expr.pos]) {
                case 1:
                    ((Function1) funcObj).call(result, objResult);
                    break;
                case 2:
                    ((Function2) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))));
                    break;
                case 3:
                    ((Function3) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2))));
                    break;
                case 4:
                    ((Function4) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 3))));
                    break;
                case 5:
                    ((Function5) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 3))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 4))));
                    break;
            }
        } catch (final ClassCastException castEx) {
            throw new RuntimeError(expr.operator, "ClassCastException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(castEx));
        } catch (final RuntimeException runtimeEx) {
            throw new RuntimeError(expr.operator, "RuntimeException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(runtimeEx));
        }
        return result;
    }

    @Override
    public Variant visitResolvedObjectCallExpr(final FlatExpr.ResolvedObjectCall expr) {
        final MutableVariant result = flatAST.getResult(expr.pos);
        final Variant objResult = evaluate(flatAST.getNode(flatAST.getStartChild()[expr.pos]));
        // if it's an object call from ExprContext
        if (isExprContext(objResult)) {
            return callFunction(result, expr.operator, expr.function, flatAST.getNodes(), (short)(flatAST.getStartChild()[expr.pos] + 1), (byte)(flatAST.getNumChildren()[expr.pos] - 1));
        }
        // otherwise it's a normal/native function call -> get the function from the global context
        final Object funcObj = expr.function;
        try {
            // expr.args.size()+1 - because we add the resolved "this" as a second parameter (objResult)
            switch (flatAST.getNumChildren()[expr.pos]) {
                case 1:
                    ((Function1) funcObj).call(result, objResult);
                    break;
                case 2:
                    ((Function2) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))));
                    break;
                case 3:
                    ((Function3) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2))));
                    break;
                case 4:
                    ((Function4) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 3))));
                    break;
                case 5:
                    ((Function5) funcObj).call(result, objResult, evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 1))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 2))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 3))), evaluate(flatAST.getNode((short)(flatAST.getStartChild()[expr.pos] + 4))));
                    break;
            }
        } catch (final ClassCastException castEx) {
            throw new RuntimeError(expr.operator, "ClassCastException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(castEx));
        } catch (final RuntimeException runtimeEx) {
            throw new RuntimeError(expr.operator, "RuntimeException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(runtimeEx));
        }
        return result;
    }

    private Variant callFunction(final MutableVariant result, final Token token, final Object funcObj, final FlatExpr[] args, final short startArg, final byte numArgs) {
        if (funcObj == null) {
            throw new RuntimeError(token, "Unknown function '" + token.lexeme + "'");
        }
        try {
            // cast to the appropriate function type depending on the number of args, evaluate all arguments and call the function
            switch (numArgs) {
                case 0:
                    ((Function0) funcObj).call(result);
                    break;
                case 1:
                    ((Function1) funcObj).call(result, evaluate(args[startArg + 0]));
                    break;
                case 2:
                    ((Function2) funcObj).call(result, evaluate(args[startArg + 0]), evaluate(args[startArg + 1]));
                    break;
                case 3:
                    ((Function3) funcObj).call(result, evaluate(args[startArg + 0]), evaluate(args[startArg + 1]), evaluate(args[startArg + 2]));
                    break;
                case 4:
                    ((Function4) funcObj).call(result, evaluate(args[startArg + 0]), evaluate(args[startArg + 1]), evaluate(args[startArg + 2]), evaluate(args[startArg + 3]));
                    break;
                case 5:
                    ((Function5) funcObj).call(result, evaluate(args[startArg + 0]), evaluate(args[startArg + 1]), evaluate(args[startArg + 2]), evaluate(args[startArg + 3]), evaluate(args[startArg + 4]));
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
