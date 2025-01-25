package com.tereigo.expr.metrics.metric;

// Marker interface for the primitive type metrics: Bool, Long, Double, String
// It should not be inherited by Dedup Metric classes
// This is just to have a distinct hierarchies of primitive metrics vs Dedup metrics
// to avoid using dynamic (instance of) casting
public interface SimpleMetric extends Metric {
}
