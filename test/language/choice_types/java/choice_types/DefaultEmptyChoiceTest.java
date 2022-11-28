package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import choice_types.default_empty_choice.DefaultEmptyChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class DefaultEmptyChoiceTest
{
    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final byte tag = VARIANT_B_SELECTOR;
        final short value = 234;
        final BitBuffer buffer = writeDefaultEmptyChoiceToBitBuffer(tag, value);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer.getBuffer(),
                buffer.getBitSize());
        final DefaultEmptyChoice defaultEmptyChoice = new DefaultEmptyChoice(reader, tag);
        assertEquals(tag, defaultEmptyChoice.getTag());
        assertEquals((short)value, defaultEmptyChoice.getB());
    }

    @Test
    public void choiceTag()
    {
        DefaultEmptyChoice defaultEmptyChoice = new DefaultEmptyChoice(VARIANT_A_SELECTOR);
        assertEquals(DefaultEmptyChoice.CHOICE_a, defaultEmptyChoice.choiceTag());

        defaultEmptyChoice = new DefaultEmptyChoice(VARIANT_B_SELECTOR);
        assertEquals(DefaultEmptyChoice.CHOICE_b, defaultEmptyChoice.choiceTag());

        defaultEmptyChoice = new DefaultEmptyChoice(DEFAULT_SELECTOR);
        assertEquals(DefaultEmptyChoice.UNDEFINED_CHOICE, defaultEmptyChoice.choiceTag());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final DefaultEmptyChoice defaultEmptyChoiceA = new DefaultEmptyChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        defaultEmptyChoiceA.setA(byteValueA);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        defaultEmptyChoiceA.write(writer);
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        final DefaultEmptyChoice readDefaultEmptyChoiceA = new DefaultEmptyChoice(reader, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readDefaultEmptyChoiceA.getA());

        final DefaultEmptyChoice defaultEmptyChoiceB = new DefaultEmptyChoice(VARIANT_B_SELECTOR);
        final short shortValueB = 234;
        defaultEmptyChoiceB.setB(shortValueB);
        writer = new ByteArrayBitStreamWriter();
        defaultEmptyChoiceB.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final DefaultEmptyChoice readDefaultEmptyChoiceB = new DefaultEmptyChoice(reader, VARIANT_B_SELECTOR);
        assertEquals(shortValueB, readDefaultEmptyChoiceB.getB());

        final DefaultEmptyChoice defaultEmptyChoiceDefault = new DefaultEmptyChoice(DEFAULT_SELECTOR);
        writer = new ByteArrayBitStreamWriter();
        defaultEmptyChoiceDefault.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final DefaultEmptyChoice readDefaultEmptyChoiceDefault =
                new DefaultEmptyChoice(reader, DEFAULT_SELECTOR);
        assertEquals(DEFAULT_SELECTOR, readDefaultEmptyChoiceDefault.getTag());
    }

    private BitBuffer writeDefaultEmptyChoiceToBitBuffer(byte tag, short value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            switch (tag)
            {
            case 1:
                writer.writeByte((byte)value);
                break;

            case 2:
                writer.writeShort(value);
                break;

            default:
                break;
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static byte VARIANT_A_SELECTOR = 1;
    private static byte VARIANT_B_SELECTOR = 2;
    private static byte DEFAULT_SELECTOR = 3;
}
