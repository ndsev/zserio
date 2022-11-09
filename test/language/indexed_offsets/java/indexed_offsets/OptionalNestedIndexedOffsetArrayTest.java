package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import indexed_offsets.optional_nested_indexed_offset_array.OptionalNestedIndexedOffsetArray;
import indexed_offsets.optional_nested_indexed_offset_array.Header;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class OptionalNestedIndexedOffsetArrayTest
{
    @Test
    public void readWithOptional() throws IOException, ZserioError
    {
        final int length = NUM_ELEMENTS;
        final boolean writeWrongOffsets = false;
        final File file = new File("test.bin");
        writeOptionalNestedIndexedOffsetArrayToFile(file, length, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                new OptionalNestedIndexedOffsetArray(stream);
        stream.close();
        checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
    }

    @Test
    public void readWithoutOptional() throws IOException, ZserioError
    {
        final int length = 0;
        final boolean writeWrongOffsets = false;
        final File file = new File("test.bin");
        writeOptionalNestedIndexedOffsetArrayToFile(file, length, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                new OptionalNestedIndexedOffsetArray(stream);
        stream.close();
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
    public void writeWithOptional() throws IOException, ZserioError
    {
        final int length = NUM_ELEMENTS;
        final boolean createWrongOffsets = false;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        optionalNestedIndexedOffsetArray.write(writer);
        writer.close();
        checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
        final OptionalNestedIndexedOffsetArray readOptionalNestedIndexedOffsetArray =
                new OptionalNestedIndexedOffsetArray(file);
        checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length);
        assertTrue(optionalNestedIndexedOffsetArray.equals(readOptionalNestedIndexedOffsetArray));
    }

    @Test
    public void writeWithoutOptional() throws IOException, ZserioError
    {
        final int length = 0;
        final boolean createWrongOffsets = false;
        final OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray =
                createOptionalNestedIndexedOffsetArray(length, createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        optionalNestedIndexedOffsetArray.write(writer);
        writer.close();
        final OptionalNestedIndexedOffsetArray readOptionalNestedIndexedOffsetArray =
                new OptionalNestedIndexedOffsetArray(file);
        checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length);
        assertTrue(optionalNestedIndexedOffsetArray.equals(readOptionalNestedIndexedOffsetArray));
    }

    private void writeOptionalNestedIndexedOffsetArrayToFile(File file, int length,
            boolean writeWrongOffsets) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

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

        writer.close();
    }

    private void checkOffsets(OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray,
            short offsetShift)
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

    private OptionalNestedIndexedOffsetArray createOptionalNestedIndexedOffsetArray(int length,
            boolean createWrongOffsets)
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

    private static final long  WRONG_OFFSET = (long)0;
    private static final long  ELEMENT0_OFFSET = (long)(Short.SIZE + NUM_ELEMENTS * Integer.SIZE) / Byte.SIZE;

    private static final byte  FIELD_VALUE = 63;

    private static final String DATA[] = {"Green", "Red", "Pink", "Blue", "Black"};
}
