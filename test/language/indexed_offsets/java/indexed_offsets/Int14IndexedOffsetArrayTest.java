package indexed_offsets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import indexed_offsets.int14_indexed_offset_array.Int14IndexedOffsetArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class Int14IndexedOffsetArrayTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final File file = new File("test.bin");
        writeInt14IndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final Int14IndexedOffsetArray int14IndexedOffsetArray = new Int14IndexedOffsetArray(stream);
        stream.close();
        checkInt14IndexedOffsetArray(int14IndexedOffsetArray);
    }

    @Test(expected=ZserioError.class)
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final File file = new File("test.bin");
        writeInt14IndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final Int14IndexedOffsetArray int14IndexedOffsetArray = new Int14IndexedOffsetArray(stream);
        stream.close();
        checkInt14IndexedOffsetArray(int14IndexedOffsetArray);
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final Int14IndexedOffsetArray int14IndexedOffsetArray =
                createInt14IndexedOffsetArray(createWrongOffsets);
        assertEquals(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE, int14IndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final Int14IndexedOffsetArray int14IndexedOffsetArray =
                createInt14IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 1;
        assertEquals(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                int14IndexedOffsetArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final Int14IndexedOffsetArray int14IndexedOffsetArray =
                createInt14IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE, int14IndexedOffsetArray.initializeOffsets(bitPosition));
        checkInt14IndexedOffsetArray(int14IndexedOffsetArray);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final Int14IndexedOffsetArray int14IndexedOffsetArray =
                createInt14IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 9;
        assertEquals(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                int14IndexedOffsetArray.initializeOffsets(bitPosition));

        final short offsetShift = 1;
        checkOffsets(int14IndexedOffsetArray, offsetShift);
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final Int14IndexedOffsetArray int14IndexedOffsetArray =
                createInt14IndexedOffsetArray(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        int14IndexedOffsetArray.write(writer);
        writer.close();
        checkInt14IndexedOffsetArray(int14IndexedOffsetArray);
        final Int14IndexedOffsetArray readInt14IndexedOffsetArray = new Int14IndexedOffsetArray(file);
        checkInt14IndexedOffsetArray(readInt14IndexedOffsetArray);
        assertTrue(int14IndexedOffsetArray.equals(readInt14IndexedOffsetArray));
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final Int14IndexedOffsetArray int14IndexedOffsetArray =
                createInt14IndexedOffsetArray(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        final int bitPosition = 8;
        writer.writeBits(0, bitPosition);
        int14IndexedOffsetArray.write(writer);
        writer.close();

        final short offsetShift = 1;
        checkOffsets(int14IndexedOffsetArray, offsetShift);
    }

    @Test(expected=ZserioError.class)
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final Int14IndexedOffsetArray int14IndexedOffsetArray =
                createInt14IndexedOffsetArray(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        int14IndexedOffsetArray.write(writer, false);
        writer.close();
    }

    private void writeInt14IndexedOffsetArrayToFile(File file, boolean writeWrongOffsets) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        long currentOffset = ELEMENT0_OFFSET;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && writeWrongOffsets)
                writer.writeUnsignedInt(WRONG_OFFSET);
            else
                writer.writeUnsignedInt(currentOffset);
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }

        writer.writeBits(SPACER_VALUE, 1);
        writer.writeBits(0, 7);

        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits(i, ELEMENT_SIZE);
            if ((i + 1) != NUM_ELEMENTS)
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
        }

        writer.close();
    }

    private void checkOffsets(Int14IndexedOffsetArray int14IndexedOffsetArray, short offsetShift)
    {
        final long[] offsets = int14IndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (long offset : offsets)
        {
            assertEquals(expectedOffset, offset);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    private void checkInt14IndexedOffsetArray(Int14IndexedOffsetArray int14IndexedOffsetArray)
    {
        final short offsetShift = 0;
        checkOffsets(int14IndexedOffsetArray, offsetShift);

        assertEquals(SPACER_VALUE, int14IndexedOffsetArray.getSpacer());

        final short[] data = int14IndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            assertEquals(i, data[i]);
    }

    private Int14IndexedOffsetArray createInt14IndexedOffsetArray(boolean createWrongOffsets)
    {
        final Int14IndexedOffsetArray int14IndexedOffsetArray = new Int14IndexedOffsetArray();

        final long[] offsets = new long[NUM_ELEMENTS];
        long currentOffset = ELEMENT0_OFFSET;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets[i] = WRONG_OFFSET;
            else
                offsets[i] = currentOffset;
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
        int14IndexedOffsetArray.setOffsets(offsets);
        int14IndexedOffsetArray.setSpacer(SPACER_VALUE);

        final short[] data = new short[NUM_ELEMENTS];
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            data[i] = i;
        int14IndexedOffsetArray.setData(data);

        return int14IndexedOffsetArray;
    }

    private static final short NUM_ELEMENTS = (short)5;

    private static final long  WRONG_OFFSET = (long)0;

    private static final long  ELEMENT0_OFFSET = (long)(NUM_ELEMENTS * Integer.SIZE + Byte.SIZE) / Byte.SIZE;
    private static final int   ELEMENT_SIZE = 14;
    private static final int   ALIGNED_ELEMENT_SIZE = 2 * Byte.SIZE;
    private static final int   ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / Byte.SIZE;

    private static final byte  SPACER_VALUE = 1;

    private static final int   INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE = NUM_ELEMENTS * Integer.SIZE + Byte.SIZE +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;
}
