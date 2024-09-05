package com.tereigo.expr;

import com.tereigo.expr.utils.ByteBufferUtils;
import com.tereigo.expr.variant.VariantUtils;

/*
  Provides Expr native functions

 TODO:
     add the following functions: equals, equalsIgnoreCase, contains, containsIgnoreCase, startsWith, startsWithIgnoreCase,
     endsWith, endsWithIgnoreCase, indexOfWith, indexOfIgnoreCase

     add all Math functions

 */
class ExprContextNativeEnricher implements ExprContextEnricher {
  static final ExprContextEnricher INSTANCE = new ExprContextNativeEnricher();

  private ExprContextNativeEnricher() { }

  public static ExprContextEnricher get() {
    return INSTANCE;
  }

  @Override
  public void enrich(MutableExprContext ctx) {
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

    // TODO: test!!!
    ctx.defineFunction("equals", (result, arg1, arg2) -> {
      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
        result.accept(arg1.getAsString().equals(arg2.getAsString()));
      } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
        result.accept(ByteBufferUtils.equals(arg1.getAsString(), arg2.getAsByteBuffer()));
      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
        result.accept(ByteBufferUtils.equals(arg1.getAsByteBuffer(), arg2.getAsString()));
      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
        result.accept(ByteBufferUtils.equals(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
      } else {
        throw new RuntimeException("Operands must be a STRING or BYTE_BUFFER");
      }
    });

    // TODO: test it
    ctx.defineFunction("equalsIgnoreCase", (result, arg1, arg2) -> {
      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
        result.accept(arg1.getAsString().equalsIgnoreCase(arg2.getAsString()));
      } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
        result.accept(ByteBufferUtils.equalsIgnoreCase(arg1.getAsString(), arg2.getAsByteBuffer()));
      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
        result.accept(ByteBufferUtils.equalsIgnoreCase(arg1.getAsByteBuffer(), arg2.getAsString()));
      } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
        result.accept(ByteBufferUtils.equalsIgnoreCase(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
      } else {
        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
      }
    });

    ctx.defineFunction("contains", (result, arg1, arg2) -> {
      if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
        result.accept(arg1.getAsString().contains(arg2.getAsString()));
      } else {
        throw new RuntimeException("Operand must be a STRING");
      }
    });

    // TODO: add containsIgnoreCase, startsWith, startsWithIgnoreCase, endsWith, endsWithIgnoreCase, indexOf, indexOfIgnoreCase

    ctx.defineFunction("contains", (result, arg1, arg2) -> {
      // alternative syntax. I'm not sure it's better
      if (VariantUtils.isString(arg1)) {
        if (VariantUtils.isString(arg2)) {
          result.accept(arg1.getAsString().contains(arg2.getAsString()));
        } else if (VariantUtils.isByteBuffer(arg2)) {
          result.accept(ByteBufferUtils.contains(arg1.getAsString(), arg2.getAsByteBuffer()));
        } else {
          throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
        }
      } else if (VariantUtils.isByteBuffer(arg1)) {
        if (VariantUtils.isString(arg2)) {
          result.accept(ByteBufferUtils.contains(arg1.getAsByteBuffer(), arg2.getAsString()));
        } else if (VariantUtils.isByteBuffer(arg2)) {
          result.accept(ByteBufferUtils.contains(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
        } else {
          throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
        }
      } else {
        throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
      }
    });

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

    ctx.defineFunction("percentOf", (result, pct, value) -> {
      if (VariantUtils.isNumber(pct) && VariantUtils.isNumber(value)) {
        result.accept(value.getAsNumber() * pct.getAsNumber() / 100.0);
      } else {
        throw new RuntimeException("Operands must be LONG or DOUBLE");
      }
    });

    ctx.addAlias("percentOf", "pctOf");
  }
  
}
