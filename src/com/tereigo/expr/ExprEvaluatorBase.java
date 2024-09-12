package com.tereigo.expr;

import com.tereigo.expr.variant.Variant;

import static com.tereigo.expr.ExceptionUtils.getExceptionMsg;

abstract class ExprEvaluatorBase {
    private final String source;
    private final ExprInterpreter interpreter;

    ExprEvaluatorBase(final ASTRoot root) {
        this.source = root.source();
        this.interpreter = new ExprInterpreter(root.expr());
    }

    protected Variant evaluate(final ExprContext ctx) {
        try {
            return interpreter.evaluate(ctx);
        } catch (final RuntimeError err) {
            // Let's enhance the error with the relevant context info
            final String msg = "Expression evaluation error [line " + err.token.line + ", pos " + (err.token.pos + 1) + "]: "
                    + err.getMessage() + getSourceString();
            throw new RuntimeError(err.token, msg);
        } catch (final RuntimeException ex) {
            // Let's enhance the error with the relevant context info
            final String msg = "Expression evaluation error: " + getExceptionMsg(ex) + getSourceString();
            throw new RuntimeException(msg, ex);
        }
    }

    private String getSourceString() {
        return source.isEmpty() ? "" : " in expression '" + source + "'";
    }
}
