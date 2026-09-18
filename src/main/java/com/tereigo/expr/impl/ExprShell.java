package com.tereigo.expr.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ExprShell {

    public static void main(final String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: ExprShell [script]");
            System.exit(0);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(final String path) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(path));
        try {
            run(new String(bytes, Charset.defaultCharset()));
        } catch (final RuntimeException ex) {
            printToError(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void runPrompt() throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print("> ");
            final String line = reader.readLine();
            if (line == null) {
                break;
            }
            try {
                run(line);
            } catch (final RuntimeException ex) {
                printToError(ex.getMessage());
            }
        }
    }

    private static void run(final String source) {
        try {
            final AstHierarchyPrinter printer = new AstHierarchyPrinter();
            final String graphView = printer.print(ExprCompiler.compile(source));
            System.out.println("AST view: \n" + graphView);
            System.out.println("\nResult: \n" + evaluateString(source));
        } catch (final RuntimeError error) {
            printToError(error.getMessage() + " [line " + error.token.line + ", pos " + (error.token.pos + 1) + "]");
        }
    }

    private static String evaluateString(final String source) {
        final ExprEvaluatorImpl evaluator = new ExprEvaluatorImpl(source);
        return evaluator.evaluateAsObject().toString();
    }

    static void printToError(final String errorMsg) {
        System.err.println(errorMsg);
    }
}
