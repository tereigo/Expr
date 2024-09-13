package com.tereigo.expr.impl;

import com.tereigo.expr.ExprContextBuilder;
import com.tereigo.expr.ExprContextEnricher;
import com.tereigo.expr.utils.ByteBufferUtils;
import com.tereigo.expr.variant.VariantUtils;

/*
  Provides Expr native functions

 TODO:
     add the following functions: equals, equalsIgnoreCase, containsIgnoreCase, startsWithIgnoreCase,
     endsWith, endsWithIgnoreCase, indexOfIgnoreCase

 */
final class ExprContextNativeEnricher implements ExprContextEnricher {
    static final ExprContextEnricher INSTANCE = new ExprContextNativeEnricher();

    private ExprContextNativeEnricher() {
    }

    public static ExprContextEnricher get() {
        return INSTANCE;
    }

    @Override
    public void enrich(final ExprContextBuilder ctx) {

        ctx.defineFunction("round", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept(Math.round(arg.getAsDouble()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("roundToNearest", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                final double delta = arg.getAsDouble() > 0.0 ? 0.5 : -0.5;
                result.accept((long) (arg.getAsDouble() + delta));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("roundUp", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept((long) (Math.ceil(arg.getAsDouble())));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("roundDown", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept((long) (Math.floor(arg.getAsDouble())));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("toLong", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept((long) (arg.getAsDouble()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("toDouble", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(arg.getAsNumber());
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ///////////////////////////////////////////////////////////////////
        // Math functions
        ///////////////////////////////////////////////////////////////////

        ctx.defineFunction("sin", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.sin(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("cos", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.cos(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("tan", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.tan(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("asin", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.asin(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("acos", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.acos(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("atan", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.atan(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("sinh", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.sinh(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("cosh", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.cosh(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("tanh", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.tanh(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("hypot", (result, arg1, arg2) -> {
            if (VariantUtils.isNumber(arg1) && VariantUtils.isNumber(arg2)) {
                result.accept(Math.hypot(arg1.getAsNumber(), arg2.getAsNumber()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ctx.defineFunction("toRadians", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.toRadians(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("toDegrees", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.toDegrees(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("exp", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.exp(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("expm1", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.expm1(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("log", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.log(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("log10", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.log10(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("log1p", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.log1p(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("sqrt", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.sqrt(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("cbrt", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.cbrt(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("ceil", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.ceil(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("floor", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.floor(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("rint", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.rint(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("atan2", (result, arg1, arg2) -> {
            if (VariantUtils.isNumber(arg1) && VariantUtils.isNumber(arg2)) {
                result.accept(Math.atan2(arg1.getAsNumber(), arg2.getAsNumber()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ctx.defineFunction("pow", (result, arg1, arg2) -> {
            if (VariantUtils.isNumber(arg1) && VariantUtils.isNumber(arg2)) {
                result.accept(Math.pow(arg1.getAsNumber(), arg2.getAsNumber()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ctx.defineFunction("random", (result) -> result.accept(Math.random()));

        ctx.defineFunction("min", (result, arg1, arg2) -> {
            if (VariantUtils.isLong(arg1) && VariantUtils.isLong(arg2)) {
                result.accept(Math.min(arg1.getAsLong(), arg2.getAsLong()));
            } else if (VariantUtils.isNumber(arg1) && VariantUtils.isNumber(arg2)) {
                result.accept(Math.min(arg1.getAsNumber(), arg2.getAsNumber()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ctx.defineFunction("max", (result, arg1, arg2) -> {
            if (VariantUtils.isLong(arg1) && VariantUtils.isLong(arg2)) {
                result.accept(Math.max(arg1.getAsLong(), arg2.getAsLong()));
            } else if (VariantUtils.isNumber(arg1) && VariantUtils.isNumber(arg2)) {
                result.accept(Math.max(arg1.getAsNumber(), arg2.getAsNumber()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ctx.defineFunction("abs", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(Math.abs(arg.getAsLong()));
            } else if (VariantUtils.isDouble(arg)) {
                result.accept(Math.abs(arg.getAsDouble()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("signum", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.signum(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("getExponent", (result, arg) -> {
            if (VariantUtils.isNumber(arg)) {
                result.accept(Math.getExponent(arg.getAsNumber()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.defineFunction("scalb", (result, arg1, arg2) -> {
            if (VariantUtils.isNumber(arg1) && VariantUtils.isLong(arg2)) {
                result.accept(Math.scalb(arg1.getAsNumber(), (int) arg2.getAsLong()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ///////////////////////////////////////////////////////////////////
        // String functions
        ///////////////////////////////////////////////////////////////////

        ctx.defineFunction("isEmpty", (result, arg) -> {
            if (VariantUtils.isString(arg)) {
                result.accept(arg.getAsString().isEmpty());
            } else if (VariantUtils.isByteBuffer(arg)) {
                result.accept(ByteBufferUtils.isEmpty(arg.getAsByteBuffer()));
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

        ctx.defineFunction("length", (result, arg) -> {
            if (VariantUtils.isString(arg)) {
                result.accept(arg.getAsString().length());
            } else if (VariantUtils.isByteBuffer(arg)) {
                result.accept(arg.getAsByteBuffer().remaining());
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

        // TODO: add containsIgnoreCase, startsWith, startsWithIgnoreCase, endsWith, endsWithIgnoreCase, indexOfIgnoreCase

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

        ctx.defineFunction("startsWith", (result, arg1, arg2) -> {
            if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
                result.accept(arg1.getAsString().startsWith(arg2.getAsString()));
            } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.startsWith(arg1.getAsString(), arg2.getAsByteBuffer()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
                result.accept(ByteBufferUtils.startsWith(arg1.getAsByteBuffer(), arg2.getAsString()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.startsWith(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

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

        ctx.defineFunction("indexOf", (result, arg1, arg2) -> {
            // alternative syntax. I'm not sure it's better
            if (VariantUtils.isString(arg1)) {
                if (VariantUtils.isString(arg2)) {
                    result.accept(arg1.getAsString().indexOf(arg2.getAsString()));
                } else if (VariantUtils.isByteBuffer(arg2)) {
                    result.accept(ByteBufferUtils.indexOf(arg1.getAsString(), arg2.getAsByteBuffer()));
                } else {
                    throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
                }
            } else if (VariantUtils.isByteBuffer(arg1)) {
                if (VariantUtils.isString(arg2)) {
                    result.accept(ByteBufferUtils.indexOf(arg1.getAsByteBuffer(), arg2.getAsString()));
                } else if (VariantUtils.isByteBuffer(arg2)) {
                    result.accept(ByteBufferUtils.indexOf(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
                } else {
                    throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
                }
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

        // "percentOf(5, 1000) == 50 or 5.pctOf(1000) == 50"
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
