package zserio.tools;

import java.lang.reflect.Array;

public class HashUtil
{
    /**
     * Collected methods which allow easy implementation of <code>hashCode</code>. Taken from:
     * http://www.javapractices.com/topic/TopicAction.do?Id=28 Example use case:
     *
     * <pre>
     * public int hashCode()
     * {
     *     int result = Util.HASH_SEED;
     *     //collect the contributions of various fields
     *     result = Util.hash(result, fPrimitive);
     *     result = Util.hash(result, fObject);
     *     result = Util.hash(result, fArray);
     *     return result;
     * }
     * </pre>
     */
    public static final int HASH_SEED = 23;

    /**
     * Creates a hash value of the given seed value and the given boolean value.
     *
     * @param aSeed a seed value
     * @param aBoolean a boolean value
     * @return the calculated hash value
     */
    public static int hash(final int aSeed, final boolean aBoolean)
    {
        return firstTerm(aSeed) + (aBoolean ? 1 : 0);
    }

    /**
     * Creates a hash value of the given seed value and the given char value.
     *
     * @param aSeed a seed value
     * @param aChar a char value
     * @return the calculated hash value
     */
    public static int hash(final int aSeed, final char aChar)
    {
        return firstTerm(aSeed) + aChar;
    }

    /**
     * Creates a hash value of the given seed value and the given integer value.
     *
     * @param aSeed a seed value
     * @param aInt a integer value
     * @return the calculated hash value
     */
    public static int hash(final int aSeed, final int aInt)
    {
        /*
         * Implementation Note
         * Note that byte and short are handled by this method, through
         * implicit conversion.
         */
        return firstTerm(aSeed) + aInt;
    }

    /**
     * Creates a hash value of the given seed value and the given long value.
     *
     * @param aSeed a seed value
     * @param aLong a long value
     * @return the calculated hash value
     */
    public static int hash(final int aSeed, final long aLong)
    {
        return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
    }

    /**
     * Creates a hash value of the given seed value and the given float value.
     *
     * @param aSeed a seed value
     * @param aFloat a float value
     * @return the calculated hash value
     */
    public static int hash(final int aSeed, final float aFloat)
    {
        return hash(aSeed, Float.floatToIntBits(aFloat));
    }

    /**
     * Creates a hash value of the given seed value and the given double value.
     *
     * @param aSeed a seed value
     * @param aDouble a double value
     * @return the calculated hash value
     */
    public static int hash(final int aSeed, final double aDouble)
    {
        return hash(aSeed, Double.doubleToLongBits(aDouble));
    }

    /**
     * Creates a hash value of the given seed value and the given object.
     *
     * @param aSeed a seed value
     * @param aObject an object
     * @return the calculated hash value
     */
    public static int hash(final int aSeed, final Object aObject)
    {
        int result = aSeed;

        if (aObject == null)
        {
            result = hash(result, 0);
        }
        else if (isEnum(aObject))
        {
            result = hash(result, ((Enum<?>) aObject).ordinal());
        }
        else if (!isArray(aObject))
        {
            result = hash(result, aObject.hashCode());
        }
        else
        {
            final int length = Array.getLength(aObject);
            for (int idx = 0; idx < length; ++idx)
            {
                final Object item = Array.get(aObject, idx);
                //recursive call!
                result = hash(result, item);
            }
        }
        return result;
    }

    /**
     * A prime number.
     */
    private static final int FODD_PRIME_NUMBER = 37;

    /**
     * Multiplies the given seed value with the prime number FODD_PRIME_NUMBER.
     *
     * @param aSeed a seed value
     * @return the result of the multiplication
     */
    private static int firstTerm(final int aSeed)
    {
        return FODD_PRIME_NUMBER * aSeed;
    }

    /**
     * Checks if the given object is an array class. True is returned if this is the case.
     *
     * @param aObject an object
     * @return the result of the check
     */
    private static boolean isArray(final Object obj)
    {
       return
               obj instanceof Object[] ||
               obj instanceof boolean[] ||
               obj instanceof byte[] ||
               obj instanceof short[] ||
               obj instanceof char[] ||
               obj instanceof int[] ||
               obj instanceof long[] ||
               obj instanceof float[] ||
               obj instanceof double[];
    }

    /**
     * Checks if the given object is an enumeration. True is returned if this is the case.
     *
     * @param aObject an object
     * @return the result of the check
     */
    private static boolean isEnum(final Object obj)
    {
        return obj instanceof Enum;
    }
}
