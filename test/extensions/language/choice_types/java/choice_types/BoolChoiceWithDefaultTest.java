package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import choice_types.bool_choice_with_default.BoolChoiceWithDefault;
import test_utils.CompoundUtil;

public class BoolChoiceWithDefaultTest
{
    @Test
    public void constructor()
    {
        final boolean selector = true;
        final BoolChoiceWithDefault data = new BoolChoiceWithDefault(selector);
        assertEquals(selector, data.getSelector());
    }

    @Test
    public void bitSizeOf()
    {
        BoolChoiceWithDefault data = new BoolChoiceWithDefault(true);
        assertEquals(8, data.bitSizeOf());
    }

    @Test
    public void equals()
    {
        BoolChoiceWithDefault data1 = new BoolChoiceWithDefault(true);
        BoolChoiceWithDefault data2 = new BoolChoiceWithDefault(true);
        assertTrue(data1.equals(data2));

        data1.setField((short)99);
        assertFalse(data1.equals(data2));
    }

    @Test
    public void hash()
    {
        BoolChoiceWithDefault data1 = new BoolChoiceWithDefault(false);
        data1.setField((short)99);
        BoolChoiceWithDefault data2 = new BoolChoiceWithDefault(false);
        data2.setField((short)99);
        CompoundUtil.hashTest(data1, 31586, data2);
    }

    @Test
    public void writeRead()
    {
        boolean selector = true;
        BoolChoiceWithDefault data = new BoolChoiceWithDefault(selector);
        data.setField((short)230);
        CompoundUtil.writeReadTest(BoolChoiceWithDefault.class, data, selector);
    }

    @Test
    public void read() throws IOException
    {
        final boolean selector = false;
        final short value = 234;
        final BoolChoiceWithDefault data = new BoolChoiceWithDefault(selector);
        data.setField(value);
        final BitBuffer buffer = writeBoolParamChoiceToBitBuffer(selector, value);
        CompoundUtil.readTest(buffer, BoolChoiceWithDefault.class, data, selector);
    }

    private BitBuffer writeBoolParamChoiceToBitBuffer(boolean selector, short value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeUnsignedByte(value);
            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }
}
