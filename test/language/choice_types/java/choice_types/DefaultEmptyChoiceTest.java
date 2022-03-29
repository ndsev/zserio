package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import choice_types.default_empty_choice.DefaultEmptyChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class DefaultEmptyChoiceTest
{
    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final byte tag = VARIANT_B_SELECTOR;
        final File file = new File("test.bin");
        final short value = 234;
        writeDefaultEmptyChoiceToFile(file, tag, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final DefaultEmptyChoice defaultEmptyChoice = new DefaultEmptyChoice(stream, tag);
        stream.close();
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
    public void fileWrite() throws IOException, ZserioError
    {
        final DefaultEmptyChoice defaultEmptyChoiceA = new DefaultEmptyChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        defaultEmptyChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        defaultEmptyChoiceA.write(file);
        final DefaultEmptyChoice readDefaultEmptyChoiceA = new DefaultEmptyChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readDefaultEmptyChoiceA.getA());

        final DefaultEmptyChoice defaultEmptyChoiceB = new DefaultEmptyChoice(VARIANT_B_SELECTOR);
        final short shortValueB = 234;
        defaultEmptyChoiceB.setB(shortValueB);
        defaultEmptyChoiceB.write(file);
        final DefaultEmptyChoice readDefaultEmptyChoiceB = new DefaultEmptyChoice(file, VARIANT_B_SELECTOR);
        assertEquals(shortValueB, readDefaultEmptyChoiceB.getB());

        final DefaultEmptyChoice defaultEmptyChoiceDefault = new DefaultEmptyChoice(DEFAULT_SELECTOR);
        defaultEmptyChoiceDefault.write(file);
        final DefaultEmptyChoice readDefaultEmptyChoiceDefault = new DefaultEmptyChoice(file, DEFAULT_SELECTOR);
        assertEquals(DEFAULT_SELECTOR, readDefaultEmptyChoiceDefault.getTag());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final DefaultEmptyChoice defaultEmptyChoiceA = new DefaultEmptyChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        defaultEmptyChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        defaultEmptyChoiceA.write(writer);
        writer.close();
        final DefaultEmptyChoice readDefaultEmptyChoiceA = new DefaultEmptyChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readDefaultEmptyChoiceA.getA());

        final DefaultEmptyChoice defaultEmptyChoiceB = new DefaultEmptyChoice(VARIANT_B_SELECTOR);
        final short shortValueB = 234;
        defaultEmptyChoiceB.setB(shortValueB);
        writer = new FileBitStreamWriter(file);
        defaultEmptyChoiceB.write(writer);
        writer.close();
        final DefaultEmptyChoice readDefaultEmptyChoiceB = new DefaultEmptyChoice(file, VARIANT_B_SELECTOR);
        assertEquals(shortValueB, readDefaultEmptyChoiceB.getB());

        final DefaultEmptyChoice defaultEmptyChoiceDefault = new DefaultEmptyChoice(DEFAULT_SELECTOR);
        writer = new FileBitStreamWriter(file);
        defaultEmptyChoiceDefault.write(writer);
        writer.close();
        final DefaultEmptyChoice readDefaultEmptyChoiceDefault = new DefaultEmptyChoice(file, DEFAULT_SELECTOR);
        assertEquals(DEFAULT_SELECTOR, readDefaultEmptyChoiceDefault.getTag());
    }

    private void writeDefaultEmptyChoiceToFile(File file, byte tag, short value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        switch (tag)
        {
        case 1:
            stream.writeByte(value);
            break;

        case 2:
            stream.writeShort(value);
            break;

        default:
            break;
        }

        stream.close();
    }

    private static byte VARIANT_A_SELECTOR = 1;
    private static byte VARIANT_B_SELECTOR = 2;
    private static byte DEFAULT_SELECTOR = 3;
}
