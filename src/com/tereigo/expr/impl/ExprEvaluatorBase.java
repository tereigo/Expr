package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.variant.Variant;

import static com.tereigo.expr.impl.ExceptionUtils.getExceptionMsg;

abstract class ExprEvaluatorBase {
    private final String source;
    private final ExprInterpreter interpreter;
//    private final ExprFlatInterpreter interpreter;

    ExprEvaluatorBase(final ASTRoot root) {
        this.source = root.source();
//        final FlatAST flatAST = new AstFlatter().flatten(root);
//        this.interpreter = new ExprFlatInterpreter(flatAST);
        this.interpreter = new ExprInterpreter(root.expr());
    }

    protected Variant evaluate(final ExprContext ctx) throws RuntimeException {
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
