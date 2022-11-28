package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import indexed_offsets.empty_indexed_offset_array.EmptyIndexedOffsetArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class EmptyIndexedOffsetArrayTest
{
    @Test
    public void readConstructor() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer = writeEmptyIndexedOffsetArrayToBitBuffer();
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = new EmptyIndexedOffsetArray(reader);
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
        assertEquals(EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                emptyIndexedOffsetArray.initializeOffsets(bitPosition));
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
    public void writeRead() throws IOException, ZserioError
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = createEmptyIndexedOffsetArray();
        final BitBuffer bitBuffer = SerializeUtil.serialize(emptyIndexedOffsetArray);
        checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray);

        final EmptyIndexedOffsetArray readEmptyIndexedOffsetArray = SerializeUtil.deserialize(
                EmptyIndexedOffsetArray.class, bitBuffer);
        checkEmptyIndexedOffsetArray(readEmptyIndexedOffsetArray);
        assertTrue(emptyIndexedOffsetArray.equals(readEmptyIndexedOffsetArray));
    }

    private BitBuffer writeEmptyIndexedOffsetArrayToBitBuffer() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(SPACER_VALUE, 1);
            writer.writeBits(FIELD_VALUE, 6);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkEmptyIndexedOffsetArray(EmptyIndexedOffsetArray emptyIndexedOffsetArray)
    {
        final long[] offsets = emptyIndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);

        assertEquals(SPACER_VALUE, emptyIndexedOffsetArray.getSpacer());
        assertEquals(FIELD_VALUE, emptyIndexedOffsetArray.getField());

        final byte[] data = emptyIndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length);
    }

    private EmptyIndexedOffsetArray createEmptyIndexedOffsetArray()
    {
        final EmptyIndexedOffsetArray emptyIndexedOffsetArray = new EmptyIndexedOffsetArray();

        final long[] offsets = new long[NUM_ELEMENTS];
        emptyIndexedOffsetArray.setOffsets(offsets);
        emptyIndexedOffsetArray.setSpacer(SPACER_VALUE);
        emptyIndexedOffsetArray.setField(FIELD_VALUE);

        final byte[] data = new byte[NUM_ELEMENTS];
        emptyIndexedOffsetArray.setData(data);

        return emptyIndexedOffsetArray;
    }

    private static final short NUM_ELEMENTS = (short)0;

    private static final byte  SPACER_VALUE = 1;
    private static final byte  FIELD_VALUE = 63;

    private static final int   EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE = 1 + 6;
}
