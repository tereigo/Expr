package com.tereigo.expr.impl;

import com.tereigo.expr.utils.ByteBufferUtils;
import com.tereigo.expr.utils.ExceptionUtils;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ExprCompiler {

    private ExprCompiler() { }

    public static ASTRoot compile(final String source) {
        return compile(ByteBufferUtils.constant(source));
    }

    public static ASTRoot compile(final String source, final Map<String, Expr.Literal> constants) {
        return compile(ByteBufferUtils.constant(source), constants);
    }

    public static ASTRoot compile(final ByteBuffer source) {
        return compile(source, Collections.emptyMap());
    }

    // Convert Expression into AST
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static ASTRoot compile(final ByteBuffer source, final Map<String, Expr.Literal> constants) {
        final String strSource = ByteBufferUtils.toString(source);
        try {
            final ExprScanner scanner = new ExprScanner(strSource);
            final List<Token> tokens = scanner.tokens();
            final ExprParser parser = new ExprParser(tokens, constants);
            final Expr expression = parser.parse();
            final ASTRoot root = new ASTRoot(strSource, expression);
            return root;
        } catch (final ParseError err) {
            // Let's enhance the error with the relevant context info
            throw new ParseError("Expression parsing error " + ExceptionUtils.getExceptionMsg(err) + " in expression '" + strSource + "'", err);
        }
    }
}
