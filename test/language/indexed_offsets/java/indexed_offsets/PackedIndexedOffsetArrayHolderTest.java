package indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import indexed_offsets.packed_indexed_offset_array_holder.AutoIndexedOffsetArray;
import indexed_offsets.packed_indexed_offset_array_holder.OffsetArray;
import indexed_offsets.packed_indexed_offset_array_holder.OffsetHolder;

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

    @Test
    public void writeReadFileLength1() throws IOException, ZserioError
    {
        checkWriteReadFile(NUM_ELEMENTS1);
    }

    @Test
    public void writeReadFileLength2() throws IOException, ZserioError
    {
        checkWriteReadFile(NUM_ELEMENTS2);
    }

    @Test
    public void writeReadFileLength3() throws IOException, ZserioError
    {
        checkWriteReadFile(NUM_ELEMENTS3);
    }

    private void checkBitSizeOf(int numElements) throws IOException, ZserioError
    {
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(numElements);
        final int unpackedBitsizeOf = calcAutoIndexedOffsetArrayBitSize(numElements);
        final int packedBitsizeOf = autoIndexedOffsetArray.bitSizeOf();
        final double minCompressionRatio = 0.82;

        assertTrue(unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf,
                ()
                        -> "Unpacked array has " + unpackedBitsizeOf + " bits, packed array has " +
                        packedBitsizeOf + " bits, "
                        + "compression ratio is " + packedBitsizeOf * 100.0 / unpackedBitsizeOf + "%!");
    }

    private void checkWriteRead(int numElements) throws IOException, ZserioError
    {
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(numElements);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        autoIndexedOffsetArray.write(writer);

        final long writtenBitPosition = writer.getBitPosition();
        assertEquals(autoIndexedOffsetArray.bitSizeOf(), writtenBitPosition);
        assertEquals(autoIndexedOffsetArray.initializeOffsets(), writtenBitPosition);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final AutoIndexedOffsetArray readAutoIndexedOffsetArray = new AutoIndexedOffsetArray(reader);
        assertEquals(autoIndexedOffsetArray, readAutoIndexedOffsetArray);
    }

    private void checkWriteReadFile(int numElements) throws IOException, ZserioError
    {
        final AutoIndexedOffsetArray autoIndexedOffsetArray = createAutoIndexedOffsetArray(numElements);
        final File file = new File(BLOB_NAME_BASE + numElements + ".blob");
        SerializeUtil.serializeToFile(autoIndexedOffsetArray, file);

        final AutoIndexedOffsetArray readAutoIndexedOffsetArray =
                SerializeUtil.deserializeFromFile(AutoIndexedOffsetArray.class, file);
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

        final AutoIndexedOffsetArray autoIndexedOffsetArray =
                new AutoIndexedOffsetArray(new OffsetArray(offsetHolders), data1, data2);
        autoIndexedOffsetArray.initializeOffsets();

        return autoIndexedOffsetArray;
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

    private static final String BLOB_NAME_BASE = "packed_indexed_offset_array_holder_";

    private static final int NUM_ELEMENTS1 = 50;
    private static final int NUM_ELEMENTS2 = 100;
    private static final int NUM_ELEMENTS3 = 1000;
}
