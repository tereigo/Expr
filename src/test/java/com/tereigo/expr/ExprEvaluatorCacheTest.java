package com.tereigo.expr;

import com.tereigo.expr.utils.ByteBufferUtils;
import org.junit.jupiter.api.Test;

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
}