package com.tereigo.atlas_expr.atlas.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class ByteBufferUtils {
    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0).asReadOnlyBuffer();

    private ByteBufferUtils() {}

    public static ByteBuffer constant(String from) {
        return ByteBuffer.wrap(from.getBytes()).asReadOnlyBuffer();
    }

    public static void parseString(ByteBuffer buffer, StringBuilder builder) {
        parseString(buffer, builder, buffer.remaining());
    }

    public static void parseString(ByteBuffer buffer, StringBuilder builder, int len) {
        int pos = buffer.position();
        int end = Math.min(buffer.remaining(), len);
        for (int i = 0; i < end; i++) {
            byte b = buffer.get();
            builder.append((char) (b& 0xFF));
        }
        buffer.position(pos);
    }

    public static String parseString(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return "";
        }
        byte[] bytes = getBytes(buffer);
        return new String(bytes, 0, buffer.remaining(), StandardCharsets.US_ASCII);
    }

    public static String parseString(ByteBuffer buffer, int len) {
        byte[] bytes = getBytes(buffer);
        return new String(bytes, 0, bytes.length, StandardCharsets.US_ASCII);
    }

    public static byte[] getBytes(ByteBuffer buffer) {
        return getBytes(buffer, buffer.remaining());
    }

    public static byte[] getBytes(ByteBuffer buffer, int len) {
        buffer.mark();
        byte[] bytes = new byte[Math.min(len, buffer.remaining())];
        buffer.get(bytes);
        buffer.reset();
        return bytes;
    }

    public static boolean isEmpty(ByteBuffer buffer) {
        return !buffer.hasRemaining();
    }

    public static ByteBuffer toByteBuffer(CharSequence data, ByteBuffer target) {
        target.clear();
        for (int i = 0; i < data.length(); i++) {
            target.put((byte)(data.charAt(i) & 0xFF));
        }
        target.flip();
        return target;
    }

    public static ByteBuffer toByteBuffer(CharSequence data, ByteBuffer target, int len) {
        target.clear();
        for (int i = 0; i < data.length() && i < len; i++) {
            target.put((byte)(data.charAt(i) & 0xFF));
        }
        target.flip();
        return target;
    }

    public static ByteBuffer toByteBufferSafe(CharSequence data, ByteBuffer target) {
        return toByteBuffer(data, target, target.capacity());
    }

    public static byte asciiByteToLower(byte b) {
        return (b >= 65 && b <= 90) ? (byte)(b+32) : b;
    }

    public static boolean startWith(ByteBuffer source, ByteBuffer key) {
        return startWith(source, key, Byte::compare);
    }

    public static boolean startWithCaseInsensitive(ByteBuffer source, ByteBuffer key) {
        return startWith(source, key, (lhs, rhs) -> Byte.compare(asciiByteToLower(lhs), asciiByteToLower(rhs)));
    }

    public static boolean startWith(ByteBuffer source, ByteBuffer key, ByteComparator comparator) {
        source.mark();
        key.mark();
        while (source.hasRemaining() && key.hasRemaining()) {
            if (comparator.compare(source.get(), key.get()) != 0) {
                source.reset();
                key.reset();
                return false;
            }
        }
        final boolean result = !key.hasRemaining();
        source.reset();
        key.reset();
        return result;
    }

    // tereni: Changed!!!
    public static boolean startWith(ByteBuffer source, CharSequence key) {
        source.mark();
        final int strSize = key.length();
        int i = 0;
        while (source.hasRemaining() && i < strSize) {
            if (source.get() != key.charAt(i)) {
                source.reset();
                return false;
            }
            ++i;
        }
        source.reset();
        return i == strSize;
    }

    public static boolean equals(ByteBuffer buffer, ByteBuffer other) {
        return equals(buffer, other, (byte lhs, byte rhs) -> Byte.compare(lhs, rhs));
    }

    public static boolean equalsCaseInsensitive(ByteBuffer buffer, ByteBuffer other) {
        return equals(buffer, other, (byte lhs, byte rhs) -> Byte.compare(asciiByteToLower(lhs), asciiByteToLower(rhs)));
    }

    public static boolean equals(ByteBuffer buffer, ByteBuffer other, ByteComparator comparator) {
        if (buffer == other) {
            return true;
        }
        if (buffer != null) {
            return (buffer.remaining() == other.remaining()
                    && startWith(buffer, other, comparator));
        } else {
            return false;
        }
    }

    public static boolean equals(ByteBuffer buffer, CharSequence chars) {
        if (buffer == null && chars == null) {
            return true;
        }

        if (buffer != null && chars != null) {
            int pos = buffer.position();
            int lim = buffer.limit();
            if (chars.length() != (lim - pos)) {
                return false;
            }

            for (int i = 0; i < chars.length(); i++) {
                if (chars.charAt(i) != buffer.get(pos + i)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static int compare(ByteBuffer buffer, ByteBuffer other) {
        return compare(buffer, other, (byte lhs, byte rhs) -> Byte.compare(lhs, rhs));
    }

    public static int compareCaseInsensitive(ByteBuffer buffer, ByteBuffer other) {
        return compare(buffer, other, (byte lhs, byte rhs) -> Byte.compare(asciiByteToLower(lhs), asciiByteToLower(rhs)));
    }

    public static int compare(ByteBuffer buffer, ByteBuffer other, ByteComparator comparator) {
        if (buffer == null && other == null) {
            return 0;
        } else if (buffer == null) {
            return -1;
        } else if (other == null) {
            return 1;
        }
        int n = buffer.position() + Math.min(buffer.remaining(), other.remaining());
        for (int i = buffer.position(), j = other.position(); i < n; i++, j++) {
            int cmp = comparator.compare(buffer.get(i), other.get(j));
            if (cmp != 0) {
                return cmp;
            }
        }
        return buffer.remaining() - other.remaining();
    }

    public static void toString(ByteBuffer buffer, StringBuilder sb) {
        int pos = buffer.position();
        int lim = buffer.limit();
        while (pos < lim) {
            sb.append((char) buffer.get(pos++));
        }
    }

    public static String toString(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder(buffer.remaining());
        toString(buffer, sb);
        return sb.toString();
    }
}

