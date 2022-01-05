package enumeration_types.uint8_enum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        assertEquals(DARK_BLUE_VALUE, darkColor.getValue());
    }

    @Test
    public void getGenericValue()
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        assertEquals(Short.valueOf(DARK_GREEN_VALUE), darkColor.getGenericValue());
    }

    @Test
    public void bitSizeOf()
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        assertEquals(UINT8_ENUM_BITSIZEOF, darkColor.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        darkColor.write(writer);
        final BitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final short readColor = (short)reader.readBits(UINT8_ENUM_BITSIZEOF);
        assertEquals(readColor, darkColor.getValue());
    }

    @Test
    public void toEnum()
    {
        DarkColor darkColor = DarkColor.toEnum(NONE_VALUE);
        assertEquals(DarkColor.NONE, darkColor);

        darkColor = DarkColor.toEnum(DARK_RED_VALUE);
        assertEquals(DarkColor.DARK_RED, darkColor);

        darkColor = DarkColor.toEnum(DARK_BLUE_VALUE);
        assertEquals(DarkColor.DARK_BLUE, darkColor);

        darkColor = DarkColor.toEnum(DARK_GREEN_VALUE);
        assertEquals(DarkColor.DARK_GREEN, darkColor);
    }

    @Test
    public void toEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> DarkColor.toEnum((short)3));
    }

    private static int UINT8_ENUM_BITSIZEOF = 8;

    private static short NONE_VALUE = (short)0;
    private static short DARK_RED_VALUE = (short)1;
    private static short DARK_BLUE_VALUE = (short)2;
    private static short DARK_GREEN_VALUE = (short)7;
}
