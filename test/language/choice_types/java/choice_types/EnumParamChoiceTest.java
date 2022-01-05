package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import choice_types.enum_param_choice.EnumParamChoice;
import choice_types.enum_param_choice.Selector;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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
    public void fileConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.BLACK;
        final File file = new File("test.bin");
        final int value = 99;
        writeEnumParamChoiceToFile(file, selector, value);
        final EnumParamChoice enumParamChoice = new EnumParamChoice(file, selector);
        assertEquals(selector, enumParamChoice.getSelector());
        assertEquals((byte)value, enumParamChoice.getBlack());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.GREY;
        final File file = new File("test.bin");
        final int value = 234;
        writeEnumParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final EnumParamChoice enumParamChoice = new EnumParamChoice(stream, selector);
        stream.close();
        assertEquals(selector, enumParamChoice.getSelector());
        assertEquals((short)value, enumParamChoice.getGrey());
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
    public void fileWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.BLACK;
        EnumParamChoice enumParamChoice = new EnumParamChoice(selector);
        final byte byteValue = 99;
        enumParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        enumParamChoice.write(file);
        EnumParamChoice readEnumParamChoice = new EnumParamChoice(file, selector);
        assertEquals(byteValue, readEnumParamChoice.getBlack());

        selector = Selector.GREY;
        enumParamChoice = new EnumParamChoice(selector);
        final short shortValue = 234;
        enumParamChoice.setGrey(shortValue);
        enumParamChoice.write(file);
        readEnumParamChoice = new EnumParamChoice(file, selector);
        assertEquals(shortValue, readEnumParamChoice.getGrey());

        selector = Selector.WHITE;
        enumParamChoice = new EnumParamChoice(selector);
        final int intValue = 65535;
        enumParamChoice.setWhite(intValue);
        enumParamChoice.write(file);
        readEnumParamChoice = new EnumParamChoice(file, selector);
        assertEquals(intValue, readEnumParamChoice.getWhite());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.BLACK;
        EnumParamChoice enumParamChoice = new EnumParamChoice(selector);
        final byte byteValue = 99;
        enumParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        enumParamChoice.write(writer);
        writer.close();
        EnumParamChoice readEnumParamChoice = new EnumParamChoice(file, selector);
        assertEquals(byteValue, readEnumParamChoice.getBlack());

        selector = Selector.GREY;
        enumParamChoice = new EnumParamChoice(selector);
        final short shortValue = 234;
        enumParamChoice.setGrey(shortValue);
        writer = new FileBitStreamWriter(file);
        enumParamChoice.write(writer);
        writer.close();
        readEnumParamChoice = new EnumParamChoice(file, selector);
        assertEquals(shortValue, readEnumParamChoice.getGrey());

        selector = Selector.WHITE;
        enumParamChoice = new EnumParamChoice(selector);
        final int intValue = 65535;
        enumParamChoice.setWhite(intValue);
        writer = new FileBitStreamWriter(file);
        enumParamChoice.write(writer);
        writer.close();
        readEnumParamChoice = new EnumParamChoice(file, selector);
        assertEquals(intValue, readEnumParamChoice.getWhite());
    }

    private void writeEnumParamChoiceToFile(File file, Selector selector, int value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        if (selector == Selector.BLACK)
            stream.writeByte(value);
        else if (selector == Selector.GREY)
            stream.writeShort(value);
        else if (selector == Selector.WHITE)
            stream.writeInt(value);
        else
            fail("Invalid selector: " + selector);

        stream.close();
    }
}
