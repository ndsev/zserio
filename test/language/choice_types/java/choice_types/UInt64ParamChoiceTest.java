package choice_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import choice_types.uint64_param_choice.UInt64ParamChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class UInt64ParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(VARIANT_A_SELECTOR, uint64ParamChoice.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final byte value = 99;
        writeUInt64ParamChoiceToFile(file, VARIANT_A_SELECTOR, value);
        final UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(file, VARIANT_A_SELECTOR);
        assertEquals(VARIANT_A_SELECTOR, uint64ParamChoice.getSelector());
        assertEquals(value, uint64ParamChoice.getA());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final short value = 234;
        writeUInt64ParamChoiceToFile(file, VARIANT_B_SELECTOR, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(stream, VARIANT_B_SELECTOR);
        stream.close();
        assertEquals(VARIANT_B_SELECTOR, uint64ParamChoice.getSelector());
        assertEquals(value, uint64ParamChoice.getB());
    }

    @Test
    public void bitSizeOf()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(8, uint64ParamChoice.bitSizeOf());

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        assertEquals(16, uint64ParamChoice.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_C_SELECTOR);
        assertEquals(VARIANT_C_SELECTOR, uint64ParamChoice.getSelector());
    }

    @Test
    public void getSetA()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        final byte value = 99;
        uint64ParamChoice.setA(value);
        assertEquals(value, uint64ParamChoice.getA());
    }

    @Test
    public void getSetB()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        final short value = 234;
        uint64ParamChoice.setB(value);
        assertEquals(value, uint64ParamChoice.getB());
    }

    @Test
    public void getSetC()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_C_SELECTOR);
        final int value = 23456;
        uint64ParamChoice.setC(value);
        assertEquals(value, uint64ParamChoice.getC());
    }

    @Test
    public void equals()
    {
        UInt64ParamChoice uint64ParamChoice1 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        UInt64ParamChoice uint64ParamChoice2 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertTrue(uint64ParamChoice1.equals(uint64ParamChoice2));

        final byte value = 99;
        uint64ParamChoice1.setA(value);
        assertFalse(uint64ParamChoice1.equals(uint64ParamChoice2));

        uint64ParamChoice2.setA(value);
        assertTrue(uint64ParamChoice1.equals(uint64ParamChoice2));

        final byte diffValue = value + 1;
        uint64ParamChoice2.setA(diffValue);
        assertFalse(uint64ParamChoice1.equals(uint64ParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        UInt64ParamChoice uint64ParamChoice1 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        UInt64ParamChoice uint64ParamChoice2 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

        final byte value = 99;
        uint64ParamChoice1.setA(value);
        assertTrue(uint64ParamChoice1.hashCode() != uint64ParamChoice2.hashCode());

        uint64ParamChoice2.setA(value);
        assertEquals(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

        final byte diffValue = value + 1;
        uint64ParamChoice2.setA(diffValue);
        assertTrue(uint64ParamChoice1.hashCode() != uint64ParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        final int bitPosition = 1;
        assertEquals(9, uint64ParamChoice.initializeOffsets(bitPosition));

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        assertEquals(17, uint64ParamChoice.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValue = 99;
        uint64ParamChoice.setA(byteValue);
        final File file = new File("test.bin");
        uint64ParamChoice.write(file);
        UInt64ParamChoice readUInt64ParamChoice = new UInt64ParamChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValue, readUInt64ParamChoice.getA());

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        final short shortValue = 234;
        uint64ParamChoice.setB(shortValue);
        uint64ParamChoice.write(file);
        readUInt64ParamChoice = new UInt64ParamChoice(file, VARIANT_B_SELECTOR);
        assertEquals(shortValue, readUInt64ParamChoice.getB());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValue = 99;
        uint64ParamChoice.setA(byteValue);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        uint64ParamChoice.write(writer);
        writer.close();
        UInt64ParamChoice readUInt64ParamChoice = new UInt64ParamChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValue, readUInt64ParamChoice.getA());

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        final short shortValue = 234;
        uint64ParamChoice.setB(shortValue);
        writer = new FileBitStreamWriter(file);
        uint64ParamChoice.write(writer);
        writer.close();
        readUInt64ParamChoice = new UInt64ParamChoice(file, VARIANT_B_SELECTOR);
        assertEquals(shortValue, readUInt64ParamChoice.getB());
    }

    private void writeUInt64ParamChoiceToFile(File file, BigInteger selector, int value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        if (selector.compareTo(new BigInteger("1")) == 0)
            stream.writeByte(value);
        else if (selector.compareTo(new BigInteger("2")) == 0 || selector.compareTo(new BigInteger("3")) == 0 ||
                 selector.compareTo(new BigInteger("4")) == 0)
            stream.writeShort(value);
        else if (selector.compareTo(new BigInteger("5")) == 0 || selector.compareTo(new BigInteger("6")) == 0)
            ;
        else
            stream.writeInt(value);

        stream.close();
    }

    private final static BigInteger VARIANT_A_SELECTOR = BigInteger.ONE;
    private final static BigInteger VARIANT_B_SELECTOR = new BigInteger("2");
    private final static BigInteger VARIANT_C_SELECTOR = new BigInteger("7");
}
