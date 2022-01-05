package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import choice_types.full_bitmask_param_choice.FullBitmaskParamChoice;
import choice_types.full_bitmask_param_choice.Selector;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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
    public void fileConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.Values.BLACK;
        final File file = new File("test.bin");
        final int value = 99;
        writeFullBitmaskParamChoiceToFile(file, selector, value);
        final FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(file, selector);
        assertEquals(selector, fullBitmaskParamChoice.getSelector());
        assertEquals((byte)value, fullBitmaskParamChoice.getBlack());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.Values.WHITE;
        final File file = new File("test.bin");
        final int value = 234;
        writeFullBitmaskParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(stream, selector);
        stream.close();
        assertEquals(selector, fullBitmaskParamChoice.getSelector());
        assertEquals((short)value, fullBitmaskParamChoice.getWhite());
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
    public void fileWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.Values.BLACK;
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final byte byteValue = 99;
        fullBitmaskParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        fullBitmaskParamChoice.write(file);
        FullBitmaskParamChoice readFullBitmaskParamChoice = new FullBitmaskParamChoice(file, selector);
        assertEquals(byteValue, readFullBitmaskParamChoice.getBlack());

        selector = Selector.Values.WHITE;
        fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final short shortValue = 234;
        fullBitmaskParamChoice.setWhite(shortValue);
        fullBitmaskParamChoice.write(file);
        readFullBitmaskParamChoice = new FullBitmaskParamChoice(file, selector);
        assertEquals(shortValue, readFullBitmaskParamChoice.getWhite());

        selector = Selector.Values.BLACK_AND_WHITE;
        fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final int intValue = 65535;
        fullBitmaskParamChoice.setBlackAndWhite(intValue);
        fullBitmaskParamChoice.write(file);
        readFullBitmaskParamChoice = new FullBitmaskParamChoice(file, selector);
        assertEquals(intValue, readFullBitmaskParamChoice.getBlackAndWhite());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.Values.BLACK;
        FullBitmaskParamChoice fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final byte byteValue = 99;
        fullBitmaskParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        fullBitmaskParamChoice.write(writer);
        writer.close();
        FullBitmaskParamChoice readFullBitmaskParamChoice = new FullBitmaskParamChoice(file, selector);
        assertEquals(byteValue, readFullBitmaskParamChoice.getBlack());

        selector = Selector.Values.WHITE;
        fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final short shortValue = 234;
        fullBitmaskParamChoice.setWhite(shortValue);
        writer = new FileBitStreamWriter(file);
        fullBitmaskParamChoice.write(writer);
        writer.close();
        readFullBitmaskParamChoice = new FullBitmaskParamChoice(file, selector);
        assertEquals(shortValue, readFullBitmaskParamChoice.getWhite());

        selector = Selector.Values.BLACK_AND_WHITE;
        fullBitmaskParamChoice = new FullBitmaskParamChoice(selector);
        final int intValue = 65535;
        fullBitmaskParamChoice.setBlackAndWhite(intValue);
        writer = new FileBitStreamWriter(file);
        fullBitmaskParamChoice.write(writer);
        writer.close();
        readFullBitmaskParamChoice = new FullBitmaskParamChoice(file, selector);
        assertEquals(intValue, readFullBitmaskParamChoice.getBlackAndWhite());
    }

    private void writeFullBitmaskParamChoiceToFile(File file, Selector selector, int value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        if (selector == Selector.Values.BLACK)
            stream.writeByte(value);
        else if (selector == Selector.Values.WHITE)
            stream.writeByte(value);
        else if (selector == Selector.Values.BLACK_AND_WHITE)
            stream.writeShort(value);
        else
            fail("Invalid selector: " + selector);

        stream.close();
    }
}
