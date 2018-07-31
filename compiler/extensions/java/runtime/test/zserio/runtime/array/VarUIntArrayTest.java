package zserio.runtime.array;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarUIntArrayTest
{
    @Test
    public void ctorLength()
    {
        VarUIntArray array = new VarUIntArray(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        VarUIntArray array = new VarUIntArray(LENGTH);

        array.setElementAt(BigInteger.ONE, LENGTH - 1);
        assertEquals(BigInteger.ONE, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        VarUIntArray array = new VarUIntArray(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (BigInteger value : DATA)
        {
            writer.writeVarUInt(value);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final VarUIntArray array = new VarUIntArray(reader, DATA.length);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final VarUIntArray array = new VarUIntArray(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (BigInteger value : DATA)
        {
            assertEquals(value, reader.readVarUInt());
        }
    }

    @Test
    public void arrayCtor()
    {
        final VarUIntArray array = new VarUIntArray(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final VarUIntArray array = new VarUIntArray(DATA, 0, DATA.length);

        int expectedBitsize = 0;
        for (BigInteger value : DATA)
        {
            expectedBitsize += BitSizeOfCalculator.getBitSizeOfVarUInt(value);
        }

        assertEquals(expectedBitsize, array.bitSizeOf(0));
    }

    private static final int LENGTH = 10;

    private static BigInteger DATA[] =
    {
        // 1 byte
        BigInteger.ZERO,
        BigInteger.ONE,
        BigInteger.ONE.shiftLeft(7).subtract(BigInteger.ONE),
        // 2 bytes
        BigInteger.ONE.shiftLeft(14).subtract(BigInteger.ONE),
        // 3 bytes
        BigInteger.ONE.shiftLeft(21).subtract(BigInteger.ONE),
        // 4 bytes
        BigInteger.ONE.shiftLeft(28).subtract(BigInteger.ONE),
        // 5 bytes
        BigInteger.ONE.shiftLeft(35).subtract(BigInteger.ONE),
        // 6 bytes
        BigInteger.ONE.shiftLeft(42).subtract(BigInteger.ONE),
        // 7 bytes
        BigInteger.ONE.shiftLeft(49).subtract(BigInteger.ONE),
        // 8 bytes
        BigInteger.ONE.shiftLeft(56).subtract(BigInteger.ONE),
        // 9 bytes
        BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE)
    };
}
