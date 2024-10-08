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
        final ExprEvaluatorCache<ExprEvaluatorWithContext> cache = new ExprEvaluatorCache<>(ExprEvaluatorFactory::create);
        final ExprEvaluatorWithContext evaluator = cache.getExprEvaluator(ByteBufferUtils.constant("1 + 1"));
        assertNotNull(evaluator);
        assertSame(evaluator, cache.getExprEvaluator(ByteBufferUtils.constant("1 + 1")));
        assertEquals(2, evaluator.evaluateLong());
        final ExprEvaluatorWithContext evaluator2 = cache.getExprEvaluator(ByteBufferUtils.constant("1 + 2"));
        assertNotNull(evaluator2);
        assertEquals(3, evaluator2.evaluateLong());
        assertSame(evaluator2, cache.getExprEvaluator(ByteBufferUtils.constant("1 + 2")));
        assertNotSame(evaluator, evaluator2);
    }
}