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
    private static final ExprContextEnricher INSTANCE = new ExprContextNativeEnricher();

    private ExprContextNativeEnricher() { }

    public static ExprContextEnricher get() {
        return INSTANCE;
    }

    @Override
    public void enrich(final ExprContextBuilder ctx) {

        ctx.addFunction("round", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept(Math.round(arg.getAsDouble()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.addFunction("roundToNearest", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                final double delta = arg.getAsDouble() > 0.0 ? 0.5 : -0.5;
                result.accept((long) (arg.getAsDouble() + delta));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.addFunction("roundUp", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept((long) (Math.ceil(arg.getAsDouble())));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.addFunction("roundDown", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept((long) (Math.floor(arg.getAsDouble())));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.addFunction("toLong", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(arg.getAsLong());
            } else if (VariantUtils.isDouble(arg)) {
                result.accept((long) (arg.getAsDouble()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.addFunction("toDouble", (result, arg) -> result.accept(arg.getAsNumber()));

        ///////////////////////////////////////////////////////////////////
        // Math functions
        ///////////////////////////////////////////////////////////////////

        ctx.addFunction("sin", (result, arg) -> result.accept(Math.sin(arg.getAsNumber())));

        ctx.addFunction("cos", (result, arg) -> result.accept(Math.cos(arg.getAsNumber())));

        ctx.addFunction("tan", (result, arg) -> result.accept(Math.tan(arg.getAsNumber())));

        ctx.addFunction("asin", (result, arg) -> result.accept(Math.asin(arg.getAsNumber())));

        ctx.addFunction("acos", (result, arg) -> result.accept(Math.acos(arg.getAsNumber())));

        ctx.addFunction("atan", (result, arg) -> result.accept(Math.atan(arg.getAsNumber())));

        ctx.addFunction("sinh", (result, arg) -> result.accept(Math.sinh(arg.getAsNumber())));

        ctx.addFunction("cosh", (result, arg) -> result.accept(Math.cosh(arg.getAsNumber())));

        ctx.addFunction("tanh", (result, arg) -> result.accept(Math.tanh(arg.getAsNumber())));

        ctx.addFunction("hypot", (result, arg1, arg2) -> result.accept(Math.hypot(arg1.getAsNumber(), arg2.getAsNumber())));

        ctx.addFunction("toRadians", (result, arg) -> result.accept(Math.toRadians(arg.getAsNumber())));

        ctx.addFunction("toDegrees", (result, arg) -> result.accept(Math.toDegrees(arg.getAsNumber())));

        ctx.addFunction("exp", (result, arg) -> result.accept(Math.exp(arg.getAsNumber())));

        ctx.addFunction("expm1", (result, arg) -> result.accept(Math.expm1(arg.getAsNumber())));

        ctx.addFunction("log", (result, arg) -> result.accept(Math.log(arg.getAsNumber())));

        ctx.addFunction("log10", (result, arg) -> result.accept(Math.log10(arg.getAsNumber())));

        ctx.addFunction("log1p", (result, arg) -> result.accept(Math.log1p(arg.getAsNumber())));

        ctx.addFunction("sqrt", (result, arg) -> result.accept(Math.sqrt(arg.getAsNumber())));

        ctx.addFunction("cbrt", (result, arg) -> result.accept(Math.cbrt(arg.getAsNumber())));

        ctx.addFunction("ceil", (result, arg) -> result.accept(Math.ceil(arg.getAsNumber())));

        ctx.addFunction("floor", (result, arg) -> result.accept(Math.floor(arg.getAsNumber())));

        ctx.addFunction("rint", (result, arg) -> result.accept(Math.rint(arg.getAsNumber())));

        ctx.addFunction("atan2", (result, arg1, arg2) -> result.accept(Math.atan2(arg1.getAsNumber(), arg2.getAsNumber())));

        ctx.addFunction("pow", (result, arg1, arg2) -> result.accept(Math.pow(arg1.getAsNumber(), arg2.getAsNumber())));

        ctx.addFunction("random", (result) -> result.accept(Math.random()));

        ctx.addFunction("min", (result, arg1, arg2) -> {
            if (VariantUtils.isLong(arg1) && VariantUtils.isLong(arg2)) {
                result.accept(Math.min(arg1.getAsLong(), arg2.getAsLong()));
            } else if (VariantUtils.isNumber(arg1) && VariantUtils.isNumber(arg2)) {
                result.accept(Math.min(arg1.getAsNumber(), arg2.getAsNumber()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ctx.addFunction("max", (result, arg1, arg2) -> {
            if (VariantUtils.isLong(arg1) && VariantUtils.isLong(arg2)) {
                result.accept(Math.max(arg1.getAsLong(), arg2.getAsLong()));
            } else if (VariantUtils.isNumber(arg1) && VariantUtils.isNumber(arg2)) {
                result.accept(Math.max(arg1.getAsNumber(), arg2.getAsNumber()));
            } else {
                throw new RuntimeException("Operands must be numbers");
            }
        });

        ctx.addFunction("abs", (result, arg) -> {
            if (VariantUtils.isLong(arg)) {
                result.accept(Math.abs(arg.getAsLong()));
            } else if (VariantUtils.isDouble(arg)) {
                result.accept(Math.abs(arg.getAsDouble()));
            } else {
                throw new RuntimeException("Operand must be a number");
            }
        });

        ctx.addFunction("signum", (result, arg) -> result.accept(Math.signum(arg.getAsNumber())));

        ctx.addFunction("getExponent", (result, arg) -> result.accept(Math.getExponent(arg.getAsNumber())));

        ctx.addFunction("scalb", (result, arg1, arg2) -> result.accept(Math.scalb(arg1.getAsNumber(), (int) arg2.getAsLong())));

        ///////////////////////////////////////////////////////////////////
        // String functions
        ///////////////////////////////////////////////////////////////////

        ctx.addFunction("isEmpty", (result, arg) -> {
            if (VariantUtils.isString(arg)) {
                result.accept(arg.getAsString().isEmpty());
            } else if (VariantUtils.isByteBuffer(arg)) {
                result.accept(ByteBufferUtils.isEmpty(arg.getAsByteBuffer()));
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

        ctx.addFunction("length", (result, arg) -> {
            if (VariantUtils.isString(arg)) {
                result.accept(arg.getAsString().length());
            } else if (VariantUtils.isByteBuffer(arg)) {
                result.accept(arg.getAsByteBuffer().remaining());
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

        // TODO: test!!!
        ctx.addFunction("equals", (result, arg1, arg2) -> {
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
        ctx.addFunction("equalsIgnoreCase", (result, arg1, arg2) -> {
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

        // TODO: add containsIgnoreCase, indexOfIgnoreCase

        ctx.addFunction("contains", (result, arg1, arg2) -> {
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

        ctx.addFunction("startsWith", (result, arg1, arg2) -> {
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

        ctx.addFunction("startsWithIgnoreCase", (result, arg1, arg2) -> {
            if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
                result.accept(ByteBufferUtils.startsWithIgnoreCase(arg1.getAsString(), arg2.getAsString()));
            } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.startsWithIgnoreCase(arg1.getAsString(), arg2.getAsByteBuffer()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
                result.accept(ByteBufferUtils.startsWithIgnoreCase(arg1.getAsByteBuffer(), arg2.getAsString()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.startsWithIgnoreCase(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

        ctx.addFunction("endsWith", (result, arg1, arg2) -> {
            if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
                result.accept(arg1.getAsString().endsWith(arg2.getAsString()));
            } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.endsWith(arg1.getAsString(), arg2.getAsByteBuffer()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
                result.accept(ByteBufferUtils.endsWith(arg1.getAsByteBuffer(), arg2.getAsString()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.endsWith(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

        ctx.addFunction("endsWithIgnoreCase", (result, arg1, arg2) -> {
            if (VariantUtils.isString(arg1) && VariantUtils.isString(arg2)) {
                result.accept(ByteBufferUtils.endsWithIgnoreCase(arg1.getAsString(), arg2.getAsString()));
            } else if (VariantUtils.isString(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.endsWithIgnoreCase(arg1.getAsString(), arg2.getAsByteBuffer()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isString(arg2)) {
                result.accept(ByteBufferUtils.endsWithIgnoreCase(arg1.getAsByteBuffer(), arg2.getAsString()));
            } else if (VariantUtils.isByteBuffer(arg1) && VariantUtils.isByteBuffer(arg2)) {
                result.accept(ByteBufferUtils.endsWithIgnoreCase(arg1.getAsByteBuffer(), arg2.getAsByteBuffer()));
            } else {
                throw new RuntimeException("Operand must be a STRING or BYTE_BUFFER");
            }
        });

        ctx.addFunction("indexOf", (result, arg1, arg2) -> {
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
        ctx.addFunction("percentOf", (result, pct, value) -> result.accept(value.getAsNumber() * pct.getAsNumber() / 100.0));
        ctx.addAlias("percentOf", "pctOf");
    }

}
