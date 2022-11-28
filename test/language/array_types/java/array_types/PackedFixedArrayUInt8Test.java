package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;
import array_types.packed_fixed_array_uint8.PackedFixedArray;

public class PackedFixedArrayUInt8Test
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final PackedFixedArray packedFixedArray = createPackedFixedArray();
        final int bitPosition = 2;
        final int fixedArrayBitSize = calcPackedFixedArrayBitSize();
        assertEquals(fixedArrayBitSize, packedFixedArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final PackedFixedArray packedFixedArray = createPackedFixedArray();
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + calcPackedFixedArrayBitSize();
        assertEquals(expectedEndBitPosition, packedFixedArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final BitBuffer buffer = writePackedFixedArrayToBitBuffer();
        final PackedFixedArray packedFixedArray = SerializeUtil.deserialize(PackedFixedArray.class, buffer);
        checkPackedFixedArray(packedFixedArray);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final PackedFixedArray packedFixedArray = createPackedFixedArray();
        final File file = new File(BLOB_NAME);
        SerializeUtil.serializeToFile(packedFixedArray, file);
        final PackedFixedArray readPackedFixedArray =
                SerializeUtil.deserializeFromFile(PackedFixedArray.class, file);
        checkPackedFixedArray(readPackedFixedArray);
    }

    @Test
    public void writeWrongArray() throws IOException, ZserioError
    {
        final PackedFixedArray packedFixedArray = createWrongPackedFixedArray();
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> packedFixedArray.write(writer));
        writer.close();
    }

    private PackedFixedArray createPackedFixedArray()
    {
        return createPackedFixedArray(false);
    }

    private PackedFixedArray createWrongPackedFixedArray()
    {
        return createPackedFixedArray(true);
    }

    private PackedFixedArray createPackedFixedArray(boolean wrongSize)
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH + ((wrongSize) ? 1 : 0)];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = PACKED_ARRAY_ELEMENT;

        return new PackedFixedArray(uint8Array);
    }

    private void checkPackedFixedArray(PackedFixedArray packedFixedArray)
    {
        final short[] uint8Array = packedFixedArray.getUint8Array();
        assertEquals(FIXED_ARRAY_LENGTH, uint8Array.length);
        for (int i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            assertEquals(PACKED_ARRAY_ELEMENT, uint8Array[i]);
    }

    private BitBuffer writePackedFixedArrayToBitBuffer() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBool(true);
            writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
            writer.writeBits(PACKED_ARRAY_ELEMENT, 8);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private int calcPackedFixedArrayBitSize()
    {
        int bitSize = 1; // packing descriptor: is_packed
        bitSize += 6; // packing descriptor: max_bit_number
        bitSize += 8; // first element

        return bitSize;
    }

    private static final String BLOB_NAME = "packed_fixed_array_uint8.blob";

    private static final int FIXED_ARRAY_LENGTH = 5;

    private static final byte PACKED_ARRAY_MAX_BIT_NUMBER = 0;
    private static final short PACKED_ARRAY_ELEMENT = 100;
}
