package enumeration_types.varuint_enum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarUIntEnumTest
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
        assertEquals(DARK_GREEN_VALUE, darkColor.getGenericValue());
    }

    @Test
    public void bitSizeOf()
    {
        final DarkColor darkColor1 = DarkColor.NONE;
        assertEquals(DARK_COLOR_NONE_BITSIZEOF, darkColor1.bitSizeOf());

        final DarkColor darkColor2 = DarkColor.DARK_RED;
        assertEquals(DARK_COLOR_DARK_RED_BITSIZEOF, darkColor2.bitSizeOf());

        final DarkColor darkColor3 = DarkColor.DARK_BLUE;
        assertEquals(DARK_COLOR_DARK_BLUE_BITSIZEOF, darkColor3.bitSizeOf());

        final DarkColor darkColor4 = DarkColor.DARK_GREEN;
        assertEquals(DARK_COLOR_DARK_GREEN_BITSIZEOF, darkColor4.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        darkColor.write(writer);
        final BitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final BigInteger readColor = reader.readVarUInt();
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
        assertThrows(IllegalArgumentException.class, () -> DarkColor.toEnum(new BigInteger("3")));
    }

    private static int DARK_COLOR_NONE_BITSIZEOF = 8;
    private static int DARK_COLOR_DARK_RED_BITSIZEOF = 8;
    private static int DARK_COLOR_DARK_BLUE_BITSIZEOF = 8;
    private static int DARK_COLOR_DARK_GREEN_BITSIZEOF = 16;

    private static BigInteger NONE_VALUE = BigInteger.valueOf(0);
    private static BigInteger DARK_RED_VALUE = BigInteger.valueOf(1);
    private static BigInteger DARK_BLUE_VALUE = BigInteger.valueOf(2);
    private static BigInteger DARK_GREEN_VALUE = BigInteger.valueOf(255);
}
