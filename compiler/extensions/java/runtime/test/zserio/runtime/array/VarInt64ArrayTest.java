package zserio.runtime.array;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarInt64ArrayTest
{
    @Test
    public void ctorLength()
    {
        VarInt64Array array = new VarInt64Array(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        VarInt64Array array = new VarInt64Array(LENGTH);

        array.setElementAt((short)1, LENGTH - 1);
        assertEquals(1, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        VarInt64Array array = new VarInt64Array(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (long value : DATA)
        {
            writer.writeVarInt64(value);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final VarInt64Array array = new VarInt64Array(reader, DATA.length);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final VarInt64Array array = new VarInt64Array(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (long value : DATA)
        {
            assertEquals(value, reader.readVarInt64());
        }
    }

    @Test
    public void arrayCtor()
    {
        final VarInt64Array array = new VarInt64Array(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final VarInt64Array array = new VarInt64Array(DATA, 0, DATA.length);

        int expectedBitsize = 0;
        for (long value : DATA)
        {
            expectedBitsize += BitSizeOfCalculator.getBitSizeOfVarInt64(value);
        }

        assertEquals(expectedBitsize, array.bitSizeOf(0));
    }

    private static final int LENGTH = 10;

    private static long DATA[] =
    {
        // VarInt64 can accommodate 56-bit numbers with sign bit
        0x01L,              // 1 byte-encoded value
        0x40L,              // 2 byte-encoded value
        0x2000L,            // 3 byte-encoded value
        0x100000L,          // 4 byte-encoded value
        0x8000000L,         // 5 byte-encoded value
        0x400000000L,       // 6 byte-encoded value
        0x20000000000L,     // 7 byte-encoded value
        0x1000000000000L,   // 8 byte-encoded value
        +0xffffffffffffffL, // max
        -0xffffffffffffffL  // min
    };
}
