package offsets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import indexed_offsets.varint32_indexed_offset_array.VarInt32IndexedOffsetArray;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedIntArray;
import zserio.runtime.array.VarInt32Array;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class VarInt32IndexedOffsetArrayTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final File file = new File("test.bin");
        writeVarInt32IndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray = new VarInt32IndexedOffsetArray(stream);
        stream.close();
        checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);
    }

    @Test(expected=ZserioError.class)
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final File file = new File("test.bin");
        writeVarInt32IndexedOffsetArrayToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray = new VarInt32IndexedOffsetArray(stream);
        stream.close();
        checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);
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
    public void write() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        varint32IndexedOffsetArray.write(writer);
        writer.close();
        checkVarInt32IndexedOffsetArray(varint32IndexedOffsetArray);
        final VarInt32IndexedOffsetArray readVarInt32IndexedOffsetArray = new VarInt32IndexedOffsetArray(file);
        checkVarInt32IndexedOffsetArray(readVarInt32IndexedOffsetArray);
        assertTrue(varint32IndexedOffsetArray.equals(readVarInt32IndexedOffsetArray));
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        final int bitPosition = 8;
        writer.writeBits(0, bitPosition);
        varint32IndexedOffsetArray.write(writer);
        writer.close();

        final short offsetShift = 1;
        checkOffsets(varint32IndexedOffsetArray, offsetShift);
    }

    @Test(expected=ZserioError.class)
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray =
                createVarInt32IndexedOffsetArray(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        varint32IndexedOffsetArray.write(writer, false);
        writer.close();
    }

    private void writeVarInt32IndexedOffsetArrayToFile(File file, boolean writeWrongOffsets) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

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

        writer.close();
    }

    private void checkOffsets(VarInt32IndexedOffsetArray varint32IndexedOffsetArray, short offsetShift)
    {
        final UnsignedIntArray offsets = varint32IndexedOffsetArray.getOffsets();
        assertEquals(NUM_ELEMENTS, offsets.length());
        long expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (int i = 0; i < offsets.length(); ++i)
        {
            assertEquals(expectedOffset, offsets.elementAt(i));
            expectedOffset += BitSizeOfCalculator.getBitSizeOfVarInt32(i) / Byte.SIZE;
        }
    }

    private void checkVarInt32IndexedOffsetArray(VarInt32IndexedOffsetArray varint32IndexedOffsetArray)
    {
        final short offsetShift = 0;
        checkOffsets(varint32IndexedOffsetArray, offsetShift);

        assertEquals(SPACER_VALUE, varint32IndexedOffsetArray.getSpacer());

        final VarInt32Array data = varint32IndexedOffsetArray.getData();
        assertEquals(NUM_ELEMENTS, data.length());
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            assertEquals(i, data.elementAt(i));
    }

    private VarInt32IndexedOffsetArray createVarInt32IndexedOffsetArray(boolean createWrongOffsets)
    {
        final VarInt32IndexedOffsetArray varint32IndexedOffsetArray = new VarInt32IndexedOffsetArray();

        final UnsignedIntArray offsets = new UnsignedIntArray(NUM_ELEMENTS);
        long currentOffset = ELEMENT0_OFFSET;
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets.setElementAt(WRONG_OFFSET, i);
            else
                offsets.setElementAt(currentOffset, i);
            currentOffset += BitSizeOfCalculator.getBitSizeOfVarInt32(i);
        }
        varint32IndexedOffsetArray.setOffsets(offsets);
        varint32IndexedOffsetArray.setSpacer(SPACER_VALUE);

        final VarInt32Array data = new VarInt32Array(NUM_ELEMENTS);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
            data.setElementAt(i, i);
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
    private static final long  WRONG_OFFSET = (long)0;

    private static final long  ELEMENT0_OFFSET = (long)(NUM_ELEMENTS * Integer.SIZE + Byte.SIZE) / Byte.SIZE;

    private static final byte  SPACER_VALUE = 1;
}
