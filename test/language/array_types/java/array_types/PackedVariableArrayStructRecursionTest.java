package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import zserio.runtime.ZserioError;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;
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
        assertTrue(unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf, () ->
                "Unpacked array has " + unpackedBitsizeOf + " bits, packed array has " + packedBitsizeOf +
                " bits, " + "compression ratio is " + packedBitsizeOf * 100.0 / unpackedBitsizeOf + "%!");
    }

    private void checkWriteRead(int numElements) throws IOException, ZserioError
    {
        final PackedVariableArray packedVariableArray = createPackedVariableArray(numElements);
        final File file = new File(BLOB_NAME_BASE + numElements + ".blob");
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);
        packedVariableArray.write(writer);
        writer.close();

        assertEquals(packedVariableArray.bitSizeOf(), writer.getBitPosition());
        assertEquals(packedVariableArray.initializeOffsets(), writer.getBitPosition());

        final FileBitStreamReader reader = new FileBitStreamReader(file);
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

    private static final String BLOB_NAME_BASE = "packed_variable_array_struct_recursion_";

    private static final int VARIABLE_ARRAY_LENGTH1 = 100;
    private static final int VARIABLE_ARRAY_LENGTH2 = 500;
    private static final int VARIABLE_ARRAY_LENGTH3 = 1000;
}
