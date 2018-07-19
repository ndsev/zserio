package zserio.runtime.array;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarUInt64ArrayTest
{
    @Test
    public void ctorLength()
    {
        VarUInt64Array array = new VarUInt64Array(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        VarUInt64Array array = new VarUInt64Array(LENGTH);

        array.setElementAt((short)1, LENGTH - 1);
        assertEquals(1, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        VarUInt64Array array = new VarUInt64Array(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (long value : DATA)
        {
            writer.writeVarUInt64(value);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final VarUInt64Array array = new VarUInt64Array(reader, DATA.length);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final VarUInt64Array array = new VarUInt64Array(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (long value : DATA)
        {
            assertEquals(value, reader.readVarUInt64());
        }
    }

    @Test
    public void arrayCtor()
    {
        final VarUInt64Array array = new VarUInt64Array(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final VarUInt64Array array = new VarUInt64Array(DATA, 0, DATA.length);

        int expectedBitsize = 0;
        for (long value : DATA)
        {
            expectedBitsize += BitSizeOfCalculator.getBitSizeOfVarUInt64(value);
        }

        assertEquals(expectedBitsize, array.bitSizeOf(0));
    }

    private static final int LENGTH = 10;

    private static long DATA[] =
    {
        // VarUInt64 can accommodate 57-bit unsigned numbers
        0x01L,                  // 1 byte-encoded value
        0x80L,                  // 2 byte-encoded value
        0x4000L,                // 3 byte-encoded value
        0x200000L,              // 4 byte-encoded value
        0x10000000L,            // 5 byte-encoded value
        0x800000000L,           // 6 byte-encoded value
        0x40000000000L,         // 7 byte-encoded value
        0x2000000000000L,       // 8 byte-encoded value
        +0x1ffffffffffffffL,    // max
        0L                      // min
    };
}
