package com.tereigo.expr.falcon.utils;

import java.util.concurrent.TimeUnit;

public interface EngineTimeProvider {
    long getEngineTimeNanos();

    default long getEngineTimeMs() {
        return TimeUnit.MILLISECONDS.convert(getEngineTimeNanos(), TimeUnit.NANOSECONDS);
    }
}
