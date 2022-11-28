package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import indexed_offsets.bit5_indexed_offset_array.Bit5IndexedOffsetArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class Bit5IndexedOffsetArrayTest
{
    @Test
    public void readConstructor() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeBit5IndexedOffsetArrayToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = new Bit5IndexedOffsetArray(reader);
        checkBit5IndexedOffsetArray(bit5IndexedOffsetArray);
    }

    @Test
    public void readConstructorWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final BitBuffer bitBuffer = writeBit5IndexedOffsetArrayToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new Bit5IndexedOffsetArray(reader));
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = createBit5IndexedOffsetArray(createWrongOffsets);
        assertEquals(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE, bit5IndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = createBit5IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 1;
        assertEquals(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                bit5IndexedOffsetArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = createBit5IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE, bit5IndexedOffsetArray.initializeOffsets(bitPosition));
        checkBit5IndexedOffsetArray(bit5IndexedOffsetArray);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = createBit5IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 9;
        assertEquals(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                bit5IndexedOffsetArray.initializeOffsets(bitPosition));

        final short offsetShift = 1;
        checkOffsets(bit5IndexedOffsetArray, offsetShift);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = false;
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = createBit5IndexedOffsetArray(createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(bit5IndexedOffsetArray);
        checkBit5IndexedOffsetArray(bit5IndexedOffsetArray);

        final Bit5IndexedOffsetArray readBit5IndexedOffsetArray =
                SerializeUtil.deserialize(Bit5IndexedOffsetArray.class, bitBuffer);
        checkBit5IndexedOffsetArray(readBit5IndexedOffsetArray);
        assertTrue(bit5IndexedOffsetArray.equals(readBit5IndexedOffsetArray));
    }

    @Test
    public void writeReadWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = createBit5IndexedOffsetArray(createWrongOffsets);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int bitPosition = 8;
        writer.writeBits(0, bitPosition);
        bit5IndexedOffsetArray.initializeOffsets(writer.getBitPosition());
        bit5IndexedOffsetArray.write(writer);
        final short offsetShift = 1;
        checkOffsets(bit5IndexedOffsetArray, offsetShift);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        assertEquals(0, reader.readBits(bitPosition));
        final Bit5IndexedOffsetArray readBit5IndexedOffsetArray = new Bit5IndexedOffsetArray(reader);
        checkOffsets(readBit5IndexedOffsetArray, offsetShift);
        assertTrue(bit5IndexedOffsetArray.equals(readBit5IndexedOffsetArray));
    }

    @Test
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = createBit5IndexedOffsetArray(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> bit5IndexedOffsetArray.write(writer));
        writer.close();
    }

    private BitBuffer writeBit5IndexedOffsetArrayToBitBuffer(boolean writeWrongOffsets) throws IOException
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
                currentOffset += ALIGNED_ELEMENT_BYTE_SIZE;
            }

            writer.writeBits(SPACER_VALUE, 1);
            writer.writeBits(0, 7);

            for (short i = 0; i < NUM_ELEMENTS; ++i)
            {
                writer.writeBits(i % 64, ELEMENT_SIZE);
                if ((i + 1) != NUM_ELEMENTS)
                    writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkOffsets(Bit5IndexedOffsetArray bit5IndexedOffsetArray, short offsetShift)
    {
        final long[] offsets = bit5IndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (long offset : offsets)
        {
            assertEquals(expectedOffset, offset);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    private void checkBit5IndexedOffsetArray(Bit5IndexedOffsetArray bit5IndexedOffsetArray)
    {
        final short offsetShift = 0;
        checkOffsets(bit5IndexedOffsetArray, offsetShift);

        assertEquals(SPACER_VALUE, bit5IndexedOffsetArray.getSpacer());

        final byte[] data = bit5IndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            assertEquals(i % 64, data[i]);
    }

    private Bit5IndexedOffsetArray createBit5IndexedOffsetArray(boolean createWrongOffsets)
    {
        final Bit5IndexedOffsetArray bit5IndexedOffsetArray = new Bit5IndexedOffsetArray();

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
        bit5IndexedOffsetArray.setOffsets(offsets);
        bit5IndexedOffsetArray.setSpacer(SPACER_VALUE);

        final byte[] data = new byte[NUM_ELEMENTS];
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            data[i] = (byte)(i % 64);
        bit5IndexedOffsetArray.setData(data);

        return bit5IndexedOffsetArray;
    }

    private static final short NUM_ELEMENTS = (short)5;

    private static final long  WRONG_OFFSET = (long)0;

    private static final long  ELEMENT0_OFFSET = (long)(NUM_ELEMENTS * Integer.SIZE + Byte.SIZE) / Byte.SIZE;
    private static final int   ELEMENT_SIZE = 5;
    private static final int   ALIGNED_ELEMENT_SIZE = Byte.SIZE;
    private static final int   ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / Byte.SIZE;

    private static final byte  SPACER_VALUE = 1;

    private static final int   BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE = NUM_ELEMENTS * Integer.SIZE + Byte.SIZE +
            (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;
}
