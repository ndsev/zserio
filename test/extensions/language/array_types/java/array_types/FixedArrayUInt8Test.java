package array_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import array_types.fixed_array_uint8.FixedArray;

public class FixedArrayUInt8Test
{
    @Test
    public void emptyConstructor()
    {
        final FixedArray fixedArray = new FixedArray();
        assertEquals(null, fixedArray.getUint8Array());
    }

    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        final FixedArray fixedArray = new FixedArray(uint8Array);
        final int bitPosition = 2;
        final int fixedArrayBitSize = FIXED_ARRAY_LENGTH * 8;
        assertEquals(fixedArrayBitSize, fixedArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        final FixedArray fixedArray = new FixedArray(uint8Array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + FIXED_ARRAY_LENGTH * 8;
        assertEquals(expectedEndBitPosition, fixedArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final BitBuffer buffer = writeFixedArrayToBitBuffer();
        final FixedArray fixedArray = SerializeUtil.deserialize(FixedArray.class, buffer);

        final short[] uint8Array = fixedArray.getUint8Array();
        assertEquals(FIXED_ARRAY_LENGTH, uint8Array.length);
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            assertEquals(i, uint8Array[i]);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        FixedArray fixedArray = new FixedArray(uint8Array);
        SerializeUtil.serializeToFile(fixedArray, BLOB_NAME);

        final FixedArray readFixedArray = SerializeUtil.deserializeFromFile(FixedArray.class, BLOB_NAME);
        final short[] readUint8Array = readFixedArray.getUint8Array();
        assertEquals(FIXED_ARRAY_LENGTH, readUint8Array.length);
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            assertEquals(i, readUint8Array[i]);
    }

    @Test
    public void writeWrongArray() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH + 1];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        FixedArray fixedArray = new FixedArray(uint8Array);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> fixedArray.write(writer));
        writer.close();
    }

    private BitBuffer writeFixedArrayToBitBuffer() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
                writer.writeUnsignedByte(i);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME = "fixed_array_uint8.blob";
    private static final int FIXED_ARRAY_LENGTH = 5;
}
