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

import indexed_offsets.optional_indexed_offset_array.OptionalIndexedOffsetArray;

public class OptionalIndexedOffsetArrayTest
{
    @Test
    public void readWithOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = true;
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeOptionalIndexedOffsetArrayToBitBuffer(hasOptional, writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray = new OptionalIndexedOffsetArray(reader);
        checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);
    }

    @Test
    public void readWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeOptionalIndexedOffsetArrayToBitBuffer(hasOptional, writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray = new OptionalIndexedOffsetArray(reader);
        checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);
    }

    @Test
    public void bitSizeOfWithOptional()
    {
        final boolean hasOptional = true;
        final boolean createWrongOffsets = false;
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray =
                createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets);
        assertEquals(getOptionalIndexedOffsetArrayBitSize(hasOptional), optionalIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final boolean hasOptional = false;
        final boolean createWrongOffsets = false;
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray =
                createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets);
        assertEquals(getOptionalIndexedOffsetArrayBitSize(hasOptional), optionalIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void initializeOffsetsWithOptional()
    {
        final boolean hasOptional = true;
        final boolean createWrongOffsets = true;
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray =
                createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(getOptionalIndexedOffsetArrayBitSize(hasOptional),
                optionalIndexedOffsetArray.initializeOffsets(bitPosition));
        checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);
    }

    @Test
    public void initializeOffsetsWithoutOptional()
    {
        final boolean hasOptional = false;
        final boolean createWrongOffsets = false;
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray =
                createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(getOptionalIndexedOffsetArrayBitSize(hasOptional),
                optionalIndexedOffsetArray.initializeOffsets(bitPosition));
    }

    @Test
    public void writeReadWithOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = true;
        final boolean createWrongOffsets = false;
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray =
                createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(optionalIndexedOffsetArray);
        checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);

        final OptionalIndexedOffsetArray readOptionalIndexedOffsetArray =
                SerializeUtil.deserialize(OptionalIndexedOffsetArray.class, bitBuffer);
        checkOptionalIndexedOffsetArray(readOptionalIndexedOffsetArray, hasOptional);
        assertTrue(optionalIndexedOffsetArray.equals(readOptionalIndexedOffsetArray));
    }

    @Test
    public void writeReadWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final boolean createWrongOffsets = false;
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray =
                createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(optionalIndexedOffsetArray);

        final OptionalIndexedOffsetArray readOptionalIndexedOffsetArray =
                SerializeUtil.deserialize(OptionalIndexedOffsetArray.class, bitBuffer);
        checkOptionalIndexedOffsetArray(readOptionalIndexedOffsetArray, hasOptional);
        assertTrue(optionalIndexedOffsetArray.equals(readOptionalIndexedOffsetArray));
    }

    private BitBuffer writeOptionalIndexedOffsetArrayToBitBuffer(boolean hasOptional, boolean writeWrongOffsets)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            long currentOffset = ELEMENT0_OFFSET;
            for (short i = 0; i < NUM_ELEMENTS; ++i)
            {
                if ((i + 1) == NUM_ELEMENTS && writeWrongOffsets)
                    writer.writeUnsignedInt(WRONG_OFFSET);
                else
                    writer.writeUnsignedInt(currentOffset);
                currentOffset += BitSizeOfCalculator.getBitSizeOfString(DATA[i]) / Byte.SIZE;
            }

            writer.writeBool(hasOptional);

            if (hasOptional)
            {
                writer.writeBits(0, 7);
                for (short i = 0; i < NUM_ELEMENTS; ++i)
                    writer.writeString(DATA[i]);
            }

            writer.writeBits(FIELD_VALUE, 6);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkOffsets(OptionalIndexedOffsetArray optionalIndexedOffsetArray, short offsetShift)
    {
        final long[] offsets = optionalIndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            assertEquals(expectedOffset, offsets[i]);
            expectedOffset += BitSizeOfCalculator.getBitSizeOfString(DATA[i]) / Byte.SIZE;
        }
    }

    private void checkOptionalIndexedOffsetArray(
            OptionalIndexedOffsetArray optionalIndexedOffsetArray, boolean hasOptional)
    {
        final short offsetShift = 0;
        checkOffsets(optionalIndexedOffsetArray, offsetShift);

        assertEquals(hasOptional, optionalIndexedOffsetArray.getHasOptional());

        if (hasOptional)
        {
            final String[] data = optionalIndexedOffsetArray.getData();
            assertEquals(NUM_ELEMENTS, data.length);
            for (short i = 0; i < NUM_ELEMENTS; ++i)
                assertTrue(DATA[i].equals(data[i]));
        }

        assertEquals(FIELD_VALUE, optionalIndexedOffsetArray.getField());
    }

    private OptionalIndexedOffsetArray createOptionalIndexedOffsetArray(
            boolean hasOptional, boolean createWrongOffsets)
    {
        final OptionalIndexedOffsetArray optionalIndexedOffsetArray = new OptionalIndexedOffsetArray();

        final long[] offsets = new long[NUM_ELEMENTS];
        long currentOffset = ELEMENT0_OFFSET;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets[i] = WRONG_OFFSET;
            else
                offsets[i] = currentOffset;
            currentOffset += BitSizeOfCalculator.getBitSizeOfString(DATA[i]) / Byte.SIZE;
        }
        optionalIndexedOffsetArray.setOffsets(offsets);
        optionalIndexedOffsetArray.setHasOptional(hasOptional);

        if (hasOptional)
        {
            final String[] data = new String[NUM_ELEMENTS];
            for (short i = 0; i < NUM_ELEMENTS; ++i)
                data[i] = DATA[i];
            optionalIndexedOffsetArray.setData(data);
        }

        optionalIndexedOffsetArray.setField(FIELD_VALUE);

        return optionalIndexedOffsetArray;
    }

    private long getOptionalIndexedOffsetArrayBitSize(boolean hasOptional)
    {
        long bitSize = NUM_ELEMENTS * Integer.SIZE + 1;
        if (hasOptional)
        {
            bitSize += 7;
            for (short i = 0; i < NUM_ELEMENTS; ++i)
                bitSize += BitSizeOfCalculator.getBitSizeOfString(DATA[i]);
        }
        bitSize += 6;

        return bitSize;
    }

    private static final short NUM_ELEMENTS = (short)5;

    private static final long WRONG_OFFSET = (long)0;
    private static final long ELEMENT0_OFFSET = (long)(NUM_ELEMENTS * Integer.SIZE + Byte.SIZE) / Byte.SIZE;

    private static final byte FIELD_VALUE = 63;

    private static final String DATA[] = {"Green", "Red", "Pink", "Blue", "Black"};
}
