package optional_members;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

import optional_members.optional_recursion.Block;

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
    public void isNextDataSetAndUsed()
    {
        final Block block1 = createBlock(BLOCK1_DATA);
        assertFalse(block1.isNextDataSet());
        assertFalse(block1.isNextDataUsed());

        block1.setBlockTerminator((short)1); // used but not set
        assertFalse(block1.isNextDataSet());
        assertTrue(block1.isNextDataUsed());

        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertTrue(block12.isNextDataSet());
        assertTrue(block12.isNextDataUsed());

        block12.setBlockTerminator((short)0); // set but not used
        assertTrue(block12.isNextDataSet());
        assertFalse(block12.isNextDataUsed());
    }

    @Test
    public void resetNextData()
    {
        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertTrue(block12.isNextDataSet());
        assertTrue(block12.isNextDataUsed());

        block12.resetNextData(); // used but not set
        assertFalse(block12.isNextDataSet());
        assertTrue(block12.isNextDataUsed());
        assertEquals(null, block12.getNextData());
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

        final Block block12_1 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertTrue(block12_1.hashCode() != block1.hashCode());

        final Block block12_2 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        assertEquals(block12_1.hashCode(), block12_2.hashCode());

        block12_1.setBlockTerminator((short)0);
        assertNotEquals(block12_1.hashCode(), block12_2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(6240113, block12_1.hashCode());
        assertEquals(1846174533, block12_2.hashCode());
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
        final BitBuffer block1BitBuffer = SerializeUtil.serialize(block1);
        final BitStreamReader reader = new ByteArrayBitStreamReader(block1BitBuffer);
        checkBlockInStream(reader, BLOCK1_DATA);

        final Block readBlock1 =
                SerializeUtil.deserialize(Block.class, block1BitBuffer, (short)BLOCK1_DATA.length);
        assertEquals(block1, readBlock1);
    }

    @Test
    public void fileWriteBlock12() throws IOException
    {
        final Block block12 = createBlock(BLOCK1_DATA, BLOCK2_DATA);
        final BitBuffer block12BitBuffer = SerializeUtil.serialize(block12);
        final BitStreamReader reader = new ByteArrayBitStreamReader(block12BitBuffer);
        checkBlockInStream(reader, BLOCK1_DATA, BLOCK2_DATA);

        final Block readBlock12 =
                SerializeUtil.deserialize(Block.class, block12BitBuffer, (short)BLOCK1_DATA.length);
        assertEquals(block12, readBlock12);
    }

    private static Block createBlock(short[] blockData)
    {
        return new Block((short)blockData.length, blockData, (short)0, null);
    }

    private static Block createBlock(short[] block1Data, short[] block2Data)
    {
        final Block block2 = createBlock(block2Data);

        return new Block((short)block1Data.length, block1Data, (short)block2Data.length, block2);
    }

    private static int getBlockBitSize(short[] blockData)
    {
        return 8 * blockData.length + 8;
    }

    private static int getBlockBitSize(short[] block1Data, short[] block2Data)
    {
        return getBlockBitSize(block1Data) + getBlockBitSize(block2Data);
    }

    private static void checkBlockInStream(BitStreamReader reader, short[] blockData) throws IOException
    {
        for (short element : blockData)
            assertEquals(element, reader.readBits(8));
        assertEquals(0, reader.readBits(8));
    }

    private static void checkBlockInStream(BitStreamReader reader, short[] block1Data, short[] block2Data)
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
