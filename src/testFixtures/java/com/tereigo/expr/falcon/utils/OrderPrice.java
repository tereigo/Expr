package com.tereigo.expr.falcon.utils;

public final class OrderPrice {

    private static final long MARKET_PRICE = Long.MIN_VALUE;
    private static final long INVALID_PRICE = Long.MIN_VALUE + 1;

    public static boolean isMarket(long price) {
        return price == MARKET_PRICE;
    }

    public static boolean isLimit(long price) {
        return isValid(price) && !isMarket(price);
    }

    public static boolean isValid(long price) {
        return price != INVALID_PRICE;
    }

    public static boolean nonValid(long price) {
        return !isValid(price);
    }

    public static long market() {
        return MARKET_PRICE;
    }

    public static long invalid() {
        return INVALID_PRICE;
    }
}
