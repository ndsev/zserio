package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class BigIntegerArrayTest
{
    @Test
    public void ctorLength()
    {
        BigIntegerArray array = new BigIntegerArray(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        BigIntegerArray array = new BigIntegerArray(LENGTH);

        array.setElementAt(BigInteger.ONE, LENGTH - 1);
        assertEquals(BigInteger.ONE, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        BigIntegerArray array = new BigIntegerArray(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (BigInteger value : DATA)
        {
            writer.writeBigInteger(value, NUM_BITS);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final BigIntegerArray array = new BigIntegerArray(reader, DATA.length, NUM_BITS);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final BigIntegerArray array = new BigIntegerArray(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer, NUM_BITS);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], reader.readBigInteger(NUM_BITS));
        }
    }

    @Test
    public void arrayCtor()
    {
        final BigIntegerArray array = new BigIntegerArray(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final BigIntegerArray array = new BigIntegerArray(DATA, 0, DATA.length);

        int expectedBitsize = DATA.length * NUM_BITS;

        assertEquals(expectedBitsize, array.bitSizeOf(0, NUM_BITS));
    }

    @Test
    public void hashCodeMethod()
    {
        BigIntegerArray array1;
        BigIntegerArray array2;

        // empty arrays
        array1 = new BigIntegerArray(0);
        array2 = new BigIntegerArray(0);
        assertEquals(array1.hashCode(), array2.hashCode());

        // null elements, same length
        array1 = new BigIntegerArray(LENGTH);
        array2 = new BigIntegerArray(LENGTH);
        assertEquals(array1.hashCode(), array2.hashCode());

        // null elements, different length
        array1 = new BigIntegerArray(LENGTH);
        array2 = new BigIntegerArray(LENGTH+1);
        assertFalse(array1.hashCode() == array2.hashCode());

        // same arrays
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length);
        assertEquals(array1.hashCode(), array2.hashCode());

        // different last element
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length);
        array2.setElementAt(BigInteger.ONE, DATA.length - 1);
        assertFalse(array1.hashCode() == array2.hashCode());

        // different length
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length - 1);
        assertFalse(array1.hashCode() == array2.hashCode());

        // same length, last element null
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length);
        array2.setElementAt(null, DATA.length - 1);
        assertFalse(array1.hashCode() == array2.hashCode());
    }

    @Test
    public void equalsMethod()
    {
        BigIntegerArray array1;
        BigIntegerArray array2;

        // empty arrays
        array1 = new BigIntegerArray(0);
        array2 = new BigIntegerArray(0);
        assertTrue(array1.equals(array2));
        assertEquals(array1, array2);

        // null elements, same length
        array1 = new BigIntegerArray(LENGTH);
        array2 = new BigIntegerArray(LENGTH);
        assertTrue(array1.equals(array2));
        assertEquals(array1, array2);

        // null elements, different length
        array1 = new BigIntegerArray(LENGTH);
        array2 = new BigIntegerArray(LENGTH+1);
        assertFalse(array1.equals(array2));

        // same arrays
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length);
        assertTrue(array1.equals(array2));
        assertEquals(array1, array2);

        // different last element
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length);
        array2.setElementAt(BigInteger.ONE, DATA.length - 1);
        assertFalse(array1.equals(array2));

        // different length
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length - 1);
        assertFalse(array1.equals(array2));

        // same length, last element null
        array1 = new BigIntegerArray(DATA, 0, DATA.length);
        array2 = new BigIntegerArray(DATA, 0, DATA.length);
        array2.setElementAt(null, DATA.length - 1);
        assertFalse(array1.equals(array2));
    }

    private static final int NUM_BITS = Long.SIZE;
    private static final int LENGTH = 10;

    private static BigInteger DATA[] =
    {
        BigInteger.ZERO,
        BigInteger.ONE,
        BigInteger.TEN,
        new BigInteger("1111111111111111", 16),
        new BigInteger("5555555555555555", 16),
        new BigInteger("aaaaaaaaaaaaaaaa", 16),
        new BigInteger("ffffffffffffffff", 16),
    };
}
