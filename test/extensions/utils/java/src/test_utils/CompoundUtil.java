package test_utils;

import static org.junit.jupiter.api.Assertions.*;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

/**
 * The class provides help methods for testing serialization API.
 */
public final class CompoundUtil
{
    private static <T extends zserio.runtime.SizeOf & zserio.runtime.io.Writer> void serializeBytes(
            Class<T> clazz, T data)
    {
        int bitSize = data.bitSizeOf();

        byte[] byteArray = SerializeUtil.serializeToBytes(data);
        assertTrue((bitSize + 7) / 8 == byteArray.length);

        T readData = SerializeUtil.deserializeFromBytes(clazz, byteArray);
        assertTrue(data.equals(readData));
    }

    private static <T extends zserio.runtime.SizeOf & zserio.runtime.io.Writer> void serializeData(
            Class<T> clazz, T data)
    {
        int bitSize = data.bitSizeOf();
        BitBuffer buffer = SerializeUtil.serialize(data);
        assertTrue(bitSize == buffer.getBitSize());

        T readData = SerializeUtil.deserialize(clazz, buffer);
        assertTrue(readData.equals(data));
    }

    public static <T extends zserio.runtime.SizeOf & zserio.runtime.io.Writer> void writeReadTest(
            Class<T> clazz, T data)
    {
        serializeBytes(clazz, data);
        serializeData(clazz, data);
    }

    public static void comparisonOperatorsTest(Object value, Object equalValue)
    {
        assertTrue(value.equals(equalValue));
    }

    public static void hashTest(Object value, int hashValue, Object equalValue)
    {
        assertEquals(hashValue, value.hashCode());
        assertEquals(hashValue, equalValue.hashCode());
    }

    public static void hashTest(
            Object value, int hashValue, Object equalValue, Object diffValue, int diffHashValue)
    {
        hashTest(value, hashValue, equalValue);
        assertNotEquals(hashValue, diffHashValue);
        assertEquals(diffHashValue, diffValue.hashCode());
    }
}
