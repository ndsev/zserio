package test_utils;

import static org.junit.jupiter.api.Assertions.*;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

/**
 * The class provides help methods for testing serialization API.
 */
public final class WriteReadTest
{
    private static <T extends zserio.runtime.SizeOf & zserio.runtime.io.Writer> void serializeBytes(
            Class<T> clazz, T data)
    {
        int bitSize = data.bitSizeOf();

        // ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        // SerializeUtil.serializeToWriter(data, writer);
        // assertTrue(bitSize == writer.getBitPosition());
        byte[] byteArray = SerializeUtil.serializeToBytes(data);
        assertTrue((bitSize + 7) / 8 == byteArray.length);

        // ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.getWriteBuffer());
        // T readData = SerializeUtil.deserializeFromReader(clazz, reader);
        // assertTrue(bitSize == reader.getBitPosition());
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

    public static <T extends zserio.runtime.SizeOf & zserio.runtime.io.Writer> void run(Class<T> clazz, T data)
    {
        serializeBytes(clazz, data);
        serializeData(clazz, data);
    }
}
