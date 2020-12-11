package com.tereigo.atlas_expr;

import java.util.List;

public final class ExprCompiler {

    private ExprCompiler() {}

    // Convert Expression into AST
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static Expr compile(String source) {
        final Scanner scanner = new Scanner(source);
        final List<Token> tokens = scanner.tokens();
        final Parser parser = new Parser(tokens);
        final Expr expression = parser.parse();
        return expression;
    }
}
