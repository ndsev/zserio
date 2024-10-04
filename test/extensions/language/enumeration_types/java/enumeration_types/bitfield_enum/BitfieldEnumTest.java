package enumeration_types.bitfield_enum;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.HashCodeUtil;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

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
    public void writeRead() throws IOException
    {
        final Color color = Color.GREEN;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        color.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final byte readColor = (byte)reader.readBits(BITFIELD_ENUM_BITSIZEOF);
        assertEquals(readColor, color.getValue());
    }

    @Test
    public void writeReadFile() throws IOException
    {
        final Color color = Color.GREEN;
        SerializeUtil.serializeToFile(color, BLOB_NAME);

        final Color readColor = SerializeUtil.deserializeFromFile(Color.class, BLOB_NAME);
        assertEquals(color, readColor);
    }

    @Test
    public void valueToEnum()
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
    public void stringToEnum()
    {
        Color color = Color.toEnum("NONE");
        assertEquals(Color.NONE, color);

        color = Color.toEnum("RED");
        assertEquals(Color.RED, color);

        color = Color.toEnum("BLUE");
        assertEquals(Color.BLUE, color);

        color = Color.toEnum("GREEN");
        assertEquals(Color.GREEN, color);
    }

    @Test
    public void valueToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> Color.toEnum((byte)1));
    }

    @Test
    public void stringToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> Color.toEnum("NONEXISTING"));
    }

    private static final String BLOB_NAME = "bitfield_enum.blob";

    private static int BITFIELD_ENUM_BITSIZEOF = 3;

    private static byte NONE_VALUE = 0;
    private static byte RED_VALUE = 2;
    private static byte BLUE_VALUE = 3;
    private static byte GREEN_VALUE = 7;
}
