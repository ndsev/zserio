package zserio.runtime.array;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarInt16ArrayTest
{
    @Test
    public void ctorLength()
    {
        VarInt16Array array = new VarInt16Array(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        VarInt16Array array = new VarInt16Array(LENGTH);

        array.setElementAt((short)1, LENGTH - 1);
        assertEquals(1, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        VarInt16Array array = new VarInt16Array(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (short value : DATA)
        {
            writer.writeVarInt16(value);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final VarInt16Array array = new VarInt16Array(reader, DATA.length);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final VarInt16Array array = new VarInt16Array(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (short value : DATA)
        {
            assertEquals(value, reader.readVarInt16());
        }
    }

    @Test
    public void arrayCtor()
    {
        final VarInt16Array array = new VarInt16Array(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final VarInt16Array array = new VarInt16Array(DATA, 0, DATA.length);

        int expectedBitsize = 0;
        for (short value : DATA)
        {
            expectedBitsize += BitSizeOfCalculator.getBitSizeOfVarInt16(value);
        }

        assertEquals(expectedBitsize, array.bitSizeOf(0));
    }

    private static final int LENGTH = 10;

    private static short DATA[] =
    {
        // VarInt16 can accommodate 14-bit numbers with sign bit
        0x01, // 1 byte-encoded value
        0x40, // 2 byte-encoded value
        +0x3fff, // max
        -0x3fff  // min
    };
}
