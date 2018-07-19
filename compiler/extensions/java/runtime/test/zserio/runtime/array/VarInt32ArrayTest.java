package zserio.runtime.array;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarInt32ArrayTest
{
    @Test
    public void ctorLength()
    {
        VarInt32Array array = new VarInt32Array(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        VarInt32Array array = new VarInt32Array(LENGTH);

        array.setElementAt((short)1, LENGTH - 1);
        assertEquals(1, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        VarInt32Array array = new VarInt32Array(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (int value : DATA)
        {
            writer.writeVarInt32(value);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final VarInt32Array array = new VarInt32Array(reader, DATA.length);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final VarInt32Array array = new VarInt32Array(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (int value : DATA)
        {
            assertEquals(value, reader.readVarInt32());
        }
    }

    @Test
    public void arrayCtor()
    {
        final VarInt32Array array = new VarInt32Array(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final VarInt32Array array = new VarInt32Array(DATA, 0, DATA.length);

        int expectedBitsize = 0;
        for (int value : DATA)
        {
            expectedBitsize += BitSizeOfCalculator.getBitSizeOfVarInt32(value);
        }

        assertEquals(expectedBitsize, array.bitSizeOf(0));
    }

    private static final int LENGTH = 10;

    private static int DATA[] =
    {
        // VarInt32 can accommodate 28-bit numbers with sign bit
        0x01,       // 1 byte-encoded value
        0x40,       // 2 byte-encoded value
        0x2000,     // 3 byte-encoded value
        0x100000,   // 4 byte-encoded value
        +0xfffffff, // max
        -0xfffffff  // min
    };
}
