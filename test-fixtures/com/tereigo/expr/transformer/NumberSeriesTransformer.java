package com.tereigo.expr.transformer;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.MutableExprContext;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Simple example of Expr usage
 * This application shows how to transform a set of numbers by applying the arbitrary expression:
 * 1, 2, 3 -> apply "x * 2" expression -> 2, 4, 6
 *
 * Sample output:
 *
 * Please enter a list of double numbers separated by comma (example: 1,15.0,-128):
 * 1,15.0,-128
 * Please enter a transformation expression where 'x' is used as a variable:
 * x * 2
 * Value: 1.0 -> 2.0
 * Value: 15.0 -> 30.0
 * Value: -128.0 -> -256.0
 */
public final class NumberSeriesTransformer {

    public static void main(final String[] args) {
        final Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter a list of double numbers separated by comma (example: 1,15.0,-128): ");
        final String numbersStr = scanner.nextLine();
        final double[] numbers = Arrays.stream(numbersStr.split(","))
                .map(String::trim)
                .mapToDouble(Double::parseDouble)
                .toArray();

        System.out.println("Please enter a transformation expression where 'x' is used as a variable: ");
        final String input = scanner.nextLine();
        scanner.close();

        // compile expression string and create an evaluator for it
        final ExprEvaluator evaluator = new ExprEvaluator(input);

        for (int i = 0; i < numbers.length; i++) {
            final double originalVal = numbers[i];

            // create evaluation context and add "x/X" as the external identifiers
            // this context will also include all "native" math functions like sin, sqrt, abs, ...
            final MutableExprContext mutCtx = ExprContextFactory.createGlobalContext(ctx -> {
                ctx.defineDouble("x", () -> originalVal);
                ctx.addAlias("x", "X");
            });
            // convert it to the immutable evaluation context
            final ExprContext ctx = mutCtx.getAsExprContext();

            final double transformedValue = evaluator.evaluateDouble(ctx);

            System.out.println("Value: " + originalVal + " -> " + transformedValue);
        }
    }
}
