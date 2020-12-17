package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.atlas.utils.ByteBufferUtils;
import com.tereigo.atlas_expr.atlas.utils.OrderPrice;
import com.tereigo.atlas_expr.atlas.utils.PriceUtils;
import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.variant.VariantUtils;

/*
  Provides Expr native functions
 */
class ExprContextNative implements ExprContext {
  static final ExprContext INSTANCE = new ExprContextNative();

  private final ExprContextImpl ctx = new ExprContextImpl();

  private ExprContextNative() {
    ctx.defineFunction("min", (result, arg1, arg2) -> {
      if (VariantUtils.isLong(arg1) && VariantUtils.isLong(arg2)) {
        result.accept(Math.min(arg1.getAsLong(), arg2.getAsLong()));
      } else if (VariantUtils.isLong(arg1) && VariantUtils.isDouble(arg2)) {
        result.accept(Math.min(arg1.getAsLong(), arg2.getAsDouble()));
      } else if (VariantUtils.isDouble(arg1) && VariantUtils.isLong(arg2)) {
        result.accept(Math.min(arg1.getAsDouble(), arg2.getAsLong()));
      } else if (VariantUtils.isDouble(arg1) && VariantUtils.isDouble(arg2)) {
        result.accept(Math.min(arg1.getAsDouble(), arg2.getAsDouble()));
      } else {
        throw new RuntimeException("Operands must be numbers");
      }
    });

    ctx.defineFunction("max", (result, arg1, arg2) -> {
      if (VariantUtils.isLong(arg1) && VariantUtils.isLong(arg2)) {
        result.accept(Math.max(arg1.getAsLong(), arg2.getAsLong()));
      } else if (VariantUtils.isLong(arg1) && VariantUtils.isDouble(arg2)) {
        result.accept(Math.max(arg1.getAsLong(), arg2.getAsDouble()));
      } else if (VariantUtils.isDouble(arg1) && VariantUtils.isLong(arg2)) {
        result.accept(Math.max(arg1.getAsDouble(), arg2.getAsLong()));
      } else if (VariantUtils.isDouble(arg1) && VariantUtils.isDouble(arg2)) {
        result.accept(Math.max(arg1.getAsDouble(), arg2.getAsDouble()));
      } else {
        throw new RuntimeException("Operands must be numbers");
      }
    });

    ctx.defineFunction("abs", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(Math.abs(arg1.getAsLong()));
      } else if (VariantUtils.isDouble(arg1)) {
        result.accept(Math.abs(arg1.getAsDouble()));
      } else {
        throw new RuntimeException("Operand must be a number");
      }
    });

    ctx.defineFunction("round", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(arg1.getAsLong());
      } else if (VariantUtils.isDouble(arg1)) {
        result.accept(Math.round(arg1.getAsDouble()));
      } else {
        throw new RuntimeException("Operand must be a number");
      }
    });

    ctx.defineFunction("roundToNearest", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(arg1.getAsLong());
      } else if (VariantUtils.isDouble(arg1)) {
        final double delta = arg1.getAsDouble() > 0.0 ? 0.5 : -0.5;
        result.accept((long)(arg1.getAsDouble() + delta));
      } else {
        throw new RuntimeException("Operand must be a number");
      }
    });

    ctx.defineFunction("roundUp", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(arg1.getAsLong());
      } else if (VariantUtils.isDouble(arg1)) {
        result.accept((long)(Math.ceil(arg1.getAsDouble())));
      } else {
        throw new RuntimeException("Operand must be a number");
      }
    });

    ctx.defineFunction("roundDown", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(arg1.getAsLong());
      } else if (VariantUtils.isDouble(arg1)) {
        result.accept((long)(Math.floor(arg1.getAsDouble())));
      } else {
        throw new RuntimeException("Operand must be a number");
      }
    });

    ctx.defineFunction("toLong", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(arg1.getAsLong());
      } else if (VariantUtils.isDouble(arg1)) {
        result.accept((long)(arg1.getAsDouble()));
      } else {
        throw new RuntimeException("Operand must be a number");
      }
    });

    ctx.defineFunction("toDouble", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept((double)arg1.getAsLong());
      } else if (VariantUtils.isDouble(arg1)) {
        result.accept(arg1.getAsDouble());
      } else {
        throw new RuntimeException("Operand must be a number");
      }
    });

    ctx.defineFunction("ltod", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(PriceUtils.ltod(arg1.getAsLong()));
      } else {
        throw new RuntimeException("Operand must be a LONG number");
      }
    });

    ctx.defineFunction("dtol", (result, arg1) -> {
      if (VariantUtils.isDouble(arg1)) {
        result.accept((long)PriceUtils.dtol(arg1.getAsDouble()));
      } else {
        throw new RuntimeException("Operand must be a DOUBLE number");
      }
    });

    ctx.defineFunction("isMarketPrice", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(OrderPrice.isMarket(arg1.getAsLong()));
      } else {
        throw new RuntimeException("Operand must be a LONG number");
      }
    });

    ctx.defineFunction("isLimitPrice", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(OrderPrice.isLimit(arg1.getAsLong()));
      } else {
        throw new RuntimeException("Operand must be a LONG number");
      }
    });

    ctx.defineFunction("isValidPrice", (result, arg1) -> {
      if (VariantUtils.isLong(arg1)) {
        result.accept(OrderPrice.isValid(arg1.getAsLong()));
      } else {
        throw new RuntimeException("Operand must be a LONG number");
      }
    });

    // String functions
    ctx.defineFunction("isEmpty", (result, arg1) -> {
      if (VariantUtils.isString(arg1)) {
        result.accept(arg1.getAsString().isEmpty());
      } else if (VariantUtils.isByteBuffer(arg1)) {
          result.accept(ByteBufferUtils.isEmpty(arg1.getAsByteBuffer()));
      } else {
        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
      }
    });

    ctx.defineFunction("length", (result, arg1) -> {
      if (VariantUtils.isString(arg1)) {
        result.accept(arg1.getAsString().length());
      } else if (VariantUtils.isByteBuffer(arg1)) {
        result.accept(arg1.getAsByteBuffer().remaining());
      } else {
        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
      }
    });

    // TODO: finish it
