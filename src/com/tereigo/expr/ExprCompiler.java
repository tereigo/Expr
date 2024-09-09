package com.tereigo.expr;

import com.tereigo.expr.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.List;

final class ExprCompiler {

    private ExprCompiler() {}

    static ASTRoot compile(final String source) {
        return compile(ByteBufferUtils.constant(source));
    }

    // Convert Expression into AST
    @SuppressWarnings("UnnecessaryLocalVariable")
    static ASTRoot compile(ByteBuffer source) {
        final String strSource = ByteBufferUtils.toString(source);
        try {
            final ExprScanner scanner = new ExprScanner(strSource);
            final List<Token> tokens = scanner.tokens();
            final ExprParser parser = new ExprParser(tokens);
            final Expr expression = parser.parse();
            return new ASTRoot(strSource, expression);
        } catch (ParseError err) {
            // Let's enhance the error with the relevant context info
            throw new ParseError("Expression parsing error " + err.getMessage() + " in expression '" + strSource + "'");
        }
    }
}
