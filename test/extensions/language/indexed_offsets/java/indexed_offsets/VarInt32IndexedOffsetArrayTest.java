package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import indexed_offsets.varint32_indexed_offset_array.VarInt32IndexedOffsetArray;

public class VarInt32IndexedOffsetArrayTest
{
    @Test
    public void readConstructor() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeVarInt32IndexedOffsetArrayToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray = new VarInt32IndexedOffsetArray(reader);
        checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);
    }

    @Test
    public void readConstructorWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final BitBuffer bitBuffer = writeVarInt32IndexedOffsetArrayToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new VarInt32IndexedOffsetArray(reader));
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        assertEquals(getVarInt32IndexedOffsetArrayBitSize(), varint32IndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 1;
        assertEquals(getVarInt32IndexedOffsetArrayBitSize() - bitPosition,
                varint32IndexedOffsetArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(getVarInt32IndexedOffsetArrayBitSize(),
                varint32IndexedOffsetArray.initializeOffsets(bitPosition));
        checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 9;
        assertEquals(getVarInt32IndexedOffsetArrayBitSize() + bitPosition - 1,
                varint32IndexedOffsetArray.initializeOffsets(bitPosition));

        final short offsetShift = 1;
        checkOffsets(varint32IndexedOffsetArray, offsetShift);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = false;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(varint32IndexedOffsetArray);
        checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);

        final VarInt32IndexedOffsetArray readVarint32IndexedOffsetArray =
                SerializeUtil.deserialize(VarInt32IndexedOffsetArray.class, bitBuffer);
        checkVarInt32IndexedOffsetArray(readVarint32IndexedOffsetArray);
        assertTrue(varint32IndexedOffsetArray.equals(readVarint32IndexedOffsetArray));
    }

    @Test
    public void writeReadWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int bitPosition = 8;
        writer.writeBits(0, bitPosition);
        varint32IndexedOffsetArray.initializeOffsets(writer.getBitPosition());
        varint32IndexedOffsetArray.write(writer);
        final short offsetShift = 1;
        checkOffsets(varint32IndexedOffsetArray, offsetShift);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        assertEquals(0, reader.readBits(bitPosition));
        final VarInt32IndexedOffsetArray readVarint32IndexedOffsetArray =
                new VarInt32IndexedOffsetArray(reader);
        checkOffsets(readVarint32IndexedOffsetArray, offsetShift);
        assertTrue(varint32IndexedOffsetArray.equals(readVarint32IndexedOffsetArray));
    }

    @Test
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> varint32IndexedOffsetArray.write(writer));
        writer.close();
    }

    private BitBuffer writeVarInt32IndexedOffsetArrayToBitBuffer(boolean writeWrongOffsets) throws IOException
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
                currentOffset += BitSizeOfCalculator.getBitSizeOfVarInt32(i) / Byte.SIZE;
            }

            writer.writeBits(SPACER_VALUE, 1);
            writer.writeBits(0, 7);

            for (short i = 0; i < NUM_ELEMENTS; ++i)
                writer.writeVarInt32(i);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkOffsets(VarInt32IndexedOffsetArray varint32IndexedOffsetArray, short offsetShift)
    {
        final long[] offsets = varint32IndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (int i = 0; i < offsets.length; ++i)
        {
            assertEquals(expectedOffset, offsets[i]);
            expectedOffset += BitSizeOfCalculator.getBitSizeOfVarInt32(i) / Byte.SIZE;
        }
    }

    private void checkVarInt32IndexedOffsetArray(VarInt32IndexedOffsetArray varint32IndexedOffsetArray)
    {
        final short offsetShift = 0;
        checkOffsets(varint32IndexedOffsetArray, offsetShift);

        assertEquals(SPACER_VALUE, varint32IndexedOffsetArray.getSpacer());

        final int[] data = varint32IndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            assertEquals(i, data[i]);
    }

    private VarInt32IndexedOffsetArray createVarInt32IndexedOffsetArray(boolean createWrongOffsets)
    {
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray = new VarInt32IndexedOffsetArray();

        final long[] offsets = new long[NUM_ELEMENTS];
        long currentOffset = ELEMENT0_OFFSET;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets[i] = WRONG_OFFSET;
            else
                offsets[i] = currentOffset;
            currentOffset += BitSizeOfCalculator.getBitSizeOfVarInt32(i) / 8;
        }
        varint32IndexedOffsetArray.setOffsets(offsets);
        varint32IndexedOffsetArray.setSpacer(SPACER_VALUE);

        final int[] data = new int[NUM_ELEMENTS];
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            data[i] = i;
        varint32IndexedOffsetArray.setData(data);

        return varint32IndexedOffsetArray;
    }

    private long getVarInt32IndexedOffsetArrayBitSize()
    {
        long bitSize = ELEMENT0_OFFSET * Byte.SIZE;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            bitSize += BitSizeOfCalculator.getBitSizeOfVarInt32(i);

        return bitSize;
    }

    private static final short NUM_ELEMENTS = (short)5;
    private static final long WRONG_OFFSET = (long)0;

    private static final long ELEMENT0_OFFSET = (long)(NUM_ELEMENTS * Integer.SIZE + Byte.SIZE) / Byte.SIZE;

    private static final byte SPACER_VALUE = 1;
}
