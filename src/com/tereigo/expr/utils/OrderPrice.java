package com.tereigo.expr.utils;

public final class OrderPrice {

    public static final long NO_LIMIT_PRICE = Long.MIN_VALUE;
    public static final long INVALID_PRICE = Long.MIN_VALUE + 1;

    public static boolean isMarket(long price) {
        return price == NO_LIMIT_PRICE;
    }

    public static boolean isLimit(long price) {
        return isValid(price) && !isMarket(price);
    }

    public static boolean isValid(long price) {
        return price != INVALID_PRICE;
    }
}