//    ctx.defineFunction("equalsIgnoreCase", (result, arg1, arg2) -> {
//      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
//        result.accept(arg1.getAsString().equalsIgnoreCase(arg2.getAsString()));
//      } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        // TODO: reimplement - generates garbage
//        result.accept(arg1.getAsString().equalsIgnoreCase(ByteBufferUtils.parseString(arg2.getAsByteBuffer())));
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
//        result.accept(ByteBufferUtils.equalsCaseInsensitive(arg1.getAsByteBuffer(), arg2.getAsString()));
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        result.accept(ByteBufferUtils.equalsCaseInsensitive(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
//      } else {
//        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
//      }
//    });

    ctx.defineFunction("contains", (result, arg1, arg2) -> {
      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
        result.accept(arg1.getAsString().contains(arg2.getAsString()));
      } else {
        throw new RuntimeException("Operand must be a STRING");
      }
    });

    // TODO: finish it
//    ctx.defineFunction("contains", (result, arg1, arg2) -> {
//      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
//        result.accept(arg1.getAsString().contains(arg2.getAsString()));
//      } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        // TODO: reimplement - generates garbage
//        result.accept(arg1.getAsString().contains(arg2.getAsByteBuffer().asCharBuffer()));
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
//        throw new RuntimeException("contains() not implemented for ByteBuffer");
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        throw new RuntimeException("contains() not implemented for ByteBuffer");
//      } else {
//        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
//      }
//    });

    // TODO: finish it
//    ctx.defineFunction("startsWith", (result, arg1, arg2) -> {
//      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
//        result.accept(arg1.getAsString().startsWith(arg2.getAsString()));
//      } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        // TODO: reimplement - generates garbage
//        result.accept(arg1.getAsString().startsWith(ByteBufferUtils.parseString(arg2.getAsByteBuffer())));
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
//        result.accept(ByteBufferUtils.startWith(arg1.getAsByteBuffer(), arg2.getAsString()));
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        result.accept(ByteBufferUtils.startWith(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
//      } else {
//        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
//      }
//    });

    // TODO: finish it
//    ctx.defineFunction("endsWith", (result, arg1, arg2) -> {
//      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
//        result.accept(arg1.getAsString().endsWith(arg2.getAsString()));
//      } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        // TODO: reimplement - generates garbage
//        result.accept(arg1.getAsString().endsWith(ByteBufferUtils.parseString(arg2.getAsByteBuffer())));
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
//        result.accept(ByteBufferUtils.endWith(arg1.getAsByteBuffer(), arg2.getAsString()));
//      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
//        result.accept(ByteBufferUtils.endWith(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
//      } else {
//        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
//      }
//    });
  }

  @Override
  public MutableVariant get(Token token, MutableVariant result) {
    return ctx.get(token, result);
  }

  @Override
  public Object getFunction(Token token) {
    return ctx.getFunction(token);
  }

  @Override
  public String toString() {
    return ctx.toString();
  }
}
