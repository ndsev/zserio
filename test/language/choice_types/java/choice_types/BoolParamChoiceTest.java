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
        final BoolParamChoice boolParamChoice = new BoolParamChoice(selector);
        assertEquals(selector, boolParamChoice.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final boolean selector = true;
        final File file = new File("test.bin");
        final int value = 99;
        writeBoolParamChoiceToFile(file, selector, value);
        final BoolParamChoice boolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(selector, boolParamChoice.getSelector());
        assertEquals((byte)value, boolParamChoice.getBlack());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final boolean selector = false;
        final File file = new File("test.bin");
        final int value = 234;
        writeBoolParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final BoolParamChoice boolParamChoice = new BoolParamChoice(stream, selector);
        stream.close();
        assertEquals(selector, boolParamChoice.getSelector());
        assertEquals((short)value, boolParamChoice.getGrey());
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
        BoolParamChoice boolParamChoice = new BoolParamChoice(true);
        final int bitPosition = 1;
        assertEquals(9, boolParamChoice.initializeOffsets(bitPosition));

        boolParamChoice = new BoolParamChoice(false);
        assertEquals(17, boolParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        boolean selector = true;
        BoolParamChoice boolParamChoice = new BoolParamChoice(selector);
        final byte byteValue = 99;
        boolParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        boolParamChoice.write(file);
        BoolParamChoice readBoolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(byteValue, readBoolParamChoice.getBlack());

        selector = false;
        boolParamChoice = new BoolParamChoice(selector);
        final short shortValue = 234;
        boolParamChoice.setGrey(shortValue);
        boolParamChoice.write(file);
        readBoolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(shortValue, readBoolParamChoice.getGrey());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        boolean selector = true;
        BoolParamChoice boolParamChoice = new BoolParamChoice(selector);
        final byte byteValue = 99;
        boolParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        boolParamChoice.write(writer);
        writer.close();
        BoolParamChoice readBoolParamChoice = new BoolParamChoice(file, selector);
        assertEquals(byteValue, readBoolParamChoice.getBlack());

        selector = false;
        boolParamChoice = new BoolParamChoice(selector);
        final short shortValue = 234;
        boolParamChoice.setGrey(shortValue);
        writer = new FileBitStreamWriter(file);
        boolParamChoice.write(writer);
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
