package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import indexed_offsets.optional_nested_indexed_offset_array.Header;
import indexed_offsets.optional_nested_indexed_offset_array.OptionalNestedIndexedOffsetArray;

public class OptionalNestedIndexedOffsetArrayTest
{
    @Test
    public void readConstructorWithOptional() throws IOException, ZserioError
    {
        final int length = NUM_ELEMENTS;
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeOptionalNestedIndexedOffsetArrayToBitBuffer(length, writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                new OptionalNestedIndexedOffsetArray(reader);
        checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
    }

    @Test
    public void readConstructorWithoutOptional() throws IOException, ZserioError
    {
        final int length = 0;
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeOptionalNestedIndexedOffsetArrayToBitBuffer(length, writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                new OptionalNestedIndexedOffsetArray(reader);
        checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
    }

    @Test
    public void bitSizeOfWithOptional()
    {
        final int length = NUM_ELEMENTS;
        final boolean createWrongOffsets = false;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        assertEquals(getOptionalNestedIndexedOffsetArrayBitSize(length),
                optionalNestedIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final int length = 0;
        final boolean createWrongOffsets = false;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        assertEquals(getOptionalNestedIndexedOffsetArrayBitSize(length),
                optionalNestedIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void initializeOffsetsWithOptional()
    {
        final int length = NUM_ELEMENTS;
        final boolean createWrongOffsets = true;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(getOptionalNestedIndexedOffsetArrayBitSize(length),
                optionalNestedIndexedOffsetArray.initializeOffsets(bitPosition));
        checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
    }

    @Test
    public void initializeOffsetsWithoutOptional()
    {
        final int length = 0;
        final boolean createWrongOffsets = false;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(getOptionalNestedIndexedOffsetArrayBitSize(length),
                optionalNestedIndexedOffsetArray.initializeOffsets(bitPosition));
    }

    @Test
    public void writeReadWithOptional() throws IOException, ZserioError
    {
        final int length = NUM_ELEMENTS;
        final boolean createWrongOffsets = false;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(optionalNestedIndexedOffsetArray);
        checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);

        final OptionalNestedIndexedOffsetArray readOptionalNestedIndexedOffsetArray =
                SerializeUtil.deserialize(OptionalNestedIndexedOffsetArray.class, bitBuffer);
        checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length);
        assertTrue(optionalNestedIndexedOffsetArray.equals(readOptionalNestedIndexedOffsetArray));
    }

    @Test
    public void writeReadWithoutOptional() throws IOException, ZserioError
    {
        final int length = 0;
        final boolean createWrongOffsets = false;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(optionalNestedIndexedOffsetArray);

        final OptionalNestedIndexedOffsetArray readOptionalNestedIndexedOffsetArray =
                SerializeUtil.deserialize(OptionalNestedIndexedOffsetArray.class, bitBuffer);
        checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length);
        assertTrue(optionalNestedIndexedOffsetArray.equals(readOptionalNestedIndexedOffsetArray));
    }

    private BitBuffer writeOptionalNestedIndexedOffsetArrayToBitBuffer(int length, boolean writeWrongOffsets)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeUnsignedShort(length);

            if (length > 0)
            {
                long currentOffset = ELEMENT0_OFFSET;
                for (int i = 0; i < length; ++i)
                {
                    if ((i + 1) == length && writeWrongOffsets)
                        writer.writeUnsignedInt(WRONG_OFFSET);
                    else
                        writer.writeUnsignedInt(currentOffset);
                    currentOffset += BitSizeOfCalculator.getBitSizeOfString(DATA[i]) / Byte.SIZE;
                }

                // already aligned
                for (int i = 0; i < length; ++i)
                    writer.writeString(DATA[i]);
            }

            writer.writeBits(FIELD_VALUE, 6);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkOffsets(
            OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray, short offsetShift)
    {
        final int length = optionalNestedIndexedOffsetArray.getHeader().getLength();
        final long[] offsets = optionalNestedIndexedOffsetArray.getHeader().getOffsets();
        assertEquals(length, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (int i = 0; i < length; ++i)
        {
            assertEquals(expectedOffset, offsets[i]);
            expectedOffset += BitSizeOfCalculator.getBitSizeOfString(DATA[i]) / Byte.SIZE;
        }
    }

    private void checkOptionalNestedIndexedOffsetArray(
            OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray, int length)
    {
        assertEquals(length, optionalNestedIndexedOffsetArray.getHeader().getLength());

        final short offsetShift = 0;
        checkOffsets(optionalNestedIndexedOffsetArray, offsetShift);

        if (length > 0)
        {
            final String[] data = optionalNestedIndexedOffsetArray.getData();
            assertEquals(length, data.length);
            for (int i = 0; i < length; ++i)
                assertTrue(DATA[i].equals(data[i]));
        }

        assertEquals(FIELD_VALUE, optionalNestedIndexedOffsetArray.getField());
    }

    private OptionalNestedIndexedOffsetArray createOptionalNestedIndexedOffsetArray(
            int length, boolean createWrongOffsets)
    {
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                new OptionalNestedIndexedOffsetArray();

        final Header header = new Header();

        final long[] offsets = new long[length];
        long currentOffset = ELEMENT0_OFFSET;
        for (int i = 0; i < length; ++i)
        {
            if ((i + 1) == length && createWrongOffsets)
                offsets[i] = WRONG_OFFSET;
            else
                offsets[i] = currentOffset;
            currentOffset += BitSizeOfCalculator.getBitSizeOfString(DATA[i]) / Byte.SIZE;
        }

        header.setLength(length);
        header.setOffsets(offsets);

        optionalNestedIndexedOffsetArray.setHeader(header);

        if (length > 0)
        {
            final String[] data = new String[length];
            for (int i = 0; i < length; ++i)
                data[i] = DATA[i];
            optionalNestedIndexedOffsetArray.setData(data);
        }

        optionalNestedIndexedOffsetArray.setField(FIELD_VALUE);

        return optionalNestedIndexedOffsetArray;
    }

    private long getOptionalNestedIndexedOffsetArrayBitSize(int length)
    {
        long bitSize = Short.SIZE + length * Integer.SIZE;
        if (length > 0)
        {
            // already aligned
            for (int i = 0; i < length; ++i)
                bitSize += BitSizeOfCalculator.getBitSizeOfString(DATA[i]);
        }
        bitSize += 6;

        return bitSize;
    }

    private static final int NUM_ELEMENTS = (int)5;

    private static final long WRONG_OFFSET = (long)0;
    private static final long ELEMENT0_OFFSET = (long)(Short.SIZE + NUM_ELEMENTS * Integer.SIZE) / Byte.SIZE;

    private static final byte FIELD_VALUE = 63;

    private static final String DATA[] = {"Green", "Red", "Pink", "Blue", "Black"};
}
