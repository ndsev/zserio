package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import choice_types.bool_param_choice.BoolParamChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class BoolParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final boolean selector = true;
        final BoolParamChoice boolParamChoice = new BoolParamChoice(selector);
        assertEquals(selector, boolParamChoice.getSelector());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final boolean selector = false;
        final byte value = (byte)234;
        final BitBuffer buffer = writeBoolParamChoiceToBitBuffer(selector, value);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer.getBuffer(),
                buffer.getBitSize());
        final BoolParamChoice boolParamChoice = new BoolParamChoice(reader, selector);
        assertEquals(selector, boolParamChoice.getSelector());
        assertEquals((short)value, boolParamChoice.getGrey());
    }

    @Test
    public void choiceTag()
    {
        BoolParamChoice boolParamChoice = new BoolParamChoice(true);
        assertEquals(BoolParamChoice.CHOICE_black, boolParamChoice.choiceTag());

        boolParamChoice = new BoolParamChoice(false);
        assertEquals(BoolParamChoice.CHOICE_grey, boolParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        BoolParamChoice boolParamChoice = new BoolParamChoice(true);
        assertEquals(8, boolParamChoice.bitSizeOf());

        boolParamChoice = new BoolParamChoice(false);
        assertEquals(16, boolParamChoice.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final boolean selector = true;
        final BoolParamChoice boolParamChoice = new BoolParamChoice(selector);
        assertEquals(selector, boolParamChoice.getSelector());
    }

    @Test
    public void getSetBlack()
    {
        BoolParamChoice boolParamChoice = new BoolParamChoice(true);
        final byte value = 99;
        boolParamChoice.setBlack(value);
        assertEquals(value, boolParamChoice.getBlack());
    }

    @Test
    public void getSetGrey()
    {
        BoolParamChoice boolParamChoice = new BoolParamChoice(false);
        final short value = 234;
        boolParamChoice.setGrey(value);
        assertEquals(value, boolParamChoice.getGrey());
    }

    @Test
    public void equals()
    {
        BoolParamChoice boolParamChoice1 = new BoolParamChoice(true);
        BoolParamChoice boolParamChoice2 = new BoolParamChoice(true);
        assertTrue(boolParamChoice1.equals(boolParamChoice2));

        final byte value = 99;
        boolParamChoice1.setBlack(value);
        assertFalse(boolParamChoice1.equals(boolParamChoice2));

        boolParamChoice2.setBlack(value);
        assertTrue(boolParamChoice1.equals(boolParamChoice2));

        final byte diffValue = value + 1;
        boolParamChoice2.setBlack(diffValue);
        assertFalse(boolParamChoice1.equals(boolParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        BoolParamChoice boolParamChoice1 = new BoolParamChoice(true);
        BoolParamChoice boolParamChoice2 = new BoolParamChoice(true);
        assertEquals(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

        final byte value = 99;
        boolParamChoice1.setBlack(value);
        assertTrue(boolParamChoice1.hashCode() != boolParamChoice2.hashCode());

        boolParamChoice2.setBlack(value);
        assertEquals(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

        final byte diffValue = value + 1;
        boolParamChoice2.setBlack(diffValue);
        assertTrue(boolParamChoice1.hashCode() != boolParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(31623, boolParamChoice1.hashCode());
        assertEquals(31624, boolParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        BoolParamChoice boolParamChoice = new BoolParamChoice(true);
        final int bitPosition = 1;
        assertEquals(9, boolParamChoice.initializeOffsets(bitPosition));

        boolParamChoice = new BoolParamChoice(false);
        assertEquals(17, boolParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        boolean selector = true;
        BoolParamChoice boolParamChoice = new BoolParamChoice(selector);
        final byte byteValue = 99;
        boolParamChoice.setBlack(byteValue);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        boolParamChoice.write(writer);
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        BoolParamChoice readBoolParamChoice = new BoolParamChoice(reader, selector);
        assertEquals(byteValue, readBoolParamChoice.getBlack());

        selector = false;
        boolParamChoice = new BoolParamChoice(selector);
        final short shortValue = 234;
        boolParamChoice.setGrey(shortValue);
        writer = new ByteArrayBitStreamWriter();
        boolParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readBoolParamChoice = new BoolParamChoice(reader, selector);
        assertEquals(shortValue, readBoolParamChoice.getGrey());
    }

    private BitBuffer writeBoolParamChoiceToBitBuffer(boolean selector, byte value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            if (selector)
                writer.writeByte(value);
            else
                writer.writeShort(value);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }
}
