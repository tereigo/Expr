package com.tereigo.expr.utils;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static com.tereigo.expr.utils.ByteBufferUtils.constant;
import static com.tereigo.expr.utils.ByteBufferUtils.contains;

public class ByteBufferUtilsBenchmarkTest {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 3)
    @Warmup(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 5000)
    @Measurement(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 5000)
//    @Warmup(iterations = 5, timeUnit = TimeUnit.MILLISECONDS, time = 10000)
//    @Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS, time = 10000)
    public void benchmarkContainsByteBufferString() {
        contains(constant("To be or not to be that is a question"), "To be");
        contains(constant("To be or not to be that is a question"), "To be or not to be that is");
        contains(constant("To be or not to be that is a question"), "To be or not to be that was");
        contains(constant("To be or not to be that is a question"), "o be that");
        contains(constant("To be or not to be that is a question"), "question");
        contains(constant("To be or not to be that is a question"), "question!");
        contains(constant("To be or not to be that is a question"), "questiom");
        contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "Gradle");
        contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "gradle");
        contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "the following");
        contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "following");
        contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "you can do either of the following");
        contains(constant("If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following"), "If you also want to run the benchmarks from within your IDE instead of through Gradle, you can do either of the following");
    }

    @Test
    public void runBenchmarks() throws RunnerException {
        Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".benchmark*")
                .build();

        new Runner(options).run();
    }
}

//  257
//
//public static boolean contains(ByteBuffer str, String pattern) {
//    // TODO: optimize by checking the first character to match to start the cycle
//    // TODO: copy-paste from String.contains()
//    for (int i = 0; i < str.remaining() - pattern.length() + 1; i++) {
//        boolean same = true;
//        for (int j = 0; j < pattern.length(); j++) {
//            if (str.get(i + j) != (byte)(pattern.charAt(j) & 0xFF)) {
//                same = false;
//                break;
//            }
//        }
//        if (same) {
//            return true;
//        }
//    }
//    return false;
//}

// 470
//
//public static boolean contains(ByteBuffer str, String pattern) {
//    if (pattern.isEmpty()) {
//        return true;
//    }
//    final int remaining = str.remaining() - pattern.length() + 1;
//    final byte firstByte = (byte)(pattern.charAt(0) & 0xFF);
//    int i = 0;
//
//    while (true) {
//        // search for the first same character in str
//        for (; i < remaining && str.get(i) != firstByte; i++) ;
//
//        // if we reached the end of the str then the pattern is not found
//        if (i >= remaining) {
//            return false;
//        }
//
//        // we found the first same character
//        int j = 1;
//        // check if the rest characters are matching from that point
//        for (; j < pattern.length(); j++) {
//            if (str.get(i + j) != (byte) (pattern.charAt(j) & 0xFF)) {
//                // not all symbols matching
//                // go to the next position in the string
//                i++;
//                break;
//            }
//        }
//        if (j == pattern.length()) {
//            return true;
//        }
//    }
//}


// 482
//public static boolean contains(ByteBuffer str, String pattern) {
//    if (pattern.isEmpty()) {
//        return true;
//    }
//
//    final byte firstByte = (byte)(pattern.charAt(0) & 0xFF);
//    final int max = str.remaining() - pattern.length();
//
//    for (int i = 0; i <= max; i++) {
//        // search for the first same character in str
//        if (str.get(i) != firstByte) {
//            while (++i <= max && str.get(i) != firstByte) ;
//        }
//
//        if (i <= max) {
//            int j = i + 1;
//            final int end = j + pattern.length() - 1;
//            for (int k = 1; j < end && str.get(j) == (byte) (pattern.charAt(k) & 0xFF); j++, k++) ;
//
//            if (j == end) {
//                return true;
//            }
//        }
//    }
//    return false;
//}
