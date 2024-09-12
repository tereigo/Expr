package com.tereigo.expr.utils;

import com.tereigo.expr.annotations.GeneratesGarbage;
import com.tereigo.expr.function.ByteComparator;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public final class ByteBufferUtils {
    private static final ByteBuffer EMPTY = ByteBuffer.allocate(0).asReadOnlyBuffer();

    private static final ByteComparator BYTE_CASE_SENSITIVE_COMPARATOR = Byte::compare;
    private static final ByteComparator BYTE_CASE_INSENSITIVE_COMPARATOR = (lhs, rhs) -> Byte.compare(asciiByteToLower(lhs), asciiByteToLower(rhs));

    private static final Comparator<ByteBuffer> BB_CASE_SENSITIVE_COMPARATOR = ByteBufferUtils::compare;
    private static final Comparator<ByteBuffer> BB_CASE_INSENSITIVE_COMPARATOR = ByteBufferUtils::compareCaseInsensitive;

    private ByteBufferUtils() {
    }

    @GeneratesGarbage
    public static ByteBuffer constant(final String from) {
        return ByteBuffer.wrap(from.getBytes()).asReadOnlyBuffer();
    }

    public static ByteBuffer empty() {
        return EMPTY;
    }

    public static void parseString(final ByteBuffer buffer, final StringBuilder builder) {
        parseString(buffer, builder, buffer.remaining());
    }

    public static void parseString(final ByteBuffer buffer, final StringBuilder builder, final int len) {
        final int pos = buffer.position();
        final int end = Math.min(buffer.remaining(), len);
        for (int i = 0; i < end; i++) {
            final byte b = buffer.get();
            builder.append((char) (b & 0xFF));
        }
        buffer.position(pos);
    }

    @GeneratesGarbage
    public static String parseString(final ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return "";
        }
        final byte[] bytes = getBytes(buffer);
        return new String(bytes, 0, buffer.remaining(), StandardCharsets.US_ASCII);
    }

    @GeneratesGarbage
    public static String parseString(final ByteBuffer buffer, final int len) {
        final byte[] bytes = getBytes(buffer, len);
        return new String(bytes, 0, bytes.length, StandardCharsets.US_ASCII);
    }

    @GeneratesGarbage
    public static byte[] getBytes(final ByteBuffer buffer) {
        return getBytes(buffer, buffer.remaining());
    }

    @GeneratesGarbage
    public static byte[] getBytes(final ByteBuffer buffer, final int len) {
        final int pos = buffer.position();
        final byte[] bytes = new byte[Math.min(len, buffer.remaining())];
        buffer.get(bytes);
        buffer.position(pos);
        return bytes;
    }

    public static boolean isEmpty(final ByteBuffer buffer) {
        return !buffer.hasRemaining();
    }

    public static void setToEmpty(final ByteBuffer buffer) {
        buffer.clear().flip();
    }

    public static ByteBuffer toByteBufferSafe(final CharSequence data, final ByteBuffer target) {
        return toByteBuffer(data, target, target.capacity());
    }

    public static ByteBuffer toByteBuffer(final CharSequence data, final ByteBuffer target) {
        return toByteBuffer(data, target, data.length());
    }

    public static ByteBuffer toByteBuffer(final CharSequence data, final ByteBuffer target, final int len) {
        target.clear();
        final int max = Math.min(data.length(), len);
        for (int i = 0; i < max; i++) {
            target.put((byte) (data.charAt(i) & 0xFF));
        }
        target.flip();
        return target;
    }

    public static ByteBuffer deepCopy(final ByteBuffer source, final ByteBuffer target) {
        if (target == null) {
            return null;
        }
        final int srcP = source.position();
        final int srcL = source.limit();
        try {
            target.put(source);
            target.flip();
            source.position(srcP);
            source.limit(srcL);
            return target;
        } catch (final BufferOverflowException e) {
            throw new IllegalArgumentException("Failed to copy ByteBuffers");
        }
    }

    @GeneratesGarbage
    public static ByteBuffer clone(final ByteBuffer original) {
        final ByteBuffer clone = (original.isDirect()) ?
                ByteBuffer.allocateDirect(original.capacity()) :
                ByteBuffer.allocate(original.capacity());

        final int pos = original.position();
        clone.put(original);
        original.position(pos);

        clone.flip();
        return clone;
    }

    public static byte asciiByteToLower(final byte b) {
        return (b >= 65 && b <= 90) ? (byte) (b + 32) : b;
    }

    public static boolean startsWith(final ByteBuffer source, final ByteBuffer key) {
        return startsWith(source, key, BYTE_CASE_SENSITIVE_COMPARATOR);
    }

    public static boolean startsWithCaseInsensitive(final ByteBuffer source, final ByteBuffer key) {
        return startsWith(source, key, BYTE_CASE_INSENSITIVE_COMPARATOR);
    }

    public static boolean startsWith(final ByteBuffer source, final ByteBuffer key, final ByteComparator comparator) {
        final int keySize = key.remaining();
        if (keySize > source.remaining()) {
            return false;
        }
        for (int i = 0; i < keySize; i++) {
            if (comparator.compare(source.get(i), key.get(i)) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean startsWith(final ByteBuffer source, final CharSequence key) {
        return startsWith(source, key, BYTE_CASE_SENSITIVE_COMPARATOR);
    }

    public static boolean startsWithCaseInsensitive(final ByteBuffer source, final CharSequence key) {
        return startsWith(source, key, BYTE_CASE_INSENSITIVE_COMPARATOR);
    }

    public static boolean startsWith(final ByteBuffer source, final CharSequence key, final ByteComparator comparator) {
        final int keySize = key.length();
        if (keySize > source.remaining()) {
            return false;
        }
        for (int i = 0; i < keySize; i++) {
            if (comparator.compare(source.get(i), (byte) (key.charAt(i) & 0xFF)) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean startsWith(final CharSequence source, final ByteBuffer key) {
        return startsWith(source, key, BYTE_CASE_SENSITIVE_COMPARATOR);
    }

    public static boolean startsWithCaseInsensitive(final CharSequence source, final ByteBuffer key) {
        return startsWith(source, key, BYTE_CASE_INSENSITIVE_COMPARATOR);
    }

    public static boolean startsWith(final CharSequence source, final ByteBuffer key, final ByteComparator comparator) {
        final int keySize = key.remaining();
        if (keySize > source.length()) {
            return false;
        }
        for (int i = 0; i < keySize; i++) {
            if (comparator.compare((byte) (source.charAt(i) & 0xFF), key.get(i)) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(final ByteBuffer buffer, final ByteBuffer other) {
        return equals(buffer, other, BYTE_CASE_SENSITIVE_COMPARATOR);
    }

    public static boolean equals(final CharSequence str, final ByteBuffer buffer) {
        return equals(buffer, str, BYTE_CASE_SENSITIVE_COMPARATOR);
    }

    public static boolean equalsIgnoreCase(final ByteBuffer buffer, final ByteBuffer other) {
        return equals(buffer, other, BYTE_CASE_INSENSITIVE_COMPARATOR);
    }

    public static boolean equalsIgnoreCase(final ByteBuffer buffer, final CharSequence str) {
        return equals(buffer, str, BYTE_CASE_INSENSITIVE_COMPARATOR);
    }

    public static boolean equalsIgnoreCase(final CharSequence str, final ByteBuffer buffer) {
        return equals(buffer, str, BYTE_CASE_INSENSITIVE_COMPARATOR);
    }

    public static boolean equals(final ByteBuffer buffer, final ByteBuffer other, final ByteComparator comparator) {
        if (buffer == other) {
            return true;
        }
        if (buffer != null) {
            return (buffer.remaining() == other.remaining()
                    && startsWith(buffer, other, comparator));
        } else {
            return false;
        }
    }

    public static boolean equals(final ByteBuffer buffer, final CharSequence str, final ByteComparator comparator) {
        if (buffer == null && str == null) {
            return true;
        }
        if (buffer != null && str != null) {
            final int pos = buffer.position();
            final int lim = buffer.limit();
            if (str.length() != (lim - pos)) {
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                if (comparator.compare((byte) (str.charAt(i) & 0xFF), buffer.get(pos + i)) != 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean equals(final ByteBuffer buffer, final CharSequence chars) {
        if (buffer == null && chars == null) {
            return true;
        }

        if (buffer != null && chars != null) {
            final int pos = buffer.position();
            final int lim = buffer.limit();
            if (chars.length() != (lim - pos)) {
                return false;
            }

            for (int i = 0; i < chars.length(); i++) {
                if ((chars.charAt(i) & 0xFF) != buffer.get(pos + i)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static int compare(final ByteBuffer buffer, final ByteBuffer other) {
        return compare(buffer, other, BYTE_CASE_SENSITIVE_COMPARATOR);
    }

    public static int compareCaseInsensitive(final ByteBuffer buffer, final ByteBuffer other) {
        return compare(buffer, other, BYTE_CASE_INSENSITIVE_COMPARATOR);
    }

    public static int compare(final ByteBuffer buffer, final ByteBuffer other, final ByteComparator comparator) {
        if (buffer == null && other == null) {
            return 0;
        } else if (buffer == null) {
            return -1;
        } else if (other == null) {
            return 1;
        }
        final int n = buffer.position() + Math.min(buffer.remaining(), other.remaining());
        for (int i = buffer.position(), j = other.position(); i < n; i++, j++) {
            final int cmp = comparator.compare(buffer.get(i), other.get(j));
            if (cmp != 0) {
                return cmp;
            }
        }
        return buffer.remaining() - other.remaining();
    }

    // TODO: is it supposed to change buffer.position?
    public static void toString(final ByteBuffer buffer, final StringBuilder sb) {
        int pos = buffer.position();
        final int lim = buffer.limit();
        while (pos < lim) {
            sb.append((char) buffer.get(pos++));
        }
    }

    @GeneratesGarbage
    public static String toString(final ByteBuffer buffer) {
        final StringBuilder sb = new StringBuilder(buffer.remaining());
        toString(buffer, sb);
        return sb.toString();
    }

    public static boolean contains(final ByteBuffer str, final String pattern) {
        return indexOf(str, pattern) > -1;
    }

    public static boolean contains(final String str, final ByteBuffer pattern) {
        return indexOf(str, pattern) > -1;
    }

    public static boolean contains(final ByteBuffer str, final ByteBuffer pattern) {
        return indexOf(str, pattern) > -1;
    }

    public static int indexOf(final ByteBuffer str, final String pattern) {
        if (pattern.isEmpty()) {
            return 0;
        }

        final byte firstByte = (byte) (pattern.charAt(0) & 0xFF);
        final int max = str.remaining() - pattern.length();

        for (int i = 0; i <= max; i++) {
            // search for the first same character in str
            if (str.get(i) != firstByte) {
                while (++i <= max && str.get(i) != firstByte) ;
            }

            if (i <= max) {
                int j = i + 1;
                final int end = j + pattern.length() - 1;
                for (int k = 1; j < end && str.get(j) == (byte) (pattern.charAt(k) & 0xFF); j++, k++) ;

                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOf(final String str, final ByteBuffer pattern) {
        if (!pattern.hasRemaining()) {
            return 0;
        }

        final byte firstByte = pattern.get(0);
        final int max = str.length() - pattern.remaining();

        for (int i = 0; i <= max; i++) {
            // search for the first same character in str
            if ((byte) (str.charAt(i) & 0xFF) != firstByte) {
                while (++i <= max && (byte) (str.charAt(i) & 0xFF) != firstByte) ;
            }

            if (i <= max) {
                int j = i + 1;
                final int end = j + pattern.remaining() - 1;
                for (int k = 1; j < end && (byte) (str.charAt(j) & 0xFF) == pattern.get(k); j++, k++) ;

                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOf(final ByteBuffer str, final ByteBuffer pattern) {
        if (!pattern.hasRemaining()) {
            return 0;
        }

        final byte firstByte = pattern.get(0);
        final int max = str.remaining() - pattern.remaining();

        for (int i = 0; i <= max; i++) {
            // search for the first same character in str
            if (str.get(i) != firstByte) {
                while (++i <= max && str.get(i) != firstByte) ;
            }

            if (i <= max) {
                int j = i + 1;
                final int end = j + pattern.remaining() - 1;
                for (int k = 1; j < end && str.get(j) == pattern.get(k); j++, k++) ;

                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }
}

