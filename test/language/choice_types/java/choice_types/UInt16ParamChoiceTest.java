package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import choice_types.uint16_param_choice.UInt16ParamChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class UInt16ParamChoiceTest
{
    @Test
    public void containerConstructor()
    {
        final int selector = VARIANT_A_SELECTOR;
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(selector);
        assertEquals(selector, uint16ParamChoice.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final int selector = VARIANT_A_SELECTOR;
        final File file = new File("test.bin");
        final int value = 99;
        writeUInt16ParamChoiceToFile(file, selector, value);
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(file, selector);
        assertEquals(selector, uint16ParamChoice.getSelector());
        assertEquals((byte)value, uint16ParamChoice.getA());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final int selector = VARIANT_B_SELECTOR1;
        final File file = new File("test.bin");
        final int value = 234;
        writeUInt16ParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(stream, selector);
        stream.close();
        assertEquals(selector, uint16ParamChoice.getSelector());
        assertEquals((short)value, uint16ParamChoice.getB());
    }

    @Test
    public void bitSizeOf()
    {
        final UInt16ParamChoice uint16ParamChoiceA = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint16ParamChoiceA.setA(byteValueA);
        assertEquals(8, uint16ParamChoiceA.bitSizeOf());

        final UInt16ParamChoice uint16ParamChoiceB = new UInt16ParamChoice(VARIANT_B_SELECTOR2);
        final short shortValueB = 234;
        uint16ParamChoiceB.setB(shortValueB);
        assertEquals(16, uint16ParamChoiceB.bitSizeOf());

        final UInt16ParamChoice uint16ParamChoiceEmpty = new UInt16ParamChoice(EMPTY_SELECTOR1);
        assertEquals(0, uint16ParamChoiceEmpty.bitSizeOf());

        final UInt16ParamChoice uint16ParamChoiceC = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint16ParamChoiceC.setC(intValueC);
        assertEquals(32, uint16ParamChoiceC.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final int selector = EMPTY_SELECTOR2;
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(selector);
        assertEquals(selector, uint16ParamChoice.getSelector());
    }

    @Test
    public void getSetA()
    {
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final byte value = 99;
        uint16ParamChoice.setA(value);
        assertEquals(value, uint16ParamChoice.getA());
    }

    @Test
    public void getSetB()
    {
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(VARIANT_B_SELECTOR3);
        final short value = 234;
        uint16ParamChoice.setB(value);
        assertEquals(value, uint16ParamChoice.getB());
    }

    @Test
    public void getSetC()
    {
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        final int value = 65535;
        uint16ParamChoice.setC(value);
        assertEquals(value, uint16ParamChoice.getC());
    }

    @Test
    public void equals()
    {
        final UInt16ParamChoice uint16ParamChoice1 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final UInt16ParamChoice uint16ParamChoice2 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        assertTrue(uint16ParamChoice1.equals(uint16ParamChoice2));

        final byte value = 99;
        uint16ParamChoice1.setA(value);
        assertFalse(uint16ParamChoice1.equals(uint16ParamChoice2));

        uint16ParamChoice2.setA(value);
        assertTrue(uint16ParamChoice1.equals(uint16ParamChoice2));

        final byte diffValue = value + 1;
        uint16ParamChoice2.setA(diffValue);
        assertFalse(uint16ParamChoice1.equals(uint16ParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        final UInt16ParamChoice uint16ParamChoice1 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final UInt16ParamChoice uint16ParamChoice2 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());

        final byte value = 99;
        uint16ParamChoice1.setA(value);
        assertTrue(uint16ParamChoice1.hashCode() != uint16ParamChoice2.hashCode());

        uint16ParamChoice2.setA(value);
        assertEquals(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());

        final byte diffValue = value + 1;
        uint16ParamChoice2.setA(diffValue);
        assertTrue(uint16ParamChoice1.hashCode() != uint16ParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;
        final UInt16ParamChoice uint16ParamChoiceA = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(9, uint16ParamChoiceA.initializeOffsets(bitPosition));

        final UInt16ParamChoice uint16ParamChoiceB = new UInt16ParamChoice(VARIANT_B_SELECTOR2);
        assertEquals(17, uint16ParamChoiceB.initializeOffsets(bitPosition));

        final UInt16ParamChoice uint16ParamChoiceEmpty = new UInt16ParamChoice(EMPTY_SELECTOR1);
        assertEquals(1, uint16ParamChoiceEmpty.initializeOffsets(bitPosition));

        final UInt16ParamChoice uint16ParamChoiceC = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        assertEquals(33, uint16ParamChoiceC.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        final UInt16ParamChoice uint16ParamChoiceA = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint16ParamChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        uint16ParamChoiceA.write(file);
        final UInt16ParamChoice readUInt16ParamChoiceA = new UInt16ParamChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readUInt16ParamChoiceA.getA());

        final UInt16ParamChoice uint16ParamChoiceB = new UInt16ParamChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        uint16ParamChoiceB.setB(shortValueB);
        uint16ParamChoiceB.write(file);
        final UInt16ParamChoice readUInt16ParamChoiceB = new UInt16ParamChoice(file, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readUInt16ParamChoiceB.getB());

        final UInt16ParamChoice uint16ParamChoiceC = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint16ParamChoiceC.setC(intValueC);
        uint16ParamChoiceC.write(file);
        final UInt16ParamChoice readUInt16ParamChoiceC = new UInt16ParamChoice(file, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readUInt16ParamChoiceC.getC());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final UInt16ParamChoice uint16ParamChoiceA = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint16ParamChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        uint16ParamChoiceA.write(writer);
        writer.close();
        final UInt16ParamChoice readUInt16ParamChoiceA = new UInt16ParamChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readUInt16ParamChoiceA.getA());

        final UInt16ParamChoice uint16ParamChoiceB = new UInt16ParamChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        uint16ParamChoiceB.setB(shortValueB);
        writer = new FileBitStreamWriter(file);
        uint16ParamChoiceB.write(writer);
        writer.close();
        final UInt16ParamChoice readUInt16ParamChoiceB = new UInt16ParamChoice(file, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readUInt16ParamChoiceB.getB());

        final UInt16ParamChoice uint16ParamChoiceC = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint16ParamChoiceC.setC(intValueC);
        writer = new FileBitStreamWriter(file);
        uint16ParamChoiceC.write(writer);
        writer.close();
        final UInt16ParamChoice readUInt16ParamChoiceC = new UInt16ParamChoice(file, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readUInt16ParamChoiceC.getC());
    }

    private void writeUInt16ParamChoiceToFile(File file, int selector, int value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        switch (selector)
        {
        case 1:
            stream.writeByte(value);
            break;

        case 2:
        case 3:
        case 4:
            stream.writeShort(value);
            break;

        case 5:
        case 6:
            break;

        default:
            stream.writeInt(value);
        }

        stream.close();
    }

    private static int VARIANT_A_SELECTOR = 1;
    private static int VARIANT_B_SELECTOR1 = 2;
    private static int VARIANT_B_SELECTOR2 = 3;
    private static int VARIANT_B_SELECTOR3 = 4;
    private static int EMPTY_SELECTOR1 = 5;
    private static int EMPTY_SELECTOR2 = 6;
    private static int VARIANT_C_SELECTOR = 7;
}
