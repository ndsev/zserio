package zserio.runtime;

import java.math.BigInteger;

import zserio.runtime.array.Array;
import zserio.runtime.io.BitBuffer;

/**
 * Utilities for ordering of Zserio objects.
 *
 * All compare methods return a negative integer, zero, or a positive integer as the first value is less than,
 * equal to, or greater than the second value. A null value is less than any non-null value.
 */
public final class CompareUtil
{
    /**
     * Compares two boolean values, false is less than true.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(boolean value1, boolean value2)
    {
        return Boolean.compare(value1, value2);
    }

    /**
     * Compares two Boolean values, false is less than true.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Boolean value1, Boolean value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compare(value1.booleanValue(), value2.booleanValue());
    }

    /**
     * Compares two byte values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(byte value1, byte value2)
    {
        return Byte.compare(value1, value2);
    }

    /**
     * Compares two Byte values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Byte value1, Byte value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compare(value1.byteValue(), value2.byteValue());
    }

    /**
     * Compares two short values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(short value1, short value2)
    {
        return Short.compare(value1, value2);
    }

    /**
     * Compares two Short values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Short value1, Short value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compare(value1.shortValue(), value2.shortValue());
    }

    /**
     * Compares two int values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(int value1, int value2)
    {
        return Integer.compare(value1, value2);
    }

    /**
     * Compares two Integer values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Integer value1, Integer value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compare(value1.intValue(), value2.intValue());
    }

    /**
     * Compares two long values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(long value1, long value2)
    {
        return Long.compare(value1, value2);
    }

    /**
     * Compares two Long values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Long value1, Long value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compare(value1.longValue(), value2.longValue());
    }

    /**
     * Compares two float values using Float.compare() which is consistent with equals().
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(float value1, float value2)
    {
        return Float.compare(value1, value2);
    }

    /**
     * Compares two Float values using Float.compare() which is consistent with equals().
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Float value1, Float value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compare(value1.floatValue(), value2.floatValue());
    }

    /**
     * Compares two double values using Double.compare() which is consistent with equals().
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(double value1, double value2)
    {
        return Double.compare(value1, value2);
    }

    /**
     * Compares two Double values using Double.compare() which is consistent with equals().
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Double value1, Double value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compare(value1.doubleValue(), value2.doubleValue());
    }

    /**
     * Compares two BigInteger values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(BigInteger value1, BigInteger value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return value1.compareTo(value2);
    }

    /**
     * Compares two String values by Unicode code points.
     *
     * This gives the same order as the lexicographical compare of the UTF-8 encoded strings.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(String value1, String value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        int index1 = 0;
        int index2 = 0;
        while (index1 < value1.length() && index2 < value2.length())
        {
            final int codePoint1 = value1.codePointAt(index1);
            final int codePoint2 = value2.codePointAt(index2);
            if (codePoint1 != codePoint2)
                return Integer.compare(codePoint1, codePoint2);

            index1 += Character.charCount(codePoint1);
            index2 += Character.charCount(codePoint2);
        }

        return Boolean.compare(index1 < value1.length(), index2 < value2.length());
    }

    /**
     * Compares two bytes values lexicographically, bytes are compared as unsigned values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(byte[] value1, byte[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = Integer.compare(value1[i] & 0xFF, value2[i] & 0xFF);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two BitBuffer values.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(BitBuffer value1, BitBuffer value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return value1.compareTo(value2);
    }

    /**
     * Compares two ZserioEnum values by their underlying values.
     *
     * Note that this differs from the natural ordering of Java enums which uses the declaration order.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     * @param <T> Concrete Java type implementing ZserioEnum.
     *
     * @return Result of the comparison.
     */
    public static <T extends ZserioEnum & SizeOf> int compare(T value1, T value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return compareEnumValues(value1, value2);
    }

    /**
     * Compares two SizeOf values using their natural ordering.
     *
     * Note: This is intended to be used by generated objects (including ZserioBitmask) which implement
     * Comparable.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     * @param <T> Concrete Java type implementing SizeOf and Comparable.
     *
     * @return Result of the comparison.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SizeOf> int compare(T value1, T value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return ((Comparable<T>)value1).compareTo(value2);
    }

    /**
     * Compares two Array values lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(Array value1, Array value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        return value1.compareTo(value2);
    }

    /**
     * Compares two boolean raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(boolean[] value1, boolean[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two short raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(short[] value1, short[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two int raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(int[] value1, int[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two long raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(long[] value1, long[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two float raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(float[] value1, float[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two double raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(double[] value1, double[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two BigInteger raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(BigInteger[] value1, BigInteger[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two bytes raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(byte[][] value1, byte[][] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two String raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(String[] value1, String[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two BitBuffer raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    public static int compare(BitBuffer[] value1, BitBuffer[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two ZserioEnum raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     * @param <T> Concrete Java type implementing ZserioEnum.
     *
     * @return Result of the comparison.
     */
    public static <T extends ZserioEnum & SizeOf> int compare(T[] value1, T[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two SizeOf raw arrays lexicographically.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     * @param <T> Concrete Java type implementing SizeOf and Comparable.
     *
     * @return Result of the comparison.
     */
    public static <T extends SizeOf> int compare(T[] value1, T[] value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        final int minLength = Math.min(value1.length, value2.length);
        for (int i = 0; i < minLength; ++i)
        {
            final int result = compare(value1[i], value2[i]);
            if (result != 0)
                return result;
        }

        return Integer.compare(value1.length, value2.length);
    }

    /**
     * Compares two objects of any type which can be stored in a Zserio object.
     *
     * Note: This is intended to be used by generated choices when no case matches the selector.
     * Objects of different classes are ordered by their class names.
     *
     * @param value1 First value to compare.
     * @param value2 Second value to compare.
     *
     * @return Result of the comparison.
     */
    @SuppressWarnings("unchecked")
    public static int compareObjects(Object value1, Object value2)
    {
        if (value1 == null || value2 == null)
            return compareNulls(value1, value2);

        if (value1.getClass() != value2.getClass())
            return value1.getClass().getName().compareTo(value2.getClass().getName());

        if (value1 instanceof String)
            return compare((String)value1, (String)value2);
        if (value1 instanceof byte[])
            return compare((byte[])value1, (byte[])value2);
        if (value1 instanceof ZserioEnum)
            return compareEnumValues((ZserioEnum)value1, (ZserioEnum)value2);

        return ((Comparable<Object>)value1).compareTo(value2);
    }

    private static int compareEnumValues(ZserioEnum value1, ZserioEnum value2)
    {
        final Number enumValue1 = value1.getGenericValue();
        final Number enumValue2 = value2.getGenericValue();
        if (enumValue1 instanceof BigInteger)
            return compare((BigInteger)enumValue1, (BigInteger)enumValue2);
        else
            return compare(enumValue1.longValue(), enumValue2.longValue());
    }

    private static int compareNulls(Object value1, Object value2)
    {
        if (value1 == null)
            return (value2 == null) ? 0 : -1;

        return 1;
    }
}
