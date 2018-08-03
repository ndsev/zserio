package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarIntArrayTest
{
    @Test
    public void ctorLength()
    {
        VarIntArray array = new VarIntArray(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        VarIntArray array = new VarIntArray(LENGTH);

        array.setElementAt(1, LENGTH - 1);
        assertEquals(1, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        VarIntArray array = new VarIntArray(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (long value : DATA)
        {
            writer.writeVarInt(value);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final VarIntArray array = new VarIntArray(reader, DATA.length);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final VarIntArray array = new VarIntArray(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (long value : DATA)
        {
            assertEquals(value, reader.readVarInt());
        }
    }

    @Test
    public void arrayCtor()
    {
        final VarIntArray array = new VarIntArray(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final VarIntArray array = new VarIntArray(DATA, 0, DATA.length);

        int expectedBitsize = 0;
        for (long value : DATA)
        {
            expectedBitsize += BitSizeOfCalculator.getBitSizeOfVarInt(value);
        }

        assertEquals(expectedBitsize, array.bitSizeOf(0));
    }

    @Test
    public void hashCodeMethod()
    {
        VarIntArray array1;
        VarIntArray array2;

        // empty arrays
        array1 = new VarIntArray(0);
        array2 = new VarIntArray(0);
        assertEquals(array1.hashCode(), array2.hashCode());

        // null elements, same length
        array1 = new VarIntArray(LENGTH);
        array2 = new VarIntArray(LENGTH);
        assertEquals(array1.hashCode(), array2.hashCode());

        // null elements, different length
        array1 = new VarIntArray(LENGTH);
        array2 = new VarIntArray(LENGTH+1);
        assertNotSame(array1.hashCode(), array2.hashCode());

        // same arrays
        array1 = new VarIntArray(DATA, 0, DATA.length);
        array2 = new VarIntArray(DATA, 0, DATA.length);
        assertEquals(array1.hashCode(), array2.hashCode());

        // different last element
        array1 = new VarIntArray(DATA, 0, DATA.length);
        array2 = new VarIntArray(DATA, 0, DATA.length);
        array2.setElementAt(1, DATA.length - 1);
        assertNotSame(array1.hashCode(), array2.hashCode());

        // different length
        array1 = new VarIntArray(DATA, 0, DATA.length);
        array2 = new VarIntArray(DATA, 0, DATA.length - 1);
        assertNotSame(array1.hashCode(), array2.hashCode());
    }

    @Test
    public void equalsMethod()
    {
        VarIntArray array1;
        VarIntArray array2;

        // empty arrays
        array1 = new VarIntArray(0);
        array2 = new VarIntArray(0);
        assertTrue(array1.equals(array2));
        assertEquals(array1, array2);

        // null elements, same length
        array1 = new VarIntArray(LENGTH);
        array2 = new VarIntArray(LENGTH);
        assertTrue(array1.equals(array2));
        assertEquals(array1, array2);

        // null elements, different length
        array1 = new VarIntArray(LENGTH);
        array2 = new VarIntArray(LENGTH+1);
        assertFalse(array1.equals(array2));
        assertNotSame(array1, array2);

        // same arrays
        array1 = new VarIntArray(DATA, 0, DATA.length);
        array2 = new VarIntArray(DATA, 0, DATA.length);
        assertTrue(array1.equals(array2));
        assertEquals(array1, array2);

        // different last element
        array1 = new VarIntArray(DATA, 0, DATA.length);
        array2 = new VarIntArray(DATA, 0, DATA.length);
        array2.setElementAt(1, DATA.length - 1);
        assertFalse(array1.equals(array2));
        assertNotSame(array1, array2);

        // different length
        array1 = new VarIntArray(DATA, 0, DATA.length);
        array2 = new VarIntArray(DATA, 0, DATA.length - 1);
        assertFalse(array1.equals(array2));
        assertNotSame(array1, array2);
    }

    private static final int LENGTH = 10;

    private static long DATA[] =
    {
        // 1 byte
        0,
        -1,
        1,
        -(1L << 6) + 1,
        (1L << 6) - 1,
        // 2 bytes
        -(1L << 13) + 1,
        (1L << 13) - 1,
        // 3 bytes
        -(1L << 20) + 1,
        (1L << 20) - 1,
        // 4 bytes
        -(1L << 27) + 1,
        (1L << 27) - 1,
        // 5 bytes
        -(1L << 34) + 1,
        (1L << 34) - 1,
        // 6 bytes
        -(1L << 41) + 1,
        (1L << 41) - 1,
        // 7 bytes
        -(1L << 48) + 1,
        (1L << 48) - 1,
        // 8 bytes
        -(1L << 55) + 1,
        (1L << 55) - 1,
        // 9 bytes
        Long.MIN_VALUE + 1,
        Long.MAX_VALUE,
        // 1 byte - special case, Long.MIN_VALUE stored as -0
        Long.MIN_VALUE
    };
}
