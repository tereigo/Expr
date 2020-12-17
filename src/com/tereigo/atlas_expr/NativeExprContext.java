package com.tereigo.atlas_expr;

import com.tereigo.atlas_expr.atlas.utils.OrderPrice;
import com.tereigo.atlas_expr.atlas.utils.PriceUtils;
import com.tereigo.atlas_expr.variant.MutableVariant;
import com.tereigo.atlas_expr.variant.VariantUtils;

/*
  Provides Expr native functions:
  1. min(arg1, arg2)
  2. max(arg1, arg2)
  3. abs(arg1)
 */
class NativeExprContext implements ExprContext {
  static final ExprContext INSTANCE = new NativeExprContext();

  private final ExprContextImpl ctx = new ExprContextImpl();

  private NativeExprContext() {
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
  }

  @Override
  public MutableVariant get(Token token, MutableVariant result) {
    return ctx.get(token, result);
  }

  @Override
  public Object getFunction(Token token) {
    return ctx.getFunction(token);
  }
}
