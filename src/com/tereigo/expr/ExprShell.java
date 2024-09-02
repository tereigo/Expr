package com.tereigo.expr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ExprShell {

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: ExprShell [script]");
      System.exit(0);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    try {
      run(new String(bytes, Charset.defaultCharset()));
    } catch (RuntimeException ex) {
      printToError(ex.getMessage());
      ex.printStackTrace();
      System.exit(1);
    }
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      try {
        run(line);
      } catch (RuntimeException ex) {
        printToError(ex.getMessage());
      }
    }
  }

  private static void run(String source) {
    try {
      AstHierarchyPrinter printer = new AstHierarchyPrinter();
      String graphView = printer.print(ExprCompiler.compile(source));
      System.out.println("AST view: \n" + graphView);
      System.out.println("\nResult: \n" + evaluateString(source));
    } catch (RuntimeError error) {
      printToError(error.getMessage() + " [line " + error.token.line + ", pos " + (error.token.pos + 1) + "]");
    }
  }

  private static String evaluateString(String source) {
    ExprEvaluator evaluator = new ExprEvaluator(source);
    return evaluator.evaluateAsObject().toString();
  }

  static void printToError(String errorMsg) {
    System.err.println(errorMsg);
  }
}
