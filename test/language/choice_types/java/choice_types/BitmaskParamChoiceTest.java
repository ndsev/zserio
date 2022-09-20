package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import choice_types.bitmask_param_choice.BitmaskParamChoice;
import choice_types.bitmask_param_choice.Selector;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class BitmaskParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final Selector selector = Selector.Values.BLACK;
        final BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(selector);
        assertEquals(selector, bitmaskParamChoice.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.Values.BLACK;
        final File file = new File("test.bin");
        final int value = 99;
        writeBitmaskParamChoiceToFile(file, selector, value);
        final BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(file, selector);
        assertEquals(selector, bitmaskParamChoice.getSelector());
        assertEquals((byte)value, bitmaskParamChoice.getBlack());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final Selector selector = Selector.Values.WHITE;
        final File file = new File("test.bin");
        final int value = 234;
        writeBitmaskParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(stream, selector);
        stream.close();
        assertEquals(selector, bitmaskParamChoice.getSelector());
        assertEquals((short)value, bitmaskParamChoice.getWhite());
    }

    @Test
    public void choiceTag()
    {
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK);
        assertEquals(BitmaskParamChoice.CHOICE_black, bitmaskParamChoice.choiceTag());

        bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.WHITE);
        assertEquals(BitmaskParamChoice.CHOICE_white, bitmaskParamChoice.choiceTag());

        bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        assertEquals(BitmaskParamChoice.CHOICE_blackAndWhite, bitmaskParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK);
        assertEquals(8, bitmaskParamChoice.bitSizeOf());

        bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.WHITE);
        assertEquals(8, bitmaskParamChoice.bitSizeOf());

        bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        assertEquals(16, bitmaskParamChoice.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final Selector selector = Selector.Values.BLACK;
        final BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(selector);
        assertEquals(selector, bitmaskParamChoice.getSelector());
    }

    @Test
    public void getSetBlack()
    {
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK);
        final byte value = 99;
        bitmaskParamChoice.setBlack(value);
        assertEquals(value, bitmaskParamChoice.getBlack());
    }

    @Test
    public void getSetWhite()
    {
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.WHITE);
        final short value = 234;
        bitmaskParamChoice.setWhite(value);
        assertEquals(value, bitmaskParamChoice.getWhite());
    }

    @Test
    public void getSetBlackAndWhite()
    {
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        final int value = 65535;
        bitmaskParamChoice.setBlackAndWhite(value);
        assertEquals(value, bitmaskParamChoice.getBlackAndWhite());
    }

    @Test
    public void equals()
    {
        BitmaskParamChoice bitmaskParamChoice1 = new BitmaskParamChoice(Selector.Values.BLACK);
        BitmaskParamChoice bitmaskParamChoice2 = new BitmaskParamChoice(Selector.Values.BLACK);
        assertTrue(bitmaskParamChoice1.equals(bitmaskParamChoice2));

        final byte value = 99;
        bitmaskParamChoice1.setBlack(value);
        assertFalse(bitmaskParamChoice1.equals(bitmaskParamChoice2));

        bitmaskParamChoice2.setBlack(value);
        assertTrue(bitmaskParamChoice1.equals(bitmaskParamChoice2));

        final byte diffValue = value + 1;
        bitmaskParamChoice2.setBlack(diffValue);
        assertFalse(bitmaskParamChoice1.equals(bitmaskParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        BitmaskParamChoice bitmaskParamChoice1 = new BitmaskParamChoice(Selector.Values.BLACK);
        BitmaskParamChoice bitmaskParamChoice2 = new BitmaskParamChoice(Selector.Values.BLACK);
        assertEquals(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

        final byte value = 99;
        bitmaskParamChoice1.setBlack(value);
        assertTrue(bitmaskParamChoice1.hashCode() != bitmaskParamChoice2.hashCode());

        bitmaskParamChoice2.setBlack(value);
        assertEquals(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

        final byte diffValue = value + 1;
        bitmaskParamChoice2.setBlack(diffValue);
        assertTrue(bitmaskParamChoice1.hashCode() != bitmaskParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(63110, bitmaskParamChoice1.hashCode());
        assertEquals(63111, bitmaskParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK);
        final int bitPosition = 1;
        assertEquals(9, bitmaskParamChoice.initializeOffsets(bitPosition));

        bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.WHITE);
        assertEquals(9, bitmaskParamChoice.initializeOffsets(bitPosition));

        bitmaskParamChoice = new BitmaskParamChoice(Selector.Values.BLACK_AND_WHITE);
        assertEquals(17, bitmaskParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.Values.BLACK;
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(selector);
        final byte byteValue = 99;
        bitmaskParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        bitmaskParamChoice.write(file);
        BitmaskParamChoice readBitmaskParamChoice = new BitmaskParamChoice(file, selector);
        assertEquals(byteValue, readBitmaskParamChoice.getBlack());

        selector = Selector.Values.WHITE;
        bitmaskParamChoice = new BitmaskParamChoice(selector);
        final short shortValue = 234;
        bitmaskParamChoice.setWhite(shortValue);
        bitmaskParamChoice.write(file);
        readBitmaskParamChoice = new BitmaskParamChoice(file, selector);
        assertEquals(shortValue, readBitmaskParamChoice.getWhite());

        selector = Selector.Values.BLACK_AND_WHITE;
        bitmaskParamChoice = new BitmaskParamChoice(selector);
        final int intValue = 65535;
        bitmaskParamChoice.setBlackAndWhite(intValue);
        bitmaskParamChoice.write(file);
        readBitmaskParamChoice = new BitmaskParamChoice(file, selector);
        assertEquals(intValue, readBitmaskParamChoice.getBlackAndWhite());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        Selector selector = Selector.Values.BLACK;
        BitmaskParamChoice bitmaskParamChoice = new BitmaskParamChoice(selector);
        final byte byteValue = 99;
        bitmaskParamChoice.setBlack(byteValue);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        bitmaskParamChoice.write(writer);
        writer.close();
        BitmaskParamChoice readBitmaskParamChoice = new BitmaskParamChoice(file, selector);
        assertEquals(byteValue, readBitmaskParamChoice.getBlack());

        selector = Selector.Values.WHITE;
        bitmaskParamChoice = new BitmaskParamChoice(selector);
        final short shortValue = 234;
        bitmaskParamChoice.setWhite(shortValue);
        writer = new FileBitStreamWriter(file);
        bitmaskParamChoice.write(writer);
        writer.close();
        readBitmaskParamChoice = new BitmaskParamChoice(file, selector);
        assertEquals(shortValue, readBitmaskParamChoice.getWhite());

        selector = Selector.Values.BLACK_AND_WHITE;
        bitmaskParamChoice = new BitmaskParamChoice(selector);
        final int intValue = 65535;
        bitmaskParamChoice.setBlackAndWhite(intValue);
        writer = new FileBitStreamWriter(file);
        bitmaskParamChoice.write(writer);
        writer.close();
        readBitmaskParamChoice = new BitmaskParamChoice(file, selector);
        assertEquals(intValue, readBitmaskParamChoice.getBlackAndWhite());
    }

    private void writeBitmaskParamChoiceToFile(File file, Selector selector, int value) throws IOException
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
