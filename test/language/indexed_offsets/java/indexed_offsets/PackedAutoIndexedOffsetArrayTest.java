package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import indexed_offsets.packed_auto_indexed_offset_array.AutoIndexedOffsetArray;

import java.io.IOException;
import java.io.File;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class PackedAutoIndexedOffsetArrayTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final File file = new File("test.bin");
        writeAutoIndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final AutoIndexedOffsetArray autoIndexedOffsetArray = new AutoIndexedOffsetArray(stream);
        stream.close();
        checkAutoIndexedOffsetArray(autoIndexedOffsetArray);
    }

    @Test
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final File file = new File("test.bin");
        writeAutoIndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new AutoIndexedOffsetArray(stream));
        stream.close();
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(createWrongOffsets);
        assertEquals(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE, autoIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 1;
        assertEquals(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                autoIndexedOffsetArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE, autoIndexedOffsetArray.initializeOffsets(bitPosition));
        checkAutoIndexedOffsetArray(autoIndexedOffsetArray);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 9;
        assertEquals(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                autoIndexedOffsetArray.initializeOffsets(bitPosition));

        final short offsetShift = 1;
        checkOffsets(autoIndexedOffsetArray, offsetShift);
    }

    @Test
    public void writeReadFile() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(createWrongOffsets);
        final File file = new File(BLOB_NAME);
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        autoIndexedOffsetArray.write(writer);
        writer.close();
        checkAutoIndexedOffsetArray(autoIndexedOffsetArray);
        final AutoIndexedOffsetArray readAutoIndexedOffsetArray = new AutoIndexedOffsetArray(file);
        checkAutoIndexedOffsetArray(readAutoIndexedOffsetArray);
        assertTrue(autoIndexedOffsetArray.equals(readAutoIndexedOffsetArray));
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        final int bitPosition = 8;
        writer.writeBits(0, bitPosition);
        autoIndexedOffsetArray.write(writer);
        writer.close();

        final short offsetShift = 1;
        checkOffsets(autoIndexedOffsetArray, offsetShift);
    }

    @Test
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> autoIndexedOffsetArray.write(writer, false));
        writer.close();
    }

    private void writeAutoIndexedOffsetArrayToFile(File file, boolean writeWrongOffsets) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeVarSize(NUM_ELEMENTS);
        long currentOffset = ELEMENT0_OFFSET;
        for (int i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && writeWrongOffsets)
                writer.writeBits(WRONG_OFFSET, 32);
            else
                writer.writeBits(currentOffset, 32);
            currentOffset += (i == 0 ? ALIGNED_FIRST_ELEMENT_BYTE_SIZE : ALIGNED_ELEMENT_BYTE_SIZE);
        }

        writer.writeBits(SPACER_VALUE, 3);

        writer.writeVarSize(NUM_ELEMENTS);

        writer.alignTo(8);
        writer.writeBool(true);
        writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(0, ELEMENT_SIZE);
        for (int i = 1; i < NUM_ELEMENTS; ++i)
        {
            writer.alignTo(8);
            writer.writeSignedBits(PACKED_ARRAY_DELTA, PACKED_ARRAY_MAX_BIT_NUMBER + 1);
        }

        writer.close();
    }

    private void checkOffsets(AutoIndexedOffsetArray autoIndexedOffsetArray, short offsetShift)
    {
        final long[] offsets = autoIndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (int i = 0; i < offsets.length; ++i)
        {
            final long offset = offsets[i];
            assertEquals(expectedOffset, offset, "index: " + i);
            expectedOffset += (i == 0 ? ALIGNED_FIRST_ELEMENT_BYTE_SIZE : ALIGNED_ELEMENT_BYTE_SIZE);
        }
    }

    private void checkAutoIndexedOffsetArray(AutoIndexedOffsetArray autoIndexedOffsetArray)
    {
        final short offsetShift = 0;
        checkOffsets(autoIndexedOffsetArray, offsetShift);

        assertEquals(SPACER_VALUE, autoIndexedOffsetArray.getSpacer());

        final byte[] data = autoIndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            assertEquals(i % 64, data[i]);
    }

    private AutoIndexedOffsetArray createAutoIndexedOffsetArray(boolean createWrongOffsets)
    {
        final AutoIndexedOffsetArray autoIndexedOffsetArray = new AutoIndexedOffsetArray();

        final long[] offsets = new long[NUM_ELEMENTS];
        long currentOffset = ELEMENT0_OFFSET;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets[i] = WRONG_OFFSET;
            else
                offsets[i] = currentOffset;
            currentOffset += (i == 0 ? ALIGNED_FIRST_ELEMENT_BYTE_SIZE : ALIGNED_ELEMENT_BYTE_SIZE);
        }
        autoIndexedOffsetArray.setOffsets(offsets);
        autoIndexedOffsetArray.setSpacer(SPACER_VALUE);

        final byte[] data = new byte[NUM_ELEMENTS];
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            data[i] = (byte)(i % 64);
        autoIndexedOffsetArray.setData(data);

        return autoIndexedOffsetArray;
    }

    private static final String BLOB_NAME = "packed_auto_indexed_offset_array.blob";

    private static final short  NUM_ELEMENTS = (short)5;

    private static final long   WRONG_OFFSET = 0;

    private static final int    AUTO_ARRAY_LENGTH_BYTE_SIZE = 1;
    private static final long   ELEMENT0_OFFSET =
            AUTO_ARRAY_LENGTH_BYTE_SIZE + (long)(NUM_ELEMENTS * 4) +
            (3 + AUTO_ARRAY_LENGTH_BYTE_SIZE * 8 + 5 /* alignment */) / 8;
    private static final int    ELEMENT_SIZE = 5;
    private static final int    ALIGNED_FIRST_ELEMENT_SIZE = 1 + 6 + ELEMENT_SIZE + 4 /* alignment */;
    private static final int    ALIGNED_FIRST_ELEMENT_BYTE_SIZE = ALIGNED_FIRST_ELEMENT_SIZE / 8;
    private static final int    ALIGNED_ELEMENT_SIZE = 8;
    private static final int    ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / 8;

    private static final byte   SPACER_VALUE = 7;

    private static final short  PACKED_ARRAY_DELTA = 1;
    private static final short  PACKED_ARRAY_MAX_BIT_NUMBER = 1;

    private static final int    AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE = (int)ELEMENT0_OFFSET * 8 +
            ALIGNED_FIRST_ELEMENT_SIZE +
            (NUM_ELEMENTS - 2) * ALIGNED_ELEMENT_SIZE + PACKED_ARRAY_MAX_BIT_NUMBER + 1;
}
