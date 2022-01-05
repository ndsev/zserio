package expressions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.lang.Math;
import java.math.BigInteger;

import expressions.numbits_operator.NumBitsFunctions;

public class NumBitsOperatorTest
{
    @Test
    public void getNumBits8()
    {
        final NumBitsFunctions numBitsFunctions = new NumBitsFunctions();
        for (short value8 = 1; value8 <= 255; ++value8)
        {
            numBitsFunctions.setValue8(value8);
            assertEquals(calculateExpectedNumBits(value8), numBitsFunctions.funcGetNumBits8());
        }
    }

    @Test
    public void getNumBits16()
    {
        final NumBitsFunctions numBitsFunctions = new NumBitsFunctions();
        for (int value16 = 1; value16 <= 65535; ++value16)
        {
            numBitsFunctions.setValue16(value16);
            assertEquals(calculateExpectedNumBits(value16), numBitsFunctions.funcGetNumBits16());
        }
    }

    @Test
    public void getNumBits32()
    {
        final NumBitsFunctions numBitsFunctions = new NumBitsFunctions();
        for (long value32 = 1; value32 <= 4294967295L; value32 <<= 1)
        {
            numBitsFunctions.setValue32(value32);
            assertEquals(calculateExpectedNumBits(value32), numBitsFunctions.funcGetNumBits32());
        }
    }

    @Test
    public void getNumBits64()
    {
        final NumBitsFunctions numBitsFunctions = new NumBitsFunctions();
        for (int power = 1; power <= 48; ++power)
        {
            // value64 = 2**power - 1
            final BigInteger value64 = BigInteger.valueOf(2).pow(power).subtract(BigInteger.ONE);
            numBitsFunctions.setValue64(value64);
            assertEquals(calculateExpectedNumBits(value64.longValue()), numBitsFunctions.funcGetNumBits64());
        }
    }

    private short calculateExpectedNumBits(long value)
    {
        if (value <= 0)
            return (short)0;
        if (value == 1)
            return (short)1;

        return (short)(Math.floor(Math.log((double)(value - 1)) / Math.log(2.0)) + 1);
    }
}
