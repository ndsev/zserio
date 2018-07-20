package choice_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import choice_types.bool_param_choice.BoolParamChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class BoolParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final boolean selector = true;
        final BoolParamChoice enumParamChoice = new BoolParamChoice(selector);
        assertEquals(selector, enumParamChoice.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final boolean selector = true;
        final File file = new File("test.bin");
        final int value = 99;
        writeBoolParamChoiceToFile(file, selector, value);
        final BoolParamChoice enumParamChoice = new BoolParamChoice(file, selector);
        assertEquals(selector, enumParamChoice.getSelector());
        assertEquals((byte)value, enumParamChoice.getBlack());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final boolean selector = false;
        final File file = new File("test.bin");
        final int value = 234;
        writeBoolParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final BoolParamChoice enumParamChoice = new BoolParamChoice(stream, selector);
        stream.close();
        assertEquals(selector, enumParamChoice.getSelector());
        assertEquals((short)value, enumParamChoice.getGrey());
    }

    @Test
    public void bitSizeOf()
    {
        BoolParamChoice enumParamChoice = new BoolParamChoice(true);
        assertEquals(8, enumParamChoice.bitSizeOf());

        enumParamChoice = new BoolParamChoice(false);
        assertEquals(16, enumParamChoice.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final boolean selector = true;
        final BoolParamChoice enumParamChoice = new BoolParamChoice(selector);
        assertEquals(selector, enumParamChoice.getSelector());
    }

    @Test
    public void getSetBlack()
    {
        BoolParamChoice enumParamChoice = new BoolParamChoice(true);
        final byte value = 99;
        enumParamChoice.setBlack(value);
        assertEquals(value, enumParamChoice.getBlack());
    }

    @Test
    public void getSetGrey()
    {
        BoolParamChoice enumParamChoice = new BoolParamChoice(false);
        final short value = 234;
        enumParamChoice.setGrey(value);
        assertEquals(value, enumParamChoice.getGrey());
    }

    @Test
    public void equals()
    {
        BoolParamChoice enumParamChoice1 = new BoolParamChoice(true);
        BoolParamChoice enumParamChoice2 = new BoolParamChoice(true);
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
        BoolParamChoice enumParamChoice1 = new BoolParamChoice(true);
        BoolParamChoice enumParamChoice2 = new BoolParamChoice(true);
        assertEquals(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

        final byte value = 99;
        enumParamChoice1.setBlack(value);
        assertTrue(enumParamChoice1.hashCode() != enumParamChoice2.hashCode());

        enumParamChoice2.setBlack(value);
        assertEquals(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

        final byte diffValue = value + 1;
        enumParamChoice2.setBlack(diffValue);
        assertTrue(enumParamChoice1.hashCode() != enumParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        BoolParamChoice enumParamChoice = new BoolParamChoice(true);
        final int bitPosition = 1;
        assertEquals(9, enumParamChoice.initializeOffsets(bitPosition));

        enumParamChoice = new BoolParamChoice(false);
        assertEquals(17, enumParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        boolean selector = true;
        BoolParamChoice enumParamChoice = new BoolParamChoice(selector);
        final byte byteValue = 99;
        enumParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        enumParamChoice.write(file);
        BoolParamChoice readBoolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(byteValue, readBoolParamChoice.getBlack());

        selector = false;
        enumParamChoice = new BoolParamChoice(selector);
        final short shortValue = 234;
        enumParamChoice.setGrey(shortValue);
        enumParamChoice.write(file);
        readBoolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(shortValue, readBoolParamChoice.getGrey());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        boolean selector = true;
        BoolParamChoice enumParamChoice = new BoolParamChoice(selector);
        final byte byteValue = 99;
        enumParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        enumParamChoice.write(writer);
        writer.close();
        BoolParamChoice readBoolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(byteValue, readBoolParamChoice.getBlack());

        selector = false;
        enumParamChoice = new BoolParamChoice(selector);
        final short shortValue = 234;
        enumParamChoice.setGrey(shortValue);
        writer = new FileBitStreamWriter(file);
        enumParamChoice.write(writer);
        writer.close();
        readBoolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(shortValue, readBoolParamChoice.getGrey());
    }

    private void writeBoolParamChoiceToFile(File file, boolean selector, int value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        if (selector)
            stream.writeByte(value);
        else
            stream.writeShort(value);

        stream.close();
    }
}
