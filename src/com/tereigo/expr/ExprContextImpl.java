package com.tereigo.expr;

import com.tereigo.expr.function.ByteBufferSupplier;
import com.tereigo.expr.function.ExprContextSupplier;
import com.tereigo.expr.function.Function0;
import com.tereigo.expr.function.Function1;
import com.tereigo.expr.function.Function2;
import com.tereigo.expr.function.Function3;
import com.tereigo.expr.function.Function4;
import com.tereigo.expr.function.Function5;
import com.tereigo.expr.function.StringSupplier;
import com.tereigo.expr.variant.MutableVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

final class ExprContextImpl implements ExprContext, MutableExprContext {

  // Map of "name" -> Function0/1/2/3/4/5 objects
  private final Map<String, Object> functions = new HashMap<>();

  @Override
  public MutableVariant get(String name, MutableVariant result) {
    final Object funcObj = getFunction(name);
    if (funcObj == null) {
      return null;
    }
    ((Function0)funcObj).call(result);
    return result;
  }

  @Override
  public Object getFunction(String name) {
    return functions.get(name);
  }

  @Override
  public void defineLong(String name, LongSupplier supplier) {
    // we wrap all value providers into a function from 0 parameters (Function0)
    defineFunction(name, result -> result.accept(supplier.getAsLong()));
  }

  @Override
  public void defineDouble(String name, DoubleSupplier supplier) {
    defineFunction(name, result -> result.accept(supplier.getAsDouble()));
  }

  @Override
  public void defineString(String name, StringSupplier supplier) {
    defineFunction(name, result -> result.accept(supplier.getAsString()));
  }

  @Override
  public void defineByteBuffer(String name, ByteBufferSupplier supplier) {
    defineFunction(name, result -> result.accept(supplier.getAsByteBuffer()));
  }

  @Override
  public void defineBool(String name, BooleanSupplier supplier) {
    defineFunction(name, result -> result.accept(supplier.getAsBoolean()));
  }

  @Override
  public void defineExprContext(String name, ExprContextSupplier supplier) {
    defineFunction(name, result -> result.accept(supplier.getAsExprContext()));
  }

  @Override
  public void defineFunction(String name, Function0 func) {
    validateName(name);
    functions.put(name, func);
  }

  @Override
  public void defineFunction(String name, Function1 func) {
    validateName(name);
    functions.put(name, func);
  }

  @Override
  public void defineFunction(String name, Function2 func) {
    validateName(name);
    functions.put(name, func);
  }

  @Override
  public void defineFunction(String name, Function3 func) {
    validateName(name);
    functions.put(name, func);
  }

  @Override
  public void defineFunction(String name, Function4 func) {
    validateName(name);
    functions.put(name, func);
  }

  @Override
  public void defineFunction(String name, Function5 func) {
    validateName(name);
    functions.put(name, func);
  }

  @Override
  public void addAlias(String name, String alias) {
    if (name == null || alias == null) {
      throw new RuntimeException("Empty name or alias");
    }
    if (name.equals(alias)) {
      throw new RuntimeException("Identical name and alias: '" + name + "'");
    }
    final Object val = functions.get(name);
    if (val == null) {
      throw new RuntimeException("Unknown identifier '" + name + "' for alias '" + alias + "'");
    }
    validateName(alias);
    functions.put(alias, val);
  }

  @Override
  public void enrich(ExprContextEnricher... enrichers) {
    for (ExprContextEnricher enricher : enrichers) {
      enricher.enrich(this);
    }
  }

  @Override
  public String toString() {
    return "functions: " + functions.keySet();
  }

  private void validateName(final String name) {
    if (functions.containsKey(name)) {
      throw new RuntimeException("Function '" + name + "' is already defined");
    }
  }
}
