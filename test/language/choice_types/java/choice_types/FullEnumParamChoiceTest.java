package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import choice_types.full_enum_param_choice.FullEnumParamChoice;
import choice_types.full_enum_param_choice.Selector;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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
    public void fileConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.BLACK;
        final File file = new File("test.bin");
        final int value = 99;
        writeFullEnumParamChoiceToFile(file, selector, value);
        final FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(file, selector);
        assertEquals(selector, fullEnumParamChoice.getSelector());
        assertEquals((byte)value, fullEnumParamChoice.getBlack());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.GREY;
        final File file = new File("test.bin");
        final int value = 234;
        writeFullEnumParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(stream, selector);
        stream.close();
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
    public void fileWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.BLACK;
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(selector);
        final byte byteValue = 99;
        fullEnumParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        fullEnumParamChoice.write(file);
        FullEnumParamChoice readFullEnumParamChoice = new FullEnumParamChoice(file, selector);
        assertEquals(byteValue, readFullEnumParamChoice.getBlack());

        selector = Selector.GREY;
        fullEnumParamChoice = new FullEnumParamChoice(selector);
        final short shortValue = 234;
        fullEnumParamChoice.setGrey(shortValue);
        fullEnumParamChoice.write(file);
        readFullEnumParamChoice = new FullEnumParamChoice(file, selector);
        assertEquals(shortValue, readFullEnumParamChoice.getGrey());

        selector = Selector.WHITE;
        fullEnumParamChoice = new FullEnumParamChoice(selector);
        final int intValue = 65535;
        fullEnumParamChoice.setWhite(intValue);
        fullEnumParamChoice.write(file);
        readFullEnumParamChoice = new FullEnumParamChoice(file, selector);
        assertEquals(intValue, readFullEnumParamChoice.getWhite());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.BLACK;
        FullEnumParamChoice fullEnumParamChoice = new FullEnumParamChoice(selector);
        final byte byteValue = 99;
        fullEnumParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        fullEnumParamChoice.write(writer);
        writer.close();
        FullEnumParamChoice readFullEnumParamChoice = new FullEnumParamChoice(file, selector);
        assertEquals(byteValue, readFullEnumParamChoice.getBlack());

        selector = Selector.GREY;
        fullEnumParamChoice = new FullEnumParamChoice(selector);
        final short shortValue = 234;
        fullEnumParamChoice.setGrey(shortValue);
        writer = new FileBitStreamWriter(file);
        fullEnumParamChoice.write(writer);
        writer.close();
        readFullEnumParamChoice = new FullEnumParamChoice(file, selector);
        assertEquals(shortValue, readFullEnumParamChoice.getGrey());

        selector = Selector.WHITE;
        fullEnumParamChoice = new FullEnumParamChoice(selector);
        final int intValue = 65535;
        fullEnumParamChoice.setWhite(intValue);
        writer = new FileBitStreamWriter(file);
        fullEnumParamChoice.write(writer);
        writer.close();
        readFullEnumParamChoice = new FullEnumParamChoice(file, selector);
        assertEquals(intValue, readFullEnumParamChoice.getWhite());
    }

    private void writeFullEnumParamChoiceToFile(File file, Selector selector, int value) throws IOException
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
