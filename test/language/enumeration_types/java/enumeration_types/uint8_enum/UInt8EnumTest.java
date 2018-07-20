package enumeration_types.uint8_enum;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UInt8EnumTest
{
    @Test
    public void constructor()
    {
        final DarkColor darkColor = DarkColor.DARK_RED;
        assertEquals(DarkColor.DARK_RED, darkColor);
    }

    @Test
    public void getValue()
    {
        final DarkColor darkColor = DarkColor.DARK_BLUE;
        assertEquals(2, darkColor.getValue());
    }

    @Test
    public void getGenericValue()
    {
        final DarkColor darkColor = DarkColor.DARK_BLACK;
        assertEquals(Short.valueOf((short)7) , darkColor.getGenericValue());
    }

    @Test
    public void bitSizeOf()
    {
        final DarkColor darkColor = DarkColor.DARK_BLACK;
        assertEquals(UINT8_ENUM_BITSIZEOF, darkColor.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        final DarkColor darkColor = DarkColor.DARK_BLACK;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        darkColor.write(writer);
        final BitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final short readColor = (short)reader.readBits(UINT8_ENUM_BITSIZEOF);
        assertEquals(readColor, darkColor.getValue());
    }

    @Test
    public void toEnum()
    {
        DarkColor darkColor = DarkColor.toEnum((short)0);
        assertEquals(DarkColor.NONE, darkColor);

        darkColor = DarkColor.toEnum((short)1);
        assertEquals(DarkColor.DARK_RED, darkColor);

        darkColor = DarkColor.toEnum((short)2);
        assertEquals(DarkColor.DARK_BLUE, darkColor);

        darkColor = DarkColor.toEnum((short)7);
        assertEquals(DarkColor.DARK_BLACK, darkColor);
    }

    @Test(expected=IllegalArgumentException.class)
    public void toEnumFailure()
    {
        DarkColor.toEnum((short)3);
    }

    private static int UINT8_ENUM_BITSIZEOF = 8;
}
