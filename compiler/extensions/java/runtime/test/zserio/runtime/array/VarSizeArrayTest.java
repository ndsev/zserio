package zserio.runtime.array;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarSizeArrayTest
{
    @Test
    public void ctorLength()
    {
        VarSizeArray array = new VarSizeArray(LENGTH);

        assertEquals(LENGTH, array.length());
    }

    @Test
    public void ctorLengthAccess()
    {
        VarSizeArray array = new VarSizeArray(LENGTH);

        array.setElementAt((short)1, LENGTH - 1);
        assertEquals(1, array.elementAt(LENGTH - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ctorLengthAccessOutOfBound()
    {
        VarSizeArray array = new VarSizeArray(LENGTH);

        array.elementAt(LENGTH);
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (int value : DATA)
        {
            writer.writeVarSize(value);
        }

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        final VarSizeArray array = new VarSizeArray(reader, DATA.length);

        assertEquals(array.length(), DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void write() throws IOException
    {
        final VarSizeArray array = new VarSizeArray(DATA.length);
        for (int i = 0; i < DATA.length; i++)
        {
            array.setElementAt(DATA[i], i);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        for (int value : DATA)
        {
            assertEquals(value, reader.readVarSize());
        }
    }

    @Test
    public void arrayCtor()
    {
        final VarSizeArray array = new VarSizeArray(DATA, 0, DATA.length);

        assertEquals(DATA.length, array.length());

        for (int i = 0; i < DATA.length; i++)
        {
            assertEquals(DATA[i], array.elementAt(i));
        }
    }

    @Test
    public void bitSizeOf()
    {
        final VarSizeArray array = new VarSizeArray(DATA, 0, DATA.length);

        int expectedBitsize = 0;
        for (int value : DATA)
        {
            expectedBitsize += BitSizeOfCalculator.getBitSizeOfVarSize(value);
        }

        assertEquals(expectedBitsize, array.bitSizeOf(0));
    }

    private static final int LENGTH = 10;

    private static int DATA[] =
    {
        // VarSize can accommodate 31-bit unsigned number
        0x01,       // 1 byte-encoded value
        0x80,       // 2 byte-encoded value
        0x4000,     // 3 byte-encoded value
        0x200000,   // 4 byte-encoded value
        0x10000000, // 5 byte-encoded value
        0x7FFFFFFF, // max
        0x00   		// min
    };
}
