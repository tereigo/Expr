package com.tereigo.atlas_expr;

import java.util.List;

public final class ExprCompiler {

    private ExprCompiler() {}

    // Convert Expression into AST
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static ASTRoot compile(String source) {
        try {
            final ExprScanner scanner = new ExprScanner(source);
            final List<Token> tokens = scanner.tokens();
            final ExprParser parser = new ExprParser(tokens);
            final Expr expression = parser.parse();
            return new ASTRoot(source, expression);
        } catch (ParseError err) {
            // Let's enhance the error with the relevant context info
            throw new ParseError("Expression parsing error " + err.getMessage() + " in expression '" + source + "'");
        }
    }
}
