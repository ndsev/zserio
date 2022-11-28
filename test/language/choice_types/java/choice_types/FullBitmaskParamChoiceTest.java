package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import choice_types.full_bitmask_param_choice.FullBitmaskParamChoice;
import choice_types.full_bitmask_param_choice.Selector;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class FullBitmaskParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final Selector selector = Selector.Values.BLACK;
        final FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        assertEquals(selector, fullBitmaskParamChoice.getSelector());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.Values.WHITE;
        final int value = 234;
        final BitBuffer buffer = writeFullBitmaskParamChoiceToBitBuffer(selector, value);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer.getBuffer(),
                buffer.getBitSize());
        final FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(reader, selector);
        assertEquals(selector, fullBitmaskParamChoice.getSelector());
        assertEquals((short)value, fullBitmaskParamChoice.getWhite());
    }

    @Test
    public void choiceTag()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK);
        assertEquals(FullBitmaskParamChoice.CHOICE_black, fullBitmaskParamChoice.choiceTag());

        fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.WHITE);
        assertEquals(FullBitmaskParamChoice.CHOICE_white, fullBitmaskParamChoice.choiceTag());

        fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        assertEquals(FullBitmaskParamChoice.CHOICE_blackAndWhite, fullBitmaskParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK);
        assertEquals(8, fullBitmaskParamChoice.bitSizeOf());

        fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.WHITE);
        assertEquals(8, fullBitmaskParamChoice.bitSizeOf());

        fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        assertEquals(16, fullBitmaskParamChoice.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final Selector selector = Selector.Values.BLACK;
        final FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        assertEquals(selector, fullBitmaskParamChoice.getSelector());
    }

    @Test
    public void getSetBlack()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK);
        final byte value = 99;
        fullBitmaskParamChoice.setBlack(value);
        assertEquals(value, fullBitmaskParamChoice.getBlack());
    }

    @Test
    public void getSetWhite()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.WHITE);
        final short value = 234;
        fullBitmaskParamChoice.setWhite(value);
        assertEquals(value, fullBitmaskParamChoice.getWhite());
    }

    @Test
    public void getSetBlackAndWhite()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        final int value = 65535;
        fullBitmaskParamChoice.setBlackAndWhite(value);
        assertEquals(value, fullBitmaskParamChoice.getBlackAndWhite());
    }

    @Test
    public void equals()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice1 = new FullBitmaskParamChoice(Selector.Values.BLACK);
        FullBitmaskParamChoice fullBitmaskParamChoice2 = new FullBitmaskParamChoice(Selector.Values.BLACK);
        assertTrue(fullBitmaskParamChoice1.equals(fullBitmaskParamChoice2));

        final byte value = 99;
        fullBitmaskParamChoice1.setBlack(value);
        assertFalse(fullBitmaskParamChoice1.equals(fullBitmaskParamChoice2));

        fullBitmaskParamChoice2.setBlack(value);
        assertTrue(fullBitmaskParamChoice1.equals(fullBitmaskParamChoice2));

        final byte diffValue = value + 1;
        fullBitmaskParamChoice2.setBlack(diffValue);
        assertFalse(fullBitmaskParamChoice1.equals(fullBitmaskParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice1 = new FullBitmaskParamChoice(Selector.Values.BLACK);
        FullBitmaskParamChoice fullBitmaskParamChoice2 = new FullBitmaskParamChoice(Selector.Values.BLACK);
        assertEquals(fullBitmaskParamChoice1.hashCode(), fullBitmaskParamChoice2.hashCode());

        final byte value = 99;
        fullBitmaskParamChoice1.setBlack(value);
        assertTrue(fullBitmaskParamChoice1.hashCode() != fullBitmaskParamChoice2.hashCode());

        fullBitmaskParamChoice2.setBlack(value);
        assertEquals(fullBitmaskParamChoice1.hashCode(), fullBitmaskParamChoice2.hashCode());

        final byte diffValue = value + 1;
        fullBitmaskParamChoice2.setBlack(diffValue);
        assertTrue(fullBitmaskParamChoice1.hashCode() != fullBitmaskParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(63110, fullBitmaskParamChoice1.hashCode());
        assertEquals(63111, fullBitmaskParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK);
        final int bitPosition = 1;
        assertEquals(9, fullBitmaskParamChoice.initializeOffsets(bitPosition));

        fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.WHITE);
        assertEquals(9, fullBitmaskParamChoice.initializeOffsets(bitPosition));

        fullBitmaskParamChoice = new FullBitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        assertEquals(17, fullBitmaskParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.Values.BLACK;
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final byte byteValue = 99;
        fullBitmaskParamChoice.setBlack(byteValue);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        fullBitmaskParamChoice.write(writer);
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        FullBitmaskParamChoice readFullBitmaskParamChoice = new FullBitmaskParamChoice(reader, selector);
        assertEquals(byteValue, readFullBitmaskParamChoice.getBlack());

        selector = Selector.Values.WHITE;
        fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final short shortValue = 234;
        fullBitmaskParamChoice.setWhite(shortValue);
        writer = new ByteArrayBitStreamWriter();
        fullBitmaskParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readFullBitmaskParamChoice = new FullBitmaskParamChoice(reader, selector);
        assertEquals(shortValue, readFullBitmaskParamChoice.getWhite());

        selector = Selector.Values.BLACK_AND_WHITE;
        fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final int intValue = 65535;
        fullBitmaskParamChoice.setBlackAndWhite(intValue);
        writer = new ByteArrayBitStreamWriter();
        fullBitmaskParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readFullBitmaskParamChoice = new FullBitmaskParamChoice(reader, selector);
        assertEquals(intValue, readFullBitmaskParamChoice.getBlackAndWhite());
    }

    private BitBuffer writeFullBitmaskParamChoiceToBitBuffer(Selector selector, int value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            if (selector == Selector.Values.BLACK)
                writer.writeByte((byte)value);
            else if (selector == Selector.Values.WHITE)
                writer.writeByte((byte)value);
            else if (selector == Selector.Values.BLACK_AND_WHITE)
                writer.writeShort((short)value);
            else
                fail("Invalid selector: " + selector);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }
}
