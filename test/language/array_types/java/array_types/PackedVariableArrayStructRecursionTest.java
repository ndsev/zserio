package array_types;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import array_types.packed_variable_array_struct_recursion.Block;
import array_types.packed_variable_array_struct_recursion.PackedVariableArray;

public class PackedVariableArrayStructRecursionTest
{
    @Test
    public void bitSizeOfLength1() throws IOException, ZserioError
    {
        checkBitSizeOf(VARIABLE_ARRAY_LENGTH1);
    }

    @Test
    public void bitSizeOfLength2() throws IOException, ZserioError
    {
        checkBitSizeOf(VARIABLE_ARRAY_LENGTH2);
    }

    @Test
    public void bitSizeOfLength3() throws IOException, ZserioError
    {
        checkBitSizeOf(VARIABLE_ARRAY_LENGTH3);
    }

    @Test
    public void writeReadLength1() throws IOException, ZserioError
    {
        checkWriteRead(VARIABLE_ARRAY_LENGTH1);
    }

    @Test
    public void writeReadLength2() throws IOException, ZserioError
    {
        checkWriteRead(VARIABLE_ARRAY_LENGTH2);
    }

    @Test
    public void writeReadLength3() throws IOException, ZserioError
    {
        checkWriteRead(VARIABLE_ARRAY_LENGTH3);
    }

    private void checkBitSizeOf(int numElements) throws IOException, ZserioError
    {
        final PackedVariableArray packedVariableArray = createPackedVariableArray(numElements);

        final int unpackedBitsizeOf = calcUnpackedVariableArrayBitSize(numElements);
        final int packedBitsizeOf = packedVariableArray.bitSizeOf();

        final double minCompressionRatio = 0.9;
        assertTrue("Unpacked array has " + unpackedBitsizeOf + " bits, packed array has " + packedBitsizeOf +
                " bits, " + "compression ratio is " + packedBitsizeOf * 100.0 / unpackedBitsizeOf + "%!",
                unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf);
    }

    private void checkWriteRead(int numElements) throws IOException, ZserioError
    {
        final PackedVariableArray packedVariableArray = createPackedVariableArray(numElements);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        packedVariableArray.write(writer);
        writer.close();

        final long writtenBitPosition = writer.getBitPosition();
        assertEquals(packedVariableArray.bitSizeOf(), writtenBitPosition);
        assertEquals(packedVariableArray.initializeOffsets(0), writtenBitPosition);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final PackedVariableArray readPackedVariableArray = new PackedVariableArray(reader);
        assertEquals(packedVariableArray, readPackedVariableArray);
    }

    private PackedVariableArray createPackedVariableArray(int numElements)
    {
        final short byteCount = 1;
        final Block[] blocks = new Block[numElements];
        for (int i = 0; i < numElements; ++i)
            blocks[i] = createBlock(byteCount, false);

        return new PackedVariableArray(byteCount, numElements, blocks);
    }

    private Block createBlock(short byteCount, boolean isLast)
    {
        final short[] dataBytes = new short[byteCount];
        for (short i = 0; i < byteCount; ++i)
            dataBytes[i] = i;
        if (isLast)
            return new Block(byteCount, dataBytes, (short)0, null);

        final short blockTerminator = (short)(byteCount + 1);
        final Block block = createBlock(blockTerminator, blockTerminator > 5);

        return new Block(byteCount, dataBytes, blockTerminator, block);
    }

    private int calcUnpackedVariableArrayBitSize(int numElements)
    {
        int bitSize = 8; // byteCount
        bitSize += 8; // numElements
        final short byteCount = 1;
        for (int i = 0; i < numElements; ++i)
            bitSize += calcUnpackedBlockBitSize(byteCount, false);

        return bitSize;
    }

    private int calcUnpackedBlockBitSize(short byteCount, boolean isLast)
    {
        int bitSize = 8 * byteCount; // dataBytes[byteCount]
        bitSize += 8; // blockTerminator
        if (!isLast)
        {
            final short blockTerminator = (short)(byteCount + 1);
            bitSize += calcUnpackedBlockBitSize(blockTerminator, blockTerminator > 5);
        }

        return bitSize;
    }

    private static final int VARIABLE_ARRAY_LENGTH1 = 100;
    private static final int VARIABLE_ARRAY_LENGTH2 = 500;
    private static final int VARIABLE_ARRAY_LENGTH3 = 1000;
}
