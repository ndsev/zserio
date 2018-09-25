package zserio.tools;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code> method.
 *
 * Only native types and Object types are supported so far.
 *
 * Usage:
 * <pre>
 * public int hashCode()
 * {
 *     int result = Util.HASH_SEED;
 *
 *     // collect the contributions of various fields
 *     result = Util.hash(result, nativeType);
 *     result = Util.hash(result, objectType);

 *     return result;
 * }
 * </pre>
 */
public class HashUtil
{
    /**
     * Creates a hash value of the given seed value and the given boolean value.
     *
     * @param seedValue    The seed value to use for calculation.
     * @param booleanValue The boolean value to use for calculation.
     *
     * @return The calculated hash value.
     */
    public static int hash(int seedValue, boolean booleanValue)
    {
        return firstTerm(seedValue) + (booleanValue ? 1 : 0);
    }

    /**
     * Creates a hash value of the given seed value and the given char value.
     *
     * @param seedValue The seed value to use for calculation.
     * @param charValue The char value to use for calculation.
     *
     * @return The calculated hash value.
     */
    public static int hash(int seedValue, char charValue)
    {
        return firstTerm(seedValue) + charValue;
    }

    /**
     * Creates a hash value of the given seed value and the given integer value.
     *
     * @param seedValue The seed value to use for calculation.
     * @param intValue  The integer value to use for calculation.
     *
     * @return The calculated hash value.
     */
    public static int hash(int seedValue, int intValue)
    {
        // note that byte and short are handled by this method, through implicit conversion
        return firstTerm(seedValue) + intValue;
    }

    /**
     * Creates a hash value of the given seed value and the given long value.
     *
     * @param seedValue The seed value to use for calculation.
     * @param longValue The long value to use for calculation.
     *
     * @return The calculated hash value.
     */
    public static int hash(int seedValue, long longValue)
    {
        return firstTerm(seedValue) + (int) (longValue ^ (longValue >>> 32));
    }

    /**
     * Creates a hash value of the given seed value and the given float value.
     *
     * @param seedValue  The seed value to use for calculation.
     * @param floatValue The float value to use for calculation.
     *
     * @return The calculated hash value.
     */
    public static int hash(int seedValue, float floatValue)
    {
        return hash(seedValue, Float.floatToIntBits(floatValue));
    }

    /**
     * Creates a hash value of the given seed value and the given double value.
     *
     * @param seedValue   The seed value to use for calculation.
     * @param doubleValue The double value to use for calculation.
     *
     * @return The calculated hash value.
     */
    public static int hash(int seedValue, double doubleValue)
    {
        return hash(seedValue, Double.doubleToLongBits(doubleValue));
    }

    /**
     * Creates a hash value of the given seed value and the given object.
     *
     * @param seedValue  The seed value to use for calculation.
     * @param objectType The object to use for calculation.
     *
     * @return The calculated hash value.
     */
    public static int hash(int seedValue, Object objectType)
    {
        int result = seedValue;

        if (objectType == null)
            result = hash(result, 0);
        else
            result = hash(result, objectType.hashCode());

        return result;
    }

    /**
     * Hash seed number to be used for initialization.
     */
    public static final int HASH_SEED = 23;

    private static int firstTerm(int seedValue)
    {
        return FODD_PRIME_NUMBER * seedValue;
    }

    private static final int FODD_PRIME_NUMBER = 37;
}
