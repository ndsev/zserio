package offsets;

import static org.junit.Assert.*;

import indexed_offsets.packed_indexed_offset_array_holder.AutoIndexedOffsetArray;
import indexed_offsets.packed_indexed_offset_array_holder.OffsetArray;
import indexed_offsets.packed_indexed_offset_array_holder.OffsetHolder;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class PackedIndexedOffsetArrayHolderTest
{
    @Test
    public void bitSizeOfLength1() throws IOException, ZserioError
    {
        checkBitSizeOf(NUM_ELEMENTS1);
    }

    @Test
    public void bitSizeOfLength2() throws IOException, ZserioError
    {
        checkBitSizeOf(NUM_ELEMENTS2);
    }

    @Test
    public void bitSizeOfLength3() throws IOException, ZserioError
    {
        checkBitSizeOf(NUM_ELEMENTS3);
    }

    @Test
    public void writeReadLength1() throws IOException, ZserioError
    {
        checkWriteRead(NUM_ELEMENTS1);
    }

    @Test
    public void writeReadLength2() throws IOException, ZserioError
    {
        checkWriteRead(NUM_ELEMENTS2);
    }

    @Test
    public void writeReadLength3() throws IOException, ZserioError
    {
        checkWriteRead(NUM_ELEMENTS3);
    }

    private void checkBitSizeOf(int numElements) throws IOException, ZserioError
    {
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(numElements);
        final int unpackedBitsizeOf = calcAutoIndexedOffsetArrayBitSize(numElements);
        final int packedBitsizeOf = autoIndexedOffsetArray.bitSizeOf();
        final double minCompressionRatio = 0.82;

        assertTrue("Unpacked array has " + unpackedBitsizeOf + " bits, packed array has " + packedBitsizeOf +
                " bits, " + "compression ratio is " + packedBitsizeOf * 100.0 / unpackedBitsizeOf + "%!",
                unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf);
    }

    private void checkWriteRead(int numElements) throws IOException, ZserioError
    {
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(numElements);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        autoIndexedOffsetArray.write(writer);
        writer.close();

        final long writtenBitPosition = writer.getBitPosition();
        assertEquals(autoIndexedOffsetArray.bitSizeOf(), writtenBitPosition);
        assertEquals(autoIndexedOffsetArray.initializeOffsets(0), writtenBitPosition);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final AutoIndexedOffsetArray readAutoIndexedOffsetArray = new AutoIndexedOffsetArray(reader);
        assertEquals(autoIndexedOffsetArray, readAutoIndexedOffsetArray);
    }

    private AutoIndexedOffsetArray createAutoIndexedOffsetArray(int numElements)
    {
        final OffsetHolder[] offsetHolders = new OffsetHolder[numElements + 1];
        for (int i = 0; i < numElements + 1; ++i)
            offsetHolders[i] = new OffsetHolder(0, new long[] {0}, i);

        final int[] data1 = new int[numElements];
        for (int i = 0; i < numElements; ++i)
            data1[i] = i;

        final int[] data2 = new int[numElements];
        for (int i = 0; i < numElements; ++i)
            data2[i] = i * 2;

        return new AutoIndexedOffsetArray(new OffsetArray(offsetHolders), data1, data2);
    }

    private int calcAutoIndexedOffsetArrayBitSize(int numElements)
    {
        int bitSize = 0;
        for (int i = 0; i < numElements + 1; ++i)
        {
            bitSize += 32; // offset[i]
            bitSize += 32; // offsets[1]
            bitSize += 32; // value[i]
        }

        for (int i = 0; i < numElements; ++i)
            bitSize += 32; // data1[i]
        for (int i = 0; i < numElements; ++i)
            bitSize += 32; // data2[i]

        return bitSize;
    }

    private static final int NUM_ELEMENTS1 = 50;
    private static final int NUM_ELEMENTS2 = 100;
    private static final int NUM_ELEMENTS3 = 1000;
}
