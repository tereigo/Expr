package com.tereigo.expr;

import com.tereigo.expr.utils.ByteBufferUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class ExprEvaluatorCacheTest {

    @Test
    public void testExprEvaluatorCache() {
        final ExprEvaluatorSupplier<ExprEvaluator> cache = ExprEvaluatorCacheFactory.create(ExprEvaluatorFactory::create);
        final ExprEvaluator evaluator = cache.getEvaluator(ByteBufferUtils.constant("1 + 1"));
        assertNotNull(evaluator);
        assertSame(evaluator, cache.getEvaluator(ByteBufferUtils.constant("1 + 1")));
        assertEquals(2, evaluator.evaluateLong());
        final ExprEvaluator evaluator2 = cache.getEvaluator(ByteBufferUtils.constant("1 + 2"));
        assertNotNull(evaluator2);
        assertEquals(3, evaluator2.evaluateLong());
        assertSame(evaluator2, cache.getEvaluator(ByteBufferUtils.constant("1 + 2")));
        assertNotSame(evaluator, evaluator2);
    }

    @Test
    public void testExprEvaluatorCacheWithContext() {
        final ExprContext ctx = ExprContextFactory.globalContext().addLong("a", () -> 1).getAsExprContext();
        final ExprEvaluatorSupplier<ExprEvaluatorWithContext> cache = ExprEvaluatorCacheFactory.create(ExprEvaluatorFactory::create);
        final ExprEvaluatorWithContext evaluator = cache.getEvaluator(ByteBufferUtils.constant("a + 1"));
        assertNotNull(evaluator);
        assertSame(evaluator, cache.getEvaluator(ByteBufferUtils.constant("a + 1")));
        assertEquals(2, evaluator.evaluateLong(ctx));
        final ExprEvaluatorWithContext evaluator2 = cache.getEvaluator(ByteBufferUtils.constant("a + 2"));
        assertNotNull(evaluator2);
        assertEquals(3, evaluator2.evaluateLong(ctx));
        assertSame(evaluator2, cache.getEvaluator(ByteBufferUtils.constant("a + 2")));
        assertNotSame(evaluator, evaluator2);
    }

    @Test
    public void testExprEvaluatorCacheWithStaticContext() {
        final ExprContext ctx = ExprContextFactory.globalContext().addLong("a", () -> 1).getAsExprContext();
        final ExprEvaluatorSupplier<ExprEvaluator> cache = ExprEvaluatorCacheFactory.create(source -> ExprEvaluatorFactory.create(ctx, source));
        final ExprEvaluator evaluator = cache.getEvaluator(ByteBufferUtils.constant("a + 1"));
        assertNotNull(evaluator);
        assertSame(evaluator, cache.getEvaluator(ByteBufferUtils.constant("a + 1")));
        assertEquals(2, evaluator.evaluateLong());
        final ExprEvaluator evaluator2 = cache.getEvaluator(ByteBufferUtils.constant("a + 2"));
        assertNotNull(evaluator2);
        assertEquals(3, evaluator2.evaluateLong());
        assertSame(evaluator2, cache.getEvaluator(ByteBufferUtils.constant("a + 2")));
        assertNotSame(evaluator, evaluator2);
    }

    @Test
    public void testExprEvaluatorFactoryByteBufferWithConstants() {
        final Map<String, ExprConstant> constants = ExprConstantsFactory.create()
                .addLong("$a", 41L)
                .build();

        final ExprEvaluatorWithContext evaluator = ExprEvaluatorFactory.create(ByteBufferUtils.constant("$a + 1"), constants);
        assertEquals(42, evaluator.evaluateLong());
    }

    @Test
    public void testExprEvaluatorFactoryOptimizedByteBufferWithConstants() {
        final ExprContext ctx = ExprContextFactory.globalContext().getAsExprContext();
        final Map<String, ExprConstant> constants = ExprConstantsFactory.create()
                .addLong("$a", 41L)
                .build();

        final ExprEvaluator evaluator = ExprEvaluatorFactory.create(ctx, ByteBufferUtils.constant("$a + 1"), constants);
        assertEquals(42, evaluator.evaluateLong());
    }

    @Test
    public void testExprEvaluatorCreatorDefaultStringMethod() {
        // exercises ExprEvaluatorCreator's default create(String) method, which delegates to create(ByteBuffer)
        final ExprEvaluatorCreator<ExprEvaluatorWithContext> creator = ExprEvaluatorFactory::create;
        final ExprEvaluatorWithContext evaluator = creator.create("1 + 1");
        assertEquals(2, evaluator.evaluateLong());
    }
}