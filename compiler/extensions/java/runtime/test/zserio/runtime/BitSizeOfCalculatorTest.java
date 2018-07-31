package zserio.runtime;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class BitSizeOfCalculatorTest
{
    @Test
    public void getBitSizeOfVarInt16()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt16((short)63));
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt16((short)-63));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt16((short)-16383));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt16((short)16383));
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarInt16Exception()
    {
        BitSizeOfCalculator.getBitSizeOfVarInt16((short) (1 << (6 + 8)));
    }

    @Test
    public void getBitSizeOfVarInt32()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt32((short)0));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt32((short)8191));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarInt32((short)8192));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarInt32((short)1 << 20));
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarInt32Exception()
    {
        BitSizeOfCalculator.getBitSizeOfVarInt32((int) (1 << (6 + 7 + 7 + 8)));
    }

    @Test
    public void getBitSizeOfVarInt64()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 5));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 6));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 13));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 20));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 27));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 34));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 41));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarInt64((1L << 48) - 1));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarInt64(1L << 48));
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarInt64Exception()
    {
        BitSizeOfCalculator.getBitSizeOfVarInt64(1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8));
    }

    @Test
    public void getBitSizeOfVarUInt16()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarUInt16((short)0));
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarUInt16((short)64));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarUInt16((short)128));
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarUInt16Exception()
    {
        BitSizeOfCalculator.getBitSizeOfVarUInt16((short)-1);
    }

    @Test
    public void getBitSizeOfVarUInt32()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarUInt32(64));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarUInt32(1 << 7));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarUInt32(1 << 14));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarUInt32(1 << 21));
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarUInt32Exception1()
    {
        BitSizeOfCalculator.getBitSizeOfVarUInt32(-1);
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarUInt32Exception2()
    {
        BitSizeOfCalculator.getBitSizeOfVarUInt32(1 << (7 + 7 + 7 + 8));
    }

    @Test
    public void getBitSizeOfVarUInt64()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 6));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 7));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 14));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 21));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 28));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 35));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 42));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarUInt64((1L << 49) - 1));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << 49));
    }

    @Test
    public void getBitSizeOfVarInt()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt(0));
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 6) + 1));
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 6) - 1));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 6)));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 6)));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 13) + 1));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 13) - 1));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 13)));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 13)));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 20) + 1));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 20) - 1));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 20)));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 20)));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 27) + 1));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 27) - 1));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 27)));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 27)));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 34) + 1));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 34) - 1));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 34)));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 34)));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 41) + 1));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 41) - 1));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 41)));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 41)));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 48) + 1));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 48) - 1));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 48)));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 48)));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 55) + 1));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 55) - 1));
        assertEquals(72, BitSizeOfCalculator.getBitSizeOfVarInt(-(1L << 55)));
        assertEquals(72, BitSizeOfCalculator.getBitSizeOfVarInt((1L << 55)));
        assertEquals(72, BitSizeOfCalculator.getBitSizeOfVarInt(Long.MIN_VALUE + 1));
        assertEquals(72, BitSizeOfCalculator.getBitSizeOfVarInt(Long.MAX_VALUE - 1));

        // special case, Long.MIN_VALUE is stored as -0
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarInt(Long.MIN_VALUE));
    }

    @Test
    public void getBitSizeOfVarUInt()
    {
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ZERO));
        assertEquals(8, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(7).subtract(BigInteger.ONE)));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(7)));
        assertEquals(16, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(14).subtract(BigInteger.ONE)));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(14)));
        assertEquals(24, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(21).subtract(BigInteger.ONE)));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(21)));
        assertEquals(32, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(28).subtract(BigInteger.ONE)));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(28)));
        assertEquals(40, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(35).subtract(BigInteger.ONE)));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(35)));
        assertEquals(48, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(42).subtract(BigInteger.ONE)));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(42)));
        assertEquals(56, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(49).subtract(BigInteger.ONE)));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(49)));
        assertEquals(64, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(56).subtract(BigInteger.ONE)));
        assertEquals(72, BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(56)));
        assertEquals(72, BitSizeOfCalculator.getBitSizeOfVarUInt(
                BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE)));
    }

    @Test(expected = ZserioError.class)
    public void getBitSizeOfVarUIntNegativeThrows()
    {
        BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.negate());
    }

    @Test(expected = ZserioError.class)
    public void getBitSizeOfVarUIntTooBigThrows()
    {
        BitSizeOfCalculator.getBitSizeOfVarUInt(BigInteger.ONE.shiftLeft(64));
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarUInt64Exception1()
    {
        BitSizeOfCalculator.getBitSizeOfVarUInt64(-1L);
    }

    @Test(expected=ZserioError.class)
    public void getBitSizeOfVarUInt64Exception2()
    {
        BitSizeOfCalculator.getBitSizeOfVarUInt64(1L << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8));
    }

    @Test
    public void getBitSizeOfString()
    {
        assertEquals((1 + 1) * 8, BitSizeOfCalculator.getBitSizeOfString("T"));
        assertEquals((1 + 4) * 8, BitSizeOfCalculator.getBitSizeOfString("Test"));

        final int testStringLength = 1 << 7;
        final String testString = new String(new char[testStringLength]);
        assertEquals((2 + testStringLength) * 8, BitSizeOfCalculator.getBitSizeOfString(testString));
    }
}
