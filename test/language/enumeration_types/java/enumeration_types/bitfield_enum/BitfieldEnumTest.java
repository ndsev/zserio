package enumeration_types.bitfield_enum;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class BitfieldEnumTest
{
    @Test
    public void constructor()
    {
        final Color color = Color.RED;
        assertEquals(Color.RED, color);
    }

    @Test
    public void getValue()
    {
        final Color color = Color.BLUE;
        assertEquals(3, color.getValue());
    }

    @Test
    public void getGenericValue()
    {
        final Color color = Color.BLACK;
        assertEquals(Byte.valueOf((byte)7) , color.getGenericValue());
    }

    @Test
    public void bitSizeOf()
    {
        final Color color = Color.NONE;
        assertEquals(BITFIELD_ENUM_BITSIZEOF, color.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        final Color color = Color.BLACK;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        color.write(writer);
        final BitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final byte readColor = (byte)reader.readBits(BITFIELD_ENUM_BITSIZEOF);
        assertEquals(readColor, color.getValue());
    }

    @Test
    public void toEnum()
    {
        Color color = Color.toEnum((byte)0);
        assertEquals(Color.NONE, color);

        color = Color.toEnum((byte)2);
        assertEquals(Color.RED, color);

        color = Color.toEnum((byte)3);
        assertEquals(Color.BLUE, color);

        color = Color.toEnum((byte)7);
        assertEquals(Color.BLACK, color);
    }

    @Test(expected=IllegalArgumentException.class)
    public void toEnumFailure()
    {
        Color.toEnum((byte)1);
    }

    private static int BITFIELD_ENUM_BITSIZEOF = 3;
}
