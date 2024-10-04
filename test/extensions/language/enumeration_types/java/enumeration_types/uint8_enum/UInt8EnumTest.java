package enumeration_types.uint8_enum;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.HashCodeUtil;
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
    public void calcHashCode()
    {
        // use hardcoded values to check that the hash code is stable
        assertEquals(1702, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.NONE));
        assertEquals(1703, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.DARK_RED));
        assertEquals(1704, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.DARK_BLUE));
        assertEquals(1709, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.DARK_GREEN));
    }

    @Test
    public void bitSizeOf()
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        assertEquals(UINT8_ENUM_BITSIZEOF, darkColor.bitSizeOf());
    }

    @Test
    public void writeRead() throws IOException
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        darkColor.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final short readColor = (short)reader.readBits(UINT8_ENUM_BITSIZEOF);
        assertEquals(readColor, darkColor.getValue());
    }

    @Test
    public void valueToEnum()
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
    public void stringToEnum()
    {
        DarkColor darkColor = DarkColor.toEnum("NONE");
        assertEquals(DarkColor.NONE, darkColor);

        darkColor = DarkColor.toEnum("DARK_RED");
        assertEquals(DarkColor.DARK_RED, darkColor);

        darkColor = DarkColor.toEnum("DARK_BLUE");
        assertEquals(DarkColor.DARK_BLUE, darkColor);

        darkColor = DarkColor.toEnum("DARK_GREEN");
        assertEquals(DarkColor.DARK_GREEN, darkColor);
    }

    @Test
    public void valueToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> DarkColor.toEnum((short)3));
    }

    @Test
    public void stringToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> DarkColor.toEnum("NONEXISTING"));
    }

    private static int UINT8_ENUM_BITSIZEOF = 8;

    private static short NONE_VALUE = (short)0;
    private static short DARK_RED_VALUE = (short)1;
    private static short DARK_BLUE_VALUE = (short)2;
    private static short DARK_GREEN_VALUE = (short)7;
}
