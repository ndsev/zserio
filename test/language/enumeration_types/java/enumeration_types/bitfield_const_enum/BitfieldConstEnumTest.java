package enumeration_types.bitfield_const_enum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.HashCodeUtil;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class BitfieldConstEnumTest
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
        assertEquals(BLUE_VALUE, color.getValue());
    }

    @Test
    public void getGenericValue()
    {
        final Color color = Color.GREEN;
        assertEquals(Byte.valueOf(GREEN_VALUE), color.getGenericValue());
    }

    @Test
    public void calcHashCode()
    {
        // use hardcoded values to check that the hash code is stable
        assertEquals(1702, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Color.NONE));
        assertEquals(1704, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Color.RED));
        assertEquals(1705, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Color.BLUE));
        assertEquals(1709, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Color.GREEN));
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
        final Color color = Color.GREEN;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        color.write(writer);
        final BitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final byte readColor = (byte)reader.readBits(BITFIELD_ENUM_BITSIZEOF);
        assertEquals(readColor, color.getValue());
    }

    @Test
    public void toEnum()
    {
        Color color = Color.toEnum(NONE_VALUE);
        assertEquals(Color.NONE, color);

        color = Color.toEnum(RED_VALUE);
        assertEquals(Color.RED, color);

        color = Color.toEnum(BLUE_VALUE);
        assertEquals(Color.BLUE, color);

        color = Color.toEnum(GREEN_VALUE);
        assertEquals(Color.GREEN, color);
    }

    @Test
    public void toEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> Color.toEnum((byte)1));
    }

    private static int BITFIELD_ENUM_BITSIZEOF = 5;

    private static byte NONE_VALUE = 0;
    private static byte RED_VALUE = 2;
    private static byte BLUE_VALUE = 3;
    private static byte GREEN_VALUE = 7;
}
