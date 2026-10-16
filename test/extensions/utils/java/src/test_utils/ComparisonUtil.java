package test_utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks equals() and compareTo() of generated objects, Java counterpart of C++ comparisonOperatorsTest.
 */
public final class ComparisonUtil
{
    public static <T extends Comparable<T>> void comparisonTest(T value, T equalValue)
    {
        assertTrue(value.equals(equalValue));
        assertEquals(0, value.compareTo(equalValue));
        assertEquals(0, equalValue.compareTo(value));
    }

    public static <T extends Comparable<T>> void comparisonTest(T value, T equalValue, T lessThanValue)
    {
        comparisonTest(value, equalValue);
        assertFalse(value.equals(lessThanValue));
        assertFalse(equalValue.equals(lessThanValue));
        assertTrue(lessThanValue.compareTo(value) < 0);
        assertTrue(value.compareTo(lessThanValue) > 0);
        assertTrue(lessThanValue.compareTo(equalValue) < 0);
    }
}
