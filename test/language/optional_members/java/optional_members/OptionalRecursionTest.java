package optional_members;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import optional_members.optional_recursion.Block;

import zserio.runtime.array.UnsignedByteArray;
import zserio.runtime.io.FileBitStreamReader;

public class OptionalRecursionTest
{
    @Test
    public void bitSizeOf()
    {
        final Block block1 = createBlock(BLOCK1_DATA);
        assertEquals(getBlockBitSize(BLOCK1_DATA), block1.bitSizeOf());

        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertEquals(getBlockBitSize(BLOCK1_DATA, BLOCK2_DATA), block12.bitSizeOf());
    }

    @Test
    public void hasNextData()
    {
        final Block block1 = createBlock(BLOCK1_DATA);
        assertFalse(block1.hasNextData());

        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertTrue(block12.hasNextData());
    }

    @Test
    public void equals()
    {
        final Block emptyBlock1 = new Block((short)0);
        final Block emptyBlock2 = new Block((short)0);
        assertTrue(emptyBlock1.equals(emptyBlock2));

        final Block block1 = createBlock(BLOCK1_DATA);
        assertFalse(block1.equals(emptyBlock1));

        final Block block2 = createBlock(BLOCK1_DATA);
        assertTrue(block2.equals(block1));

        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertFalse(block12.equals(block1));
    }

    @Test
    public void hashCodeMethod()
    {
        final Block emptyBlock1 = new Block((short)0);
        final Block emptyBlock2 = new Block((short)0);
        assertEquals(emptyBlock1.hashCode(), emptyBlock2.hashCode());

        final Block block1 = createBlock(BLOCK1_DATA);
        assertTrue(block1.hashCode() != emptyBlock1.hashCode());

        final Block block2 = createBlock(BLOCK1_DATA);
        assertEquals(block2.hashCode(), block1.hashCode());

        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertTrue(block12.hashCode() != block1.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final Block block1 = createBlock(BLOCK1_DATA);
        final int bitPosition = 1;
        assertEquals(bitPosition + getBlockBitSize(BLOCK1_DATA), block1.initializeOffsets(bitPosition));

        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertEquals(bitPosition + getBlockBitSize(BLOCK1_DATA, BLOCK2_DATA),
                block12.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWriteBlock1() throws IOException
    {
        final Block block1 = createBlock(BLOCK1_DATA);
        final File block1File = new File("block1.bin");
        block1.write(block1File);
        final FileBitStreamReader reader = new FileBitStreamReader(block1File);
        checkBlockInStream(reader, BLOCK1_DATA);
        reader.close();

        final Block readBlock1 = new Block(block1File, (short)BLOCK1_DATA.length);
        assertEquals(block1, readBlock1);
    }

    @Test
    public void fileWriteBlock12() throws IOException
    {
        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        final File block12File = new File("block12.bin");
        block12.write(block12File);
        final FileBitStreamReader reader = new FileBitStreamReader(block12File);
        checkBlockInStream(reader, BLOCK1_DATA, BLOCK2_DATA);
        reader.close();

        final Block readBlock12 = new Block(block12File, (short)BLOCK1_DATA.length);
        assertEquals(block12, readBlock12);
    }

    private static Block createBlock(short[] blockData)
    {
        final UnsignedByteArray blockDataArray = new UnsignedByteArray(blockData, 0, blockData.length); 

        return new Block((short)blockData.length, blockDataArray, (short)0, null);
    }

    private static Block createBlock(short[] block1Data, short[] block2Data)
    {
        final Block block2 = createBlock(block2Data);
        final UnsignedByteArray block1DataArray = new UnsignedByteArray(block1Data, 0, block1Data.length);

        return new Block((short)block1Data.length, block1DataArray, (short)block2Data.length, block2);
    }

    private static int getBlockBitSize(short[] blockData)
    {
        return 8 * blockData.length + 8;
    }

    private static int getBlockBitSize(short[] block1Data, short[] block2Data)
    {
        return getBlockBitSize(block1Data) + getBlockBitSize(block2Data);
    }

    private static void checkBlockInStream(FileBitStreamReader reader, short[] blockData) throws IOException
    {
        for (short element : blockData)
            assertEquals(element, reader.readBits(8));
        assertEquals(0, reader.readBits(8));
    }

    private static void checkBlockInStream(FileBitStreamReader reader, short[] block1Data, short[] block2Data)
            throws IOException
    {
        for (short element : block1Data)
            assertEquals(element, reader.readBits(8));
        assertEquals(block2Data.length, reader.readBits(8));

        checkBlockInStream(reader, block2Data);
    }

    private static short[] BLOCK1_DATA = {1, 2, 3, 4, 5, 6};
    private static short[] BLOCK2_DATA = {10, 9, 8, 7};
}
