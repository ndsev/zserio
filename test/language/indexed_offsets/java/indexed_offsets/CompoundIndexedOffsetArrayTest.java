package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import indexed_offsets.compound_indexed_offset_array.Compound;
import indexed_offsets.compound_indexed_offset_array.CompoundIndexedOffsetArray;

public class CompoundIndexedOffsetArrayTest
{
    @Test
    public void readConstructor() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeCompoundIndexedOffsetArrayToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray = new CompoundIndexedOffsetArray(reader);
        checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray);
    }

    @Test
    public void readConstructorWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final BitBuffer bitBuffer = writeCompoundIndexedOffsetArrayToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new CompoundIndexedOffsetArray(reader));
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray =
                createCompoundIndexedOffsetArray(createWrongOffsets);
        assertEquals(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE, compoundIndexedOffsetArray.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray =
                createCompoundIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 1;
        assertEquals(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                compoundIndexedOffsetArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray =
                createCompoundIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                compoundIndexedOffsetArray.initializeOffsets(bitPosition));
        checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray =
                createCompoundIndexedOffsetArray(createWrongOffsets);
        final int bitPosition = 9;
        assertEquals(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                compoundIndexedOffsetArray.initializeOffsets(bitPosition));

        final short offsetShift = 1;
        checkOffsets(compoundIndexedOffsetArray, offsetShift);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = false;
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray =
                createCompoundIndexedOffsetArray(createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(compoundIndexedOffsetArray);
        checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray);

        final CompoundIndexedOffsetArray readCompoundIndexedOffsetArray =
                SerializeUtil.deserialize(CompoundIndexedOffsetArray.class, bitBuffer);
        checkCompoundIndexedOffsetArray(readCompoundIndexedOffsetArray);
        assertTrue(compoundIndexedOffsetArray.equals(readCompoundIndexedOffsetArray));
    }

    @Test
    public void writeReadWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray =
                createCompoundIndexedOffsetArray(createWrongOffsets);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int bitPosition = 8;
        writer.writeBits(0, bitPosition);
        compoundIndexedOffsetArray.initializeOffsets(writer.getBitPosition());
        compoundIndexedOffsetArray.write(writer);
        final short offsetShift = 1;
        checkOffsets(compoundIndexedOffsetArray, offsetShift);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        assertEquals(0, reader.readBits(bitPosition));
        final CompoundIndexedOffsetArray readCompoundIndexedOffsetArray =
                new CompoundIndexedOffsetArray(reader);
        checkOffsets(readCompoundIndexedOffsetArray, offsetShift);
        assertTrue(compoundIndexedOffsetArray.equals(readCompoundIndexedOffsetArray));
    }

    @Test
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray =
                createCompoundIndexedOffsetArray(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> compoundIndexedOffsetArray.write(writer));
        writer.close();
    }

    private BitBuffer writeCompoundIndexedOffsetArrayToBitBuffer(boolean writeWrongOffsets) throws IOException
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
                writer.writeBits(i, 32);
                writer.writeBits(i % 8, 3);
                if ((i + 1) != NUM_ELEMENTS)
                    writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE);
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkOffsets(CompoundIndexedOffsetArray compoundIndexedOffsetArray, short offsetShift)
    {
        final long[] offsets = compoundIndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length);
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (long offset : offsets)
        {
            assertEquals(expectedOffset, offset);
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE;
        }
    }

    private void checkCompoundIndexedOffsetArray(CompoundIndexedOffsetArray compoundIndexedOffsetArray)
    {
        final short offsetShift = 0;
        checkOffsets(compoundIndexedOffsetArray, offsetShift);

        assertEquals(SPACER_VALUE, compoundIndexedOffsetArray.getSpacer());

        final Compound[] data = compoundIndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            final Compound compound = data[i];
            assertEquals(i, compound.getId());
            assertEquals((byte)(i % 8), compound.getValue());
        }
    }

    private CompoundIndexedOffsetArray createCompoundIndexedOffsetArray(boolean createWrongOffsets)
    {
        final CompoundIndexedOffsetArray compoundIndexedOffsetArray = new CompoundIndexedOffsetArray();

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
        compoundIndexedOffsetArray.setOffsets(offsets);
        compoundIndexedOffsetArray.setSpacer(SPACER_VALUE);

        final Compound[] data = new Compound[NUM_ELEMENTS];
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            final Compound compound = new Compound(i, (byte)(i % 8));
            data[i] = compound;
        }
        compoundIndexedOffsetArray.setData(data);

        return compoundIndexedOffsetArray;
    }

    private static final short NUM_ELEMENTS = (short)5;

    private static final long WRONG_OFFSET = (long)0;

    private static final long ELEMENT0_OFFSET = (long)(NUM_ELEMENTS * Integer.SIZE + Byte.SIZE) / Byte.SIZE;
    private static final int ELEMENT_SIZE = 35;
    private static final int ALIGNED_ELEMENT_SIZE = 5 * Byte.SIZE;
    private static final int ALIGNED_ELEMENT_BYTE_SIZE = ALIGNED_ELEMENT_SIZE / Byte.SIZE;

    private static final byte SPACER_VALUE = 1;

    private static final int COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE =
            NUM_ELEMENTS * Integer.SIZE + Byte.SIZE + (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE + ELEMENT_SIZE;
}
