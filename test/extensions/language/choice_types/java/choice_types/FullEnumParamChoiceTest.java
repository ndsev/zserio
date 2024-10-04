package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import choice_types.full_enum_param_choice.FullEnumParamChoice;
import choice_types.full_enum_param_choice.Selector;

public class FullEnumParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final Selector selector = Selector.BLACK;
        final FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(selector);
        assertEquals(selector, fullEnumParamChoice.getSelector());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.GREY;
        final int value = 234;
        final BitBuffer buffer = writeFullEnumParamChoiceToBitBuffer(selector, value);
        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(buffer.getBuffer(), buffer.getBitSize());
        final FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(reader, selector);
        assertEquals(selector, fullEnumParamChoice.getSelector());
        assertEquals((short)value, fullEnumParamChoice.getGrey());
    }

    @Test
    public void choiceTag()
    {
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(Selector.BLACK);
        assertEquals(FullEnumParamChoice.CHOICE_black, fullEnumParamChoice.choiceTag());

        fullEnumParamChoice = new FullEnumParamChoice(Selector.GREY);
        assertEquals(FullEnumParamChoice.CHOICE_grey, fullEnumParamChoice.choiceTag());

        fullEnumParamChoice = new FullEnumParamChoice(Selector.WHITE);
        assertEquals(FullEnumParamChoice.CHOICE_white, fullEnumParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(Selector.BLACK);
        assertEquals(8, fullEnumParamChoice.bitSizeOf());

        fullEnumParamChoice = new FullEnumParamChoice(Selector.GREY);
        assertEquals(16, fullEnumParamChoice.bitSizeOf());

        fullEnumParamChoice = new FullEnumParamChoice(Selector.WHITE);
        assertEquals(32, fullEnumParamChoice.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final Selector selector = Selector.BLACK;
        final FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(selector);
        assertEquals(selector, fullEnumParamChoice.getSelector());
    }

    @Test
    public void getSetBlack()
    {
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(Selector.BLACK);
        final byte value = 99;
        fullEnumParamChoice.setBlack(value);
        assertEquals(value, fullEnumParamChoice.getBlack());
    }

    @Test
    public void getSetGrey()
    {
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(Selector.GREY);
        final short value = 234;
        fullEnumParamChoice.setGrey(value);
        assertEquals(value, fullEnumParamChoice.getGrey());
    }

    @Test
    public void getSetWhite()
    {
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(Selector.WHITE);
        final int value = 65535;
        fullEnumParamChoice.setWhite(value);
        assertEquals(value, fullEnumParamChoice.getWhite());
    }

    @Test
    public void equals()
    {
        FullEnumParamChoice fullEnumParamChoice1 = new FullEnumParamChoice(Selector.BLACK);
        FullEnumParamChoice fullEnumParamChoice2 = new FullEnumParamChoice(Selector.BLACK);
        assertTrue(fullEnumParamChoice1.equals(fullEnumParamChoice2));

        final byte value = 99;
        fullEnumParamChoice1.setBlack(value);
        assertFalse(fullEnumParamChoice1.equals(fullEnumParamChoice2));

        fullEnumParamChoice2.setBlack(value);
        assertTrue(fullEnumParamChoice1.equals(fullEnumParamChoice2));

        final byte diffValue = value + 1;
        fullEnumParamChoice2.setBlack(diffValue);
        assertFalse(fullEnumParamChoice1.equals(fullEnumParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        FullEnumParamChoice fullEnumParamChoice1 = new FullEnumParamChoice(Selector.BLACK);
        FullEnumParamChoice fullEnumParamChoice2 = new FullEnumParamChoice(Selector.BLACK);
        assertEquals(fullEnumParamChoice1.hashCode(), fullEnumParamChoice2.hashCode());

        final byte value = 99;
        fullEnumParamChoice1.setBlack(value);
        assertTrue(fullEnumParamChoice1.hashCode() != fullEnumParamChoice2.hashCode());

        fullEnumParamChoice2.setBlack(value);
        assertEquals(fullEnumParamChoice1.hashCode(), fullEnumParamChoice2.hashCode());

        final byte diffValue = value + 1;
        fullEnumParamChoice2.setBlack(diffValue);
        assertTrue(fullEnumParamChoice1.hashCode() != fullEnumParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(63073, fullEnumParamChoice1.hashCode());
        assertEquals(63074, fullEnumParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(Selector.BLACK);
        final int bitPosition = 1;
        assertEquals(9, fullEnumParamChoice.initializeOffsets(bitPosition));

        fullEnumParamChoice = new FullEnumParamChoice(Selector.GREY);
        assertEquals(17, fullEnumParamChoice.initializeOffsets(bitPosition));

        fullEnumParamChoice = new FullEnumParamChoice(Selector.WHITE);
        assertEquals(33, fullEnumParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.BLACK;
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(selector);
        final byte byteValue = 99;
        fullEnumParamChoice.setBlack(byteValue);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        fullEnumParamChoice.write(writer);
        ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        FullEnumParamChoice readFullEnumParamChoice = new FullEnumParamChoice(reader, selector);
        assertEquals(byteValue, readFullEnumParamChoice.getBlack());

        selector = Selector.GREY;
        fullEnumParamChoice = new FullEnumParamChoice(selector);
        final short shortValue = 234;
        fullEnumParamChoice.setGrey(shortValue);
        writer = new ByteArrayBitStreamWriter();
        fullEnumParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readFullEnumParamChoice = new FullEnumParamChoice(reader, selector);
        assertEquals(shortValue, readFullEnumParamChoice.getGrey());

        selector = Selector.WHITE;
        fullEnumParamChoice = new FullEnumParamChoice(selector);
        final int intValue = 65535;
        fullEnumParamChoice.setWhite(intValue);
        writer = new ByteArrayBitStreamWriter();
        fullEnumParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readFullEnumParamChoice = new FullEnumParamChoice(reader, selector);
        assertEquals(intValue, readFullEnumParamChoice.getWhite());
    }

    private BitBuffer writeFullEnumParamChoiceToBitBuffer(Selector selector, int value) throws IOException
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
