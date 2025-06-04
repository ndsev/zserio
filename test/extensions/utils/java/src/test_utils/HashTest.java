package test_utils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class provides help methods.
 */
public final class HashTest
{
    public static void run(Object value, int hashValue, Object equalValue)
    {
        assertEquals(hashValue, value.hashCode());
        assertEquals(hashValue, equalValue.hashCode());
    }

    public static void run(Object value, int hashValue, Object equalValue, Object diffValue, int diffHashValue)
    {
        run(value, hashValue, equalValue);
        assertNotEquals(hashValue, diffHashValue);
        assertEquals(diffHashValue, diffValue.hashCode());
    }
}
