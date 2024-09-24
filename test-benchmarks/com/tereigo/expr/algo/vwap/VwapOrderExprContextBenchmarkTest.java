package com.tereigo.expr.algo.vwap;

import com.tereigo.expr.ExprContext;
import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprEvaluatorFactory;
import com.tereigo.expr.ExprEvaluatorWithContext;
import com.tereigo.expr.domains.FalconExprContextBuilder;
import com.tereigo.expr.domains.order.OrderDomain;
import com.tereigo.expr.domains.order.OrderFieldResolverImpl;
import com.tereigo.expr.falcon.utils.ReferenceDataCacheImpl;
import com.tereigo.expr.order.TestVwapOrder;
import com.tereigo.expr.utils.ByteBufferUtils;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class VwapOrderExprContextBenchmarkTest {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    @Fork(value = 1)
//    @Warmup(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 5000)
//    @Measurement(iterations = 3, timeUnit = TimeUnit.MILLISECONDS, time = 5000)
    @Fork(value = 3)
    @Warmup(iterations = 5, timeUnit = TimeUnit.MILLISECONDS, time = 10000)
    @Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS, time = 10000)
    public void benchmarkSimpleExpression(final BenchmarkState state) {
        state.evaluator.evaluateBool(state.ctx);
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
        ExprEvaluatorWithContext evaluator;
        ExprContext ctx;

        @Setup
        public void prepare() {
            final ReferenceDataCacheImpl refData = new ReferenceDataCacheImpl();

            final OrderFieldResolverImpl orderFieldResolver;

            final TestVwapOrder order1 = TestVwapOrder.create()
                    .withProductId(123).withClientId(1).withVolumeLimit(0.1);
            final TestVwapOrder order2 = TestVwapOrder.create()
                    .withProductId(124).withClientId(2).withVolumeLimit(0.2);
            final TestVwapOrder order3 = TestVwapOrder.create()
                    .withProductId(124).withClientId(3).withVolumeLimit(0.2);

            refData.addTuid(1, ByteBufferUtils.constant("CLIENT1"));
            refData.addTuid(2, ByteBufferUtils.constant("CLIENT2"));

            refData.addRic(123, ByteBufferUtils.constant("VOD.L"));
            refData.addRic(124, ByteBufferUtils.constant("BP.L"));

            OrderDomain.init(refData);

            orderFieldResolver = new OrderFieldResolverImpl();
            final ExprContextBuilder mutCtx = FalconExprContextBuilder.start().orderWithShortcuts(orderFieldResolver).build();
            final VwapOrderExprContextCreator creator = new VwapOrderExprContextCreator();
            creator.enrich(orderFieldResolver, mutCtx);

            ctx = mutCtx.getAsExprContext();

            orderFieldResolver.setOrder(order1);

            // 232000
//            evaluator = ExprEvaluatorFactory.create("true");
            // 14300
//            evaluator = ExprEvaluatorFactory.create("vwap.volumeLimit == 0.1");
            // 13000
//            evaluator = ExprEvaluatorFactory.create("(vwap.volumeLimit == 0.1)");
            // 16600
//            evaluator = ExprEvaluatorFactory.create("ric in ['BT.L', 'VOD.L', 'TSCO.L']");
            // 15400
//            evaluator = ExprEvaluatorFactory.create("(ric in ['BT.L', 'VOD.L', 'TSCO.L'])");
            // 7366 +- 262
//            evaluator = ExprEvaluatorFactory.create("(vwap.volumeLimit == 0.1) and (ric in ['BT.L', 'VOD.L', 'TSCO.L'])");
            // 7152 +- 427
//            evaluator = ExprEvaluatorFactory.create("vwap.volumeLimit == 0.1 and ric in ['BT.L', 'VOD.L', 'TSCO.L']");
            // 1553
            // flat: 1310 (worse!)
            evaluator = ExprEvaluatorFactory.create("(vwap.volumeLimit == 0.1) and (vwap.ric == 'VOD.L') and (order.ric == 'VOD.L') and (ric == 'VOD.L') and (vwap.tuid == 'CLIENT1') and (order.tuid == 'CLIENT1') and (tuid == 'CLIENT1') and (ric in ['BT.L', 'VOD.L', 'TSCO.L'])");
        }
    }
}