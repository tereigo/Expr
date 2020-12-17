package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.function.ByteBufferSupplier;
import com.tereigo.atlas_expr.function.Function0;
import com.tereigo.atlas_expr.function.Function1;
import com.tereigo.atlas_expr.function.Function2;
import com.tereigo.atlas_expr.function.Function3;
import com.tereigo.atlas_expr.function.Function4;
import com.tereigo.atlas_expr.function.Function5;
import com.tereigo.atlas_expr.function.StringSupplier;
import com.tereigo.atlas_expr.variant.MutableVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

class ExprContextImpl implements ExprContext {

  private final Map<String, Entry> values = new HashMap<>();
  private final Map<String, Object> functions = new HashMap<>();

  @Override
  public MutableVariant get(Token token, MutableVariant result) {
    if (values.containsKey(token.lexeme)) {
      Entry entry = values.get(token.lexeme);
      switch (entry.type) {
        case DOUBLE:
          result.accept(((DoubleSupplier)entry.supplier).getAsDouble());
          return result;
        case LONG:
          result.accept(((LongSupplier)entry.supplier).getAsLong());
          return result;
        case BOOL:
          result.accept(((BooleanSupplier)entry.supplier).getAsBoolean());
          return result;
        case STRING:
          result.accept(((StringSupplier)entry.supplier).getAsString());
          return result;
        case BYTE_BUFFER:
          result.accept(((ByteBufferSupplier)entry.supplier).getAsByteBuffer());
          return result;
        default:
          throw new RuntimeError(token, "Unknown type '" + entry.type + "' for identifier '" + token.lexeme + "'");
      }
    }
    return null;
  }

  @Override
  public Object getFunction(Token token) {
    return functions.get(token.lexeme);
  }

  void defineLong(String name, LongSupplier supplier) {
    values.put(name, new Entry(supplier, ExprType.LONG));
  }

  void defineDouble(String name, DoubleSupplier supplier) {
    values.put(name, new Entry(supplier, ExprType.DOUBLE));
  }

  void defineString(String name, StringSupplier supplier) {
    values.put(name, new Entry(supplier, ExprType.STRING));
  }

  void defineByteBuffer(String name, ByteBufferSupplier supplier) {
    values.put(name, new Entry(supplier, ExprType.BYTE_BUFFER));
  }

  void defineBool(String name, BooleanSupplier supplier) {
    values.put(name, new Entry(supplier, ExprType.BOOL));
  }

  void defineFunction(String name, Function0 func) {
    functions.put(name, func);
  }

  void defineFunction(String name, Function1 func) {
    functions.put(name, func);
  }

  void defineFunction(String name, Function2 func) {
    functions.put(name, func);
  }

  void defineFunction(String name, Function3 func) {
    functions.put(name, func);
  }

  void defineFunction(String name, Function4 func) {
    functions.put(name, func);
  }

  void defineFunction(String name, Function5 func) {
    functions.put(name, func);
  }

  @Override
  public String toString() {
    return "values: " + values.toString() + ", functions: " + functions.keySet();
  }

  private static class Entry {
    final Object supplier;
    final ExprType type;

    private Entry(Object supplier, ExprType type) {
      this.supplier = supplier;
      this.type = type;
    }
  }
}
