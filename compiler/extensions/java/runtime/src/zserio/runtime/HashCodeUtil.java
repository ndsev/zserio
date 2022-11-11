package zserio.runtime;

import java.math.BigInteger;

import zserio.runtime.array.Array;
import zserio.runtime.io.BitBuffer;

/**
 * Utilities for hash code calculation.
 */
public class HashCodeUtil
{
    /**
     * Calculates hash code for a boolean value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, boolean value)
    {
        return calcHashCode(seedValue, value ? 1 : 0);
    }

    /**
     * Calculates hash code for a Boolean value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Boolean value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.booleanValue());
    }

    /**
     * Calculates hash code for an Byte value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Byte value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.byteValue());
    }

    /**
     * Calculates hash code for an Short value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Short value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.shortValue());
    }

    /**
     * Calculates hash code for an int value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, int value)
    {
        return HASH_PRIME_NUMBER * seedValue + value;
    }

    /**
     * Calculates hash code for an Integer value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Integer value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.intValue());
    }

    /**
     * Calculates hash code for a long value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, long value)
    {
        return calcHashCode(seedValue, (int)(value ^ (value >>> 32)));
    }

    /**
     * Calculates hash code for a Long value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Long value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.longValue());
    }

    /**
     * Calculates hash code for a float value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, float value)
    {
        return calcHashCode(seedValue, FloatUtil.convertFloatToInt(value));
    }

    /**
     * Calculates hash code for a Float value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Float value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.floatValue());
    }

    /**
     * Calculates hash code for a double value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, double value)
    {
        return calcHashCode(seedValue, FloatUtil.convertDoubleToLong(value));
    }

    /**
     * Calculates hash code for a Double value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Double value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.doubleValue());
    }

    /**
     * Calculates hash code for a BigInteger value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, BigInteger value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.xor(value.shiftRight(32)).intValue());
    }

    /**
     * Calculates hash code for a String value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, String value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (int i = 0; i < value.length(); ++i)
            result = calcHashCode(result, (int)value.charAt(i));

        return result;
    }

    /**
     * Calculates hash code for BitBuffer value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, BitBuffer value)
    {
        return calcHashCodeForObject(seedValue, value);
    }

    /**
     * Calculates hash code for a ZserioEnum value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     * @param <T> Concrete Java type implementing ZserioEnum.
     *
     * @return Calculated hash code.
     */
    public static <T extends ZserioEnum & SizeOf> int calcHashCode(int seedValue, T value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        final Number enumValue = value.getGenericValue();
        if (enumValue instanceof BigInteger)
            return calcHashCode(seedValue, calcHashCode(HASH_SEED, (BigInteger)enumValue));
        else
            return calcHashCode(seedValue, calcHashCode(HASH_SEED, enumValue.longValue()));
    }

    /**
     * Calculates hash code for an SizeOf value.
     *
     * Note: This is intended to be used by generated objects (including ZserioBitmask).
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     * @param <T> Concrete Java type implementing SizeOf.
     *
     * @return Calculated hash code.
     */
    public static <T extends SizeOf> int calcHashCode(int seedValue, T value)
    {
        return calcHashCodeForObject(seedValue, value);
    }

    /**
     * Calculates hash code for an Array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, Array value)
    {
        return calcHashCodeForObject(seedValue, value);
    }

    /**
     * Calculates hash code for a boolean raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, boolean[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (boolean element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a byte raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, byte[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (byte element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a short raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, short[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (short element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for an int raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, int[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (int element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a long raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, long[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (long element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a float raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, float[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (float element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a double raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, double[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (double element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a BigInteger raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, BigInteger[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (BigInteger element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a bytes raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, byte[][] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (byte[] element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a String raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, String[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (String element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a BitBuffer raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     *
     * @return Calculated hash code.
     */
    public static int calcHashCode(int seedValue, BitBuffer[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (BitBuffer element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a ZserioEnum raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     * @param <T> Concrete Java type implementing ZserioEnum.
     *
     * @return Calculated hash code.
     */
    public static <T extends ZserioEnum & SizeOf> int calcHashCode(int seedValue, T[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (T element : value)
            result = calcHashCode(result, element);
        return result;
    }

    /**
     * Calculates hash code for a generated object (including ZserioBitmask) raw array value.
     *
     * @param seedValue Seed value (current hash code).
     * @param value Value to use.
     * @param <T> Concrete Java type implementing SizeOf.
     *
     * @return Calculated hash code.
     */
    public static <T extends SizeOf> int calcHashCode(int seedValue, T[] value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        int result = seedValue;
        for (T element : value)
            result = calcHashCode(result, element);
        return result;
    }

    private static int calcHashCodeForObject(int seedValue, Object value)
    {
        if (value == null)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, value.hashCode());
    }

    /** Initial seed for hash calculation. */
    public static final int HASH_SEED = 23;
    /** Prime number for hash calculation. */
    public static final int HASH_PRIME_NUMBER = 37;
}
