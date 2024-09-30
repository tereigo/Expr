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
        return evaluate(flatAST.getNodes()[(short)0]);
    }

    private Variant evaluate(final FlatExpr expr) {
        return expr.accept(this);
    }

    @Override
    public Variant visitBinaryExpr(final FlatExpr.Binary expr) {
        final short childPos = expr.startChild;
        final Variant left = evaluate(flatAST.getNodes()[childPos]);
        final Variant right = evaluate(flatAST.getNodes()[(short)(childPos + 1)]);
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
                //  if (isString(left) && isString(right)) {
                //    expr.result.accept(left.getAsString() + right.getAsString());
                //    break;
                //  }
                //  throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
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
    public Variant visitInOperator(final FlatExpr.InOperator expr) {
        final short childPos = expr.startChild;
        final Variant operand = evaluate(flatAST.getNodes()[childPos]);
        try {
            for (int i = 1; i < expr.numChildren; i++) {
                final Variant value = evaluate(flatAST.getNodes()[(short)(childPos + i)]);
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
    public Variant visitWithinOperator(final FlatExpr.WithinOperator expr) {
        final short childPos = expr.startChild;
        final Variant operand = evaluate(flatAST.getNodes()[childPos]);
        try {
            final Variant minVal = evaluate(flatAST.getNodes()[(short)(childPos + 1)]);
            final Variant maxVal = evaluate(flatAST.getNodes()[(short)(childPos + 2)]);
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
    public Variant visitBetweenOperator(final FlatExpr.BetweenOperator expr) {
        final short childPos = expr.startChild;
        final Variant operand = evaluate(flatAST.getNodes()[childPos]);
        try {
            final Variant minVal = evaluate(flatAST.getNodes()[(short)(childPos + 1)]);
            final Variant maxVal = evaluate(flatAST.getNodes()[(short)(childPos + 2)]);
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
    public Variant visitLiteralExpr(final FlatExpr.Literal expr) {
        return expr.result;
    }

    @Override
    public Variant visitLogicalExpr(final FlatExpr.Logical expr) {
        final short childPos = expr.startChild;
        final Variant leftVar = evaluate(flatAST.getNodes()[childPos]);
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
        final Variant rightVar = evaluate(flatAST.getNodes()[(short)(childPos + 1)]);
        checkBoolOperand(expr.operator, rightVar);
        expr.result.accept(rightVar.getAsBoolean());
        return expr.result;
    }

    @Override
    public Variant visitTernaryExpr(final FlatExpr.Ternary expr) {
        final short childPos = expr.startChild;
        final Variant conditionVar = evaluate(flatAST.getNodes()[childPos]);
        checkBoolOperand(expr.operator, conditionVar);
        final boolean condition = conditionVar.getAsBoolean();
        return evaluate(condition ? flatAST.getNodes()[(short)(childPos + 1)] : flatAST.getNodes()[(short)(childPos + 2)]);
    }

    @Override
    public Variant visitUnaryExpr(final FlatExpr.Unary expr) {
        final Variant result = evaluate(flatAST.getNodes()[expr.startChild]);
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
    public Variant visitIdentifierExpr(final FlatExpr.Identifier expr) {
        final Variant res = ctx.get(expr.operator.lexeme, expr.result);
        if (res == null) {
            throw new RuntimeError(expr.operator, "Unknown identifier '" + expr.operator.lexeme + "'");
        }
        return res;
    }

    @Override
    public Variant visitResolvedIdentifierExpr(final FlatExpr.ResolvedIdentifier expr) {
        expr.function.call(expr.result);
        return expr.result;
    }

    @Override
    public Variant visitCallExpr(final FlatExpr.Call expr) {
        final Object funcObj = ctx.getFunction(expr.operator.lexeme);
        return callFunction(expr.result, expr.operator, funcObj, flatAST.getNodes(), expr.startChild, expr.numChildren);
    }

    @Override
    public Variant visitResolvedCallExpr(final FlatExpr.ResolvedCall expr) {
        return callFunction(expr.result, expr.operator, expr.function, flatAST.getNodes(), expr.startChild, expr.numChildren);
    }

    @Override
    public Variant visitObjectCallExpr(final FlatExpr.ObjectCall expr) {
        final short childPos = expr.startChild;
        final Variant objResult = evaluate(flatAST.getNodes()[childPos]);
        // if it's an object call from ExprContext
        if (isExprContext(objResult)) {
            // then fetch the function from that ExprContext
            final Object funcObj = objResult.getAsExprContext().getFunction(expr.operator.lexeme);
            return callFunction(expr.result, expr.operator, funcObj, flatAST.getNodes(), (short)(childPos + 1), (byte)(expr.numChildren - 1));
        }
        // otherwise it's a normal/native function call -> get the function from the global context
        final Object funcObj = ctx.getFunction(expr.operator.lexeme);
        if (funcObj == null) {
            throw new RuntimeError(expr.operator, "Unknown function '" + expr.operator.lexeme + "'");
        }

        try {
            // expr.args.size()+1 - because we add the resolved "this" as a second parameter (objResult)
            switch (expr.numChildren) {
                case 1:
                    ((Function1) funcObj).call(expr.result, objResult);
                    break;
                case 2:
                    ((Function2) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]));
                    break;
                case 3:
                    ((Function3) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]), evaluate(flatAST.getNodes()[(short)(childPos + 2)]));
                    break;
                case 4:
                    ((Function4) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]), evaluate(flatAST.getNodes()[(short)(childPos + 2)]), evaluate(flatAST.getNodes()[(short)(childPos + 3)]));
                    break;
                case 5:
                    ((Function5) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]), evaluate(flatAST.getNodes()[(short)(childPos + 2)]), evaluate(flatAST.getNodes()[(short)(childPos + 3)]), evaluate(flatAST.getNodes()[(short)(childPos + 4)]));
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
    public Variant visitResolvedObjectCallExpr(final FlatExpr.ResolvedObjectCall expr) {
        final short childPos = expr.startChild;
        final Variant objResult = evaluate(flatAST.getNodes()[childPos]);
        // if it's an object call from ExprContext
        if (isExprContext(objResult)) {
            return callFunction(expr.result, expr.operator, expr.function, flatAST.getNodes(), (short)(childPos + 1), (byte)(expr.numChildren - 1));
        }
        // otherwise it's a normal/native function call -> get the function from the global context
        final Object funcObj = expr.function;
        try {
            // expr.args.size()+1 - because we add the resolved "this" as a second parameter (objResult)
            switch (expr.numChildren) {
                case 1:
                    ((Function1) funcObj).call(expr.result, objResult);
                    break;
                case 2:
                    ((Function2) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]));
                    break;
                case 3:
                    ((Function3) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]), evaluate(flatAST.getNodes()[(short)(childPos + 2)]));
                    break;
                case 4:
                    ((Function4) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]), evaluate(flatAST.getNodes()[(short)(childPos + 2)]), evaluate(flatAST.getNodes()[(short)(childPos + 3)]));
                    break;
                case 5:
                    ((Function5) funcObj).call(expr.result, objResult, evaluate(flatAST.getNodes()[(short)(childPos + 1)]), evaluate(flatAST.getNodes()[(short)(childPos + 2)]), evaluate(flatAST.getNodes()[(short)(childPos + 3)]), evaluate(flatAST.getNodes()[(short)(childPos + 4)]));
                    break;
            }
        } catch (final ClassCastException castEx) {
            throw new RuntimeError(expr.operator, "ClassCastException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(castEx));
        } catch (final RuntimeException runtimeEx) {
            throw new RuntimeError(expr.operator, "RuntimeException in function '" + expr.operator.lexeme + "': " + getExceptionMsg(runtimeEx));
        }
        return expr.result;
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
