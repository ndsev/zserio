package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import indexed_offsets.bool_indexed_offset_array.BoolIndexedOffsetArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class BoolIndexedOffsetArrayTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final File file = new File("test.bin");
        writeBoolIndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final BoolIndexedOffsetArray boolIndexedOffsetArray = new BoolIndexedOffsetArray(stream);
        stream.close();
        checkBoolIndexedOffsetArray(boolIndexedOffsetArray);
    }

    @Test
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final File file = new File("test.bin");
        writeBoolIndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new BoolIndexedOffsetArray(stream));
        stream.close();
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final BoolIndexedOffsetArray boolIndexedOffsetArray = createBoolIndexedOffsetArray(createWrongOffsets);
        assertEquals(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE, boolIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final BoolIndexedOffsetArray boolIndexedOffsetArray = createBoolIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 1;
        assertEquals(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                boolIndexedOffsetArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final BoolIndexedOffsetArray boolIndexedOffsetArray = createBoolIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE, boolIndexedOffsetArray.initializeOffsets(bitPosition));
        checkBoolIndexedOffsetArray(boolIndexedOffsetArray);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final BoolIndexedOffsetArray boolIndexedOffsetArray = createBoolIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 9;
        assertEquals(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                boolIndexedOffsetArray.initializeOffsets(bitPosition));

        final short offsetShift = 1;
        checkOffsets(boolIndexedOffsetArray, offsetShift);
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = false;
        final BoolIndexedOffsetArray boolIndexedOffsetArray = createBoolIndexedOffsetArray(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        boolIndexedOffsetArray.write(writer);
        writer.close();
        checkBoolIndexedOffsetArray(boolIndexedOffsetArray);
        final BoolIndexedOffsetArray readBoolIndexedOffsetArray = new BoolIndexedOffsetArray(file);
        checkBoolIndexedOffsetArray(readBoolIndexedOffsetArray);
        assertTrue(boolIndexedOffsetArray.equals(readBoolIndexedOffsetArray));
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final BoolIndexedOffsetArray boolIndexedOffsetArray = createBoolIndexedOffsetArray(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        final int bitPosition = 8;
        writer.writeBits(0, bitPosition);
        boolIndexedOffsetArray.initializeOffsets(writer.getBitPosition());
        boolIndexedOffsetArray.write(writer);
        writer.close();

        final short offsetShift = 1;
        checkOffsets(boolIndexedOffsetArray, offsetShift);
    }

    @Test
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final BoolIndexedOffsetArray boolIndexedOffsetArray = createBoolIndexedOffsetArray(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> boolIndexedOffsetArray.write(writer));
        writer.close();
    }

    private void writeBoolIndexedOffsetArrayToFile(File file, boolean writeWrongOffsets) throws IOException
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
            writer.writeBits((i & 0x01), ELEMENT_SIZE);
            if ((i + 1) != NUM_ELEMENTS)
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
        }

        writer.close();
    }

    private void checkOffsets(BoolIndexedOffsetArray boolIndexedOffsetArray, short offsetShift)
    {
        final long[] offsets = boolIndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (long offset : offsets)
        {
            assertEquals(expectedOffset, offset);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    private void checkBoolIndexedOffsetArray(BoolIndexedOffsetArray boolIndexedOffsetArray)
    {
        final short offsetShift = 0;
        checkOffsets(boolIndexedOffsetArray, offsetShift);

        assertEquals(SPACER_VALUE, boolIndexedOffsetArray.getSpacer());

        final boolean[] data = boolIndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            assertEquals(((i & 0x01) != 0) ? true : false, data[i]);
    }

    private BoolIndexedOffsetArray createBoolIndexedOffsetArray(boolean createWrongOffsets)
    {
        final BoolIndexedOffsetArray boolIndexedOffsetArray = new BoolIndexedOffsetArray();

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
        boolIndexedOffsetArray.setOffsets(offsets);
        boolIndexedOffsetArray.setSpacer(SPACER_VALUE);

        final boolean[] data = new boolean[NUM_ELEMENTS];
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            data[i] = ((i & 0x01) != 0) ? true : false;
        boolIndexedOffsetArray.setData(data);

        return boolIndexedOffsetArray;
    }

    private static final short NUM_ELEMENTS = (short)5;

    private static final long  WRONG_OFFSET = (long)0;

    private static final long  ELEMENT0_OFFSET = (long)(NUM_ELEMENTS * Integer.SIZE + Byte.SIZE) / Byte.SIZE;
    private static final int   ELEMENT_SIZE = 1;
    private static final int   ALIGNED_ELEMENT_SIZE = Byte.SIZE;
    private static final int   ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / Byte.SIZE;

    private static final byte  SPACER_VALUE = 1;

    private static final int   BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE = NUM_ELEMENTS * Integer.SIZE + Byte.SIZE +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;
}
