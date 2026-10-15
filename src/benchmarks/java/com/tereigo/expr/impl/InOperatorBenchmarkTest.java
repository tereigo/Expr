package com.tereigo.expr.impl;

import com.tereigo.expr.*;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks the "in [...]" operator's literal-list fast path (StaticInOperator: values partitioned
 * once at parse time into sorted arrays, matched via binary search) against the generic dynamic path
 * (InOperator: re-evaluates every value expression and scans linearly) - which is exactly what every
 * literal "in" list went through before StaticInOperator was added.
 * <p>
 * The "dynamic" expression wraps every literal in a trivial "+0" so the parser can't treat it as a
 * literal (it becomes an Expr.Binary, not an Expr.Literal) and falls back to InOperator, while
 * evaluating to the exact same set of values - giving a fair, apples-to-apples comparison through the
 * real parse+evaluate pipeline, not a hand-rolled approximation.
 * <p>
 * Each benchmark looks up a value that is NOT in the list, forcing the dynamic path's linear scan to
 * run to completion (its worst case) on every invocation - the scenario where the fast path's
 * O(log n) binary search should look best against the O(n) scan.
 */
public class InOperatorBenchmarkTest {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Warmup(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 1000)
    @Measurement(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 1000)
    public boolean benchmarkStaticInOperator(final BenchmarkState state) {
        return state.staticEvaluator.evaluateBool(state.ctx);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Warmup(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 1000)
    @Measurement(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 1000)
    public boolean benchmarkDynamicInOperator(final BenchmarkState state) {
        return state.dynamicEvaluator.evaluateBool(state.ctx);
    }

    @Test
    public void runBenchmarks() throws RunnerException {
        final Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".benchmark*")
                .build();

        new Runner(options).run();
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"10", "100", "1000"})
        int size;

        ExprContext ctx;
        ExprEvaluatorWithContext staticEvaluator;
        ExprEvaluatorWithContext dynamicEvaluator;

        @Setup
        public void prepare() {
            final ExprContextBuilder mutCtx = ExprContextFactory.globalContext();
            // never present in either list: forces a full, unsuccessful scan of the dynamic list,
            // and a full-depth, unsuccessful binary search of the static one
            mutCtx.addLong("$x", () -> -1L);
            ctx = mutCtx.getAsExprContext();

            final StringBuilder staticList = new StringBuilder();
            final StringBuilder dynamicList = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    staticList.append(", ");
                    dynamicList.append(", ");
                }
                staticList.append(i);
                dynamicList.append(i);
            }

            staticEvaluator = ExprEvaluatorFactory.create("$x in [" + staticList + "]");
            dynamicEvaluator = ExprEvaluatorFactory.create("$x in [" + dynamicList + "+0]");
        }
    }
}
