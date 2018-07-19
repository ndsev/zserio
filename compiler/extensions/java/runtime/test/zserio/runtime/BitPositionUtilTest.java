package zserio.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BitPositionUtilTest
{
    @Test
    public void alignTo()
    {
        final long bitPosition = 5;
        assertEquals(5, BitPositionUtil.alignTo(0, bitPosition));
        assertEquals(5, BitPositionUtil.alignTo(1, bitPosition));
        assertEquals(6, BitPositionUtil.alignTo(2, bitPosition));
        assertEquals(6, BitPositionUtil.alignTo(3, bitPosition));
        assertEquals(8, BitPositionUtil.alignTo(4, bitPosition));
        assertEquals(5, BitPositionUtil.alignTo(5, bitPosition));
        assertEquals(6, BitPositionUtil.alignTo(6, bitPosition));
        assertEquals(7, BitPositionUtil.alignTo(7, bitPosition));
        assertEquals(8, BitPositionUtil.alignTo(8, bitPosition));
    }

    @Test
    public void bitsToBytes()
    {
        assertEquals(1, BitPositionUtil.bitsToBytes(8L));
        assertEquals(3, BitPositionUtil.bitsToBytes(24L));
    }

    @Test(expected = ZserioError.class)
    public void bitsToBytesException1()
    {
        BitPositionUtil.bitsToBytes(4L);
    }

    @Test(expected = ZserioError.class)
    public void bitsToBytesException2()
    {
        BitPositionUtil.bitsToBytes(9L);
    }

    @Test
    public void bytesToBits()
    {
        assertEquals(0, BitPositionUtil.bytesToBits(0L));
        assertEquals(8, BitPositionUtil.bytesToBits(1L));
        assertEquals(16, BitPositionUtil.bytesToBits(2L));
    }
}
