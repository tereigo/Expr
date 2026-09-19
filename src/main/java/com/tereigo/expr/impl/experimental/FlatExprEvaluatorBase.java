package com.tereigo.expr.impl.experimental;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.utils.ExceptionUtils;
import com.tereigo.expr.impl.RuntimeError;
import com.tereigo.expr.variant.Variant;

/*
  Experimental: mirrors com.tereigo.expr.impl.ExprEvaluatorBase, but evaluates a FlatAST via
  ExprFlatInterpreter instead of walking the Expr tree via ExprInterpreter.
 */
abstract class FlatExprEvaluatorBase {
    private final String source;
    private final ExprFlatInterpreter interpreter;

    FlatExprEvaluatorBase(final FlatAST flatAST, final String source) {
        this.source = source;
        this.interpreter = new ExprFlatInterpreter(flatAST);
    }

    protected Variant evaluate(final ExprContext ctx) throws RuntimeException {
        try {
            return interpreter.evaluate(ctx);
        } catch (final RuntimeError err) {
            // Let's enhance the error with the relevant context info
            final String msg = "Expression evaluation error [line " + err.token.line + ", pos " + (err.token.pos + 1) + "]: "
                    + ExceptionUtils.getExceptionMsg(err) + getSourceString();
            throw new RuntimeError(err.token, msg, err);
        } catch (final RuntimeException ex) {
            // Let's enhance the error with the relevant context info
            final String msg = "Expression evaluation error: " + ExceptionUtils.getExceptionMsg(ex) + getSourceString();
            throw new RuntimeException(msg, ex);
        }
    }

    private String getSourceString() {
        return source.isEmpty() ? "" : " in expression '" + source + "'";
    }

    protected static RuntimeException wrapTypeError(final RuntimeException ex) {
        return new RuntimeException("Result of an unexpected type: " + ExceptionUtils.getExceptionMsg(ex), ex);
    }
}
