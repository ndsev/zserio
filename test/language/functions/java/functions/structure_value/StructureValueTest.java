package functions.structure_value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureValueTest
{
    @Test
    public void checkCustomVarIntValue42() throws IOException
    {
        checkCustomVarInt(42);
    }

    @Test
    public void checkCustomVarIntValue253() throws IOException
    {
        checkCustomVarInt(MAX_ONE_BYTE_VALUE);
    }

    @Test
    public void checkCustomVarIntValue255() throws IOException
    {
        checkCustomVarInt(TWO_BYTES_INDICATOR);
    }

    @Test
    public void checkCustomVarIntValue254() throws IOException
    {
        checkCustomVarInt(FOUR_BYTES_INDICATOR);
    }

    @Test
    public void checkCustomVarIntValue1000() throws IOException
    {
        checkCustomVarInt(1000);
    }

    @Test
    public void checkCustomVarIntValue87654() throws IOException
    {
        checkCustomVarInt(87654);
    }

    private byte[] writeCustomVarIntToByteArray(int value) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        if (value <= MAX_ONE_BYTE_VALUE)
        {
            writer.writeBits(value, 8);
        }
        else if (value <= 0xFFFF)
        {
            writer.writeBits(TWO_BYTES_INDICATOR, 8);
            writer.writeBits(value, 16);
        }
        else
        {
            writer.writeBits(FOUR_BYTES_INDICATOR, 8);
            writer.writeBits(value, 32);
        }
        writer.close();

        return writer.toByteArray();
    }

    private CustomVarInt createCustomVarInt(int value)
    {
        final CustomVarInt customVarInt = new CustomVarInt();
        if (value <= MAX_ONE_BYTE_VALUE)
        {
            customVarInt.setVal1((short)value);
        }
        else if (value <= 0xFFFF)
        {
            customVarInt.setVal1(TWO_BYTES_INDICATOR);
            customVarInt.setVal2(value);
        }
        else
        {
            customVarInt.setVal1(FOUR_BYTES_INDICATOR);
            customVarInt.setVal3(Long.valueOf(value));
        }

        return customVarInt;
    }


    private void checkCustomVarInt(int value) throws IOException
    {
        final CustomVarInt customVarInt = createCustomVarInt(value);
        final long readValue = customVarInt.getValue();
        assertEquals(value, readValue);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        customVarInt.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final byte[] expecteByteArray = writeCustomVarIntToByteArray(value);
        assertTrue(Arrays.equals(expecteByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final CustomVarInt readcustomVarInt = new CustomVarInt(reader);
        assertEquals(customVarInt, readcustomVarInt);
    }

    private static final int    MAX_ONE_BYTE_VALUE = 253;
    private static final short  TWO_BYTES_INDICATOR = 255;
    private static final short  FOUR_BYTES_INDICATOR = 254;
}
