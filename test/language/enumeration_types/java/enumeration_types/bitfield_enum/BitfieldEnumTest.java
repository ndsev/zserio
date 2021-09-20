package enumeration_types.bitfield_enum;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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
        final BitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final byte readColor = (byte)reader.readBits(BITFIELD_ENUM_BITSIZEOF);
        assertEquals(readColor, color.getValue());
    }

    @Test
    public void writeReadFile() throws IOException
    {
        final Color color = Color.GREEN;
        final File file = new File(BLOB_NAME);
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);
        color.write(writer);
        writer.close();

        final BitStreamReader reader = new FileBitStreamReader(file);
        final Color readColor = Color.readEnum(reader);
        assertEquals(color, readColor);
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

    @Test(expected=IllegalArgumentException.class)
    public void toEnumFailure()
    {
        Color.toEnum((byte)1);
    }

    private static final String BLOB_NAME = "bitfield_enum.blob";

    private static int BITFIELD_ENUM_BITSIZEOF = 3;

    private static byte NONE_VALUE = 0;
    private static byte RED_VALUE = 2;
    private static byte BLUE_VALUE = 3;
    private static byte GREEN_VALUE = 7;
}
