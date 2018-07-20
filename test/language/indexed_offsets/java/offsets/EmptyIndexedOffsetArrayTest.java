package offsets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import indexed_offsets.empty_indexed_offset_array.EmptyIndexedOffsetArray;

import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedByteArray;
import zserio.runtime.array.UnsignedIntArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class EmptyIndexedOffsetArrayTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeEmptyIndexedOffsetArrayToFile(file);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = new EmptyIndexedOffsetArray(stream);
        stream.close();
        checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);
    }

    @Test
    public void bitSizeOf()
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = createEmptyIndexedOffsetArray();
        assertEquals(EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE, emptyIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = createEmptyIndexedOffsetArray();
        final int bitPosition = 1;
        assertEquals(EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE, emptyIndexedOffsetArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = createEmptyIndexedOffsetArray();
        final int bitPosition = 0;
        assertEquals(EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE, emptyIndexedOffsetArray.initializeOffsets(bitPosition));
        checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = createEmptyIndexedOffsetArray();
        final int bitPosition = 9;
        assertEquals(EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition,
                emptyIndexedOffsetArray.initializeOffsets(bitPosition));
        checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = createEmptyIndexedOffsetArray();
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        emptyIndexedOffsetArray.write(writer);
        writer.close();
        checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);
        final EmptyIndexedOffsetArray readEmptyIndexedOffsetArray = new EmptyIndexedOffsetArray(file);
        checkEmptyIndexedOffsetArray(readEmptyIndexedOffsetArray);
        assertTrue(emptyIndexedOffsetArray.equals(readEmptyIndexedOffsetArray));
    }

    private void writeEmptyIndexedOffsetArrayToFile(File file) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeBits(SPACER_VALUE, 1);
        writer.writeBits(FIELD_VALUE, 6);

        writer.close();
    }

    private void checkEmptyIndexedOffsetArray(EmptyIndexedOffsetArray emptyIndexedOffsetArray)
    {
        final UnsignedIntArray offsets = emptyIndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length());

        assertEquals(SPACER_VALUE, emptyIndexedOffsetArray.getSpacer());
        assertEquals(FIELD_VALUE, emptyIndexedOffsetArray.getField());

        final UnsignedByteArray data = emptyIndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length());
    }

    private EmptyIndexedOffsetArray createEmptyIndexedOffsetArray()
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = new EmptyIndexedOffsetArray();

        final UnsignedIntArray offsets = new UnsignedIntArray(NUM_ELEMENTS);
        emptyIndexedOffsetArray.setOffsets(offsets);
        emptyIndexedOffsetArray.setSpacer(SPACER_VALUE);
        emptyIndexedOffsetArray.setField(FIELD_VALUE);

        final UnsignedByteArray data = new UnsignedByteArray(NUM_ELEMENTS);
        emptyIndexedOffsetArray.setData(data);

        return emptyIndexedOffsetArray;
    }

    private static final short NUM_ELEMENTS = (short)0;

    private static final byte  SPACER_VALUE = 1;
    private static final byte  FIELD_VALUE = 63;

    private static final int   EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE = 1 + 6;
}
