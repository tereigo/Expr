package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.function.ByteBufferSupplier;
import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.function.StringSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

class ExprContextImpl implements ExprContext {

  private final Map<String, Entry> values = new HashMap<>();

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

  @Override
  public String toString() {
    return values.toString();
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
