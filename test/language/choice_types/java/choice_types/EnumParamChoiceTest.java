package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import choice_types.enum_param_choice.EnumParamChoice;
import choice_types.enum_param_choice.Selector;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class EnumParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final Selector selector = Selector.BLACK;
        final EnumParamChoice enumParamChoice = new EnumParamChoice(selector);
        assertEquals(selector, enumParamChoice.getSelector());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.GREY;
        final int value = 234;
        final BitBuffer buffer = writeEnumParamChoiceToBitBuffer(selector, value);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer.getBuffer(),
                buffer.getBitSize());
        final EnumParamChoice enumParamChoice = new EnumParamChoice(reader, selector);
        assertEquals(selector, enumParamChoice.getSelector());
        assertEquals((short)value, enumParamChoice.getGrey());
    }

    @Test
    public void choiceTag()
    {
        EnumParamChoice enumParamChoice = new EnumParamChoice(Selector.BLACK);
        assertEquals(EnumParamChoice.CHOICE_black, enumParamChoice.choiceTag());

        enumParamChoice = new EnumParamChoice(Selector.GREY);
        assertEquals(EnumParamChoice.CHOICE_grey, enumParamChoice.choiceTag());

        enumParamChoice = new EnumParamChoice(Selector.WHITE);
        assertEquals(EnumParamChoice.CHOICE_white, enumParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        EnumParamChoice enumParamChoice = new EnumParamChoice(Selector.BLACK);
        assertEquals(8, enumParamChoice.bitSizeOf());

        enumParamChoice = new EnumParamChoice(Selector.GREY);
        assertEquals(16, enumParamChoice.bitSizeOf());

        enumParamChoice = new EnumParamChoice(Selector.WHITE);
        assertEquals(32, enumParamChoice.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final Selector selector = Selector.BLACK;
        final EnumParamChoice enumParamChoice = new EnumParamChoice(selector);
        assertEquals(selector, enumParamChoice.getSelector());
    }

    @Test
    public void getSetBlack()
    {
        EnumParamChoice enumParamChoice = new EnumParamChoice(Selector.BLACK);
        final byte value = 99;
        enumParamChoice.setBlack(value);
        assertEquals(value, enumParamChoice.getBlack());
    }

    @Test
    public void getSetGrey()
    {
        EnumParamChoice enumParamChoice = new EnumParamChoice(Selector.GREY);
        final short value = 234;
        enumParamChoice.setGrey(value);
        assertEquals(value, enumParamChoice.getGrey());
    }

    @Test
    public void getSetWhite()
    {
        EnumParamChoice enumParamChoice = new EnumParamChoice(Selector.WHITE);
        final int value = 65535;
        enumParamChoice.setWhite(value);
        assertEquals(value, enumParamChoice.getWhite());
    }

    @Test
    public void equals()
    {
        EnumParamChoice enumParamChoice1 = new EnumParamChoice(Selector.BLACK);
        EnumParamChoice enumParamChoice2 = new EnumParamChoice(Selector.BLACK);
        assertTrue(enumParamChoice1.equals(enumParamChoice2));

        final byte value = 99;
        enumParamChoice1.setBlack(value);
        assertFalse(enumParamChoice1.equals(enumParamChoice2));

        enumParamChoice2.setBlack(value);
        assertTrue(enumParamChoice1.equals(enumParamChoice2));

        final byte diffValue = value + 1;
        enumParamChoice2.setBlack(diffValue);
        assertFalse(enumParamChoice1.equals(enumParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        EnumParamChoice enumParamChoice1 = new EnumParamChoice(Selector.BLACK);
        EnumParamChoice enumParamChoice2 = new EnumParamChoice(Selector.BLACK);
        assertEquals(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

        final byte value = 99;
        enumParamChoice1.setBlack(value);
        assertTrue(enumParamChoice1.hashCode() != enumParamChoice2.hashCode());

        enumParamChoice2.setBlack(value);
        assertEquals(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

        final byte diffValue = value + 1;
        enumParamChoice2.setBlack(diffValue);
        assertTrue(enumParamChoice1.hashCode() != enumParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(63073, enumParamChoice1.hashCode());
        assertEquals(63074, enumParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        EnumParamChoice enumParamChoice = new EnumParamChoice(Selector.BLACK);
        final int bitPosition = 1;
        assertEquals(9, enumParamChoice.initializeOffsets(bitPosition));

        enumParamChoice = new EnumParamChoice(Selector.GREY);
        assertEquals(17, enumParamChoice.initializeOffsets(bitPosition));

        enumParamChoice = new EnumParamChoice(Selector.WHITE);
        assertEquals(33, enumParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.BLACK;
        EnumParamChoice enumParamChoice = new EnumParamChoice(selector);
        final byte byteValue = 99;
        enumParamChoice.setBlack(byteValue);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        enumParamChoice.write(writer);
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        EnumParamChoice readEnumParamChoice = new EnumParamChoice(reader, selector);
        assertEquals(byteValue, readEnumParamChoice.getBlack());

        selector = Selector.GREY;
        enumParamChoice = new EnumParamChoice(selector);
        final short shortValue = 234;
        enumParamChoice.setGrey(shortValue);
        writer = new ByteArrayBitStreamWriter();
        enumParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readEnumParamChoice = new EnumParamChoice(reader, selector);
        assertEquals(shortValue, readEnumParamChoice.getGrey());

        selector = Selector.WHITE;
        enumParamChoice = new EnumParamChoice(selector);
        final int intValue = 65535;
        enumParamChoice.setWhite(intValue);
        writer = new ByteArrayBitStreamWriter();
        enumParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readEnumParamChoice = new EnumParamChoice(reader, selector);
        assertEquals(intValue, readEnumParamChoice.getWhite());
    }

    private BitBuffer writeEnumParamChoiceToBitBuffer(Selector selector, int value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            if (selector == Selector.BLACK)
                writer.writeByte((byte)value);
            else if (selector == Selector.GREY)
                writer.writeShort((short)value);
            else if (selector == Selector.WHITE)
                writer.writeInt(value);
            else
                fail("Invalid selector: " + selector);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }
}
