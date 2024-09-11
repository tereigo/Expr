package com.tereigo.expr.transformer;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextFactory;
import com.tereigo.expr.ExprEvaluator;
import com.tereigo.expr.MutableExprContext;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.DoubleSupplier;

/**
 * Simple example of Expr usage
 * This application shows how to transform a set of numbers by applying the arbitrary expression:
 * 1, 2, 3 -> apply "x * 2" expression -> 2, 4, 6
 *
 * Sample output:
 *
 * Please enter a list of double numbers separated by comma (example: 1,15.0,-128):
 * 1,15, 35.0, -24
 * Please enter a transformation expression where 'x' is used as a variable:
 * pow(x, 2)
 * Value: 1.0 -> 1.0
 * Value: 15.0 -> 225.0
 * Value: 35.0 -> 1225.0
 * Value: -24.0 -> 576.0
 */
public final class OptimizedNumberSeriesTransformer {

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

        // NOTICE: comparing to the other example here we show how to avoid creating ExprContext in a loop
        // because it's effectively static and not supposed to be changed for each evaluation
        // So we can create it once and apply the same context for all different numbers evaluations

        // Create value holder where we will source the value from
        final SettableDoubleSupplier valueHolder = new SettableDoubleSupplier();

        // create evaluation context and add "x/X" as the external identifiers
        // this context will also include all "native" math functions like sin, sqrt, abs, ...
        final MutableExprContext mutCtx = ExprContextFactory.createGlobalContext(ctx -> {
            ctx.defineDouble("x", valueHolder);
            ctx.addAlias("x", "X");
        });
        // convert it to the immutable evaluation context
        final ExprContext ctx = mutCtx.getAsExprContext();

        // compile expression string and create an evaluator for it

        // when we pass ExprContext to Evaluator constructor then this context is considered to be immutable
        // (meaning that it's not supposed to change from this moment till evaluation)
        // and the same context is supposed to be passed for the evaluation: evaluator.evaluateDouble(ctx)
        // In this case ExprEvaluator will "optimize" the expression during parsing
        final ExprEvaluator evaluator = new ExprEvaluator(ctx, input);

        for (int i = 0; i < numbers.length; i++) {
            final double val = numbers[i];
            // set the new number to process in the value holder where it will be sourced from during evaluation
            valueHolder.setValue(val);
            System.out.println("Value: " + val + " -> " + evaluator.evaluateDouble(ctx));
        }
    }

    private static class SettableDoubleSupplier implements DoubleSupplier {
        private double value;

        void setValue(final double val) {
            value = val;
        }

        @Override
        public double getAsDouble() {
            return value;
        }
    }
}
