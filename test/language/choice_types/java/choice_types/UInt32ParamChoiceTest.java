package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import choice_types.uint32_param_choice.UInt32ParamChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class UInt32ParamChoiceTest
{
    @Test
    public void containerConstructor()
    {
        final long selector = VARIANT_A_SELECTOR;
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(selector);
        assertEquals(selector, uint32ParamChoice.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final long selector = VARIANT_A_SELECTOR;
        final File file = new File("test.bin");
        final int value = 99;
        writeUInt32ParamChoiceToFile(file, selector, value);
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(file, selector);
        assertEquals(selector, uint32ParamChoice.getSelector());
        assertEquals((byte)value, uint32ParamChoice.getA());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final long selector = VARIANT_B_SELECTOR1;
        final File file = new File("test.bin");
        final int value = 234;
        writeUInt32ParamChoiceToFile(file, selector, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(stream, selector);
        stream.close();
        assertEquals(selector, uint32ParamChoice.getSelector());
        assertEquals((short)value, uint32ParamChoice.getB());
    }

    @Test
    public void choiceTag()
    {
        UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(UInt32ParamChoice.CHOICE_a, uint32ParamChoice.choiceTag());

        uint32ParamChoice = new UInt32ParamChoice(VARIANT_B_SELECTOR1);
        assertEquals(UInt32ParamChoice.CHOICE_b, uint32ParamChoice.choiceTag());

        uint32ParamChoice = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        assertEquals(UInt32ParamChoice.CHOICE_c, uint32ParamChoice.choiceTag());

        uint32ParamChoice = new UInt32ParamChoice(EMPTY_SELECTOR1);
        assertEquals(UInt32ParamChoice.UNDEFINED_CHOICE, uint32ParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        final UInt32ParamChoice uint32ParamChoiceA = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint32ParamChoiceA.setA(byteValueA);
        assertEquals(8, uint32ParamChoiceA.bitSizeOf());

        final UInt32ParamChoice uint32ParamChoiceB = new UInt32ParamChoice(VARIANT_B_SELECTOR2);
        final short shortValueB = 234;
        uint32ParamChoiceB.setB(shortValueB);
        assertEquals(16, uint32ParamChoiceB.bitSizeOf());

        final UInt32ParamChoice uint32ParamChoiceEmpty = new UInt32ParamChoice(EMPTY_SELECTOR1);
        assertEquals(0, uint32ParamChoiceEmpty.bitSizeOf());

        final UInt32ParamChoice uint32ParamChoiceC = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint32ParamChoiceC.setC(intValueC);
        assertEquals(32, uint32ParamChoiceC.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final long selector = EMPTY_SELECTOR2;
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(selector);
        assertEquals(selector, uint32ParamChoice.getSelector());
    }

    @Test
    public void getSetA()
    {
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final byte value = 99;
        uint32ParamChoice.setA(value);
        assertEquals(value, uint32ParamChoice.getA());
    }

    @Test
    public void getSetB()
    {
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(VARIANT_B_SELECTOR3);
        final short value = 234;
        uint32ParamChoice.setB(value);
        assertEquals(value, uint32ParamChoice.getB());
    }

    @Test
    public void getSetC()
    {
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        final int value = 65535;
        uint32ParamChoice.setC(value);
        assertEquals(value, uint32ParamChoice.getC());
    }

    @Test
    public void equals()
    {
        final UInt32ParamChoice uint32ParamChoice1 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final UInt32ParamChoice uint32ParamChoice2 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        assertTrue(uint32ParamChoice1.equals(uint32ParamChoice2));

        final byte value = 99;
        uint32ParamChoice1.setA(value);
        assertFalse(uint32ParamChoice1.equals(uint32ParamChoice2));

        uint32ParamChoice2.setA(value);
        assertTrue(uint32ParamChoice1.equals(uint32ParamChoice2));

        final byte diffValue = value + 1;
        uint32ParamChoice2.setA(diffValue);
        assertFalse(uint32ParamChoice1.equals(uint32ParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        final UInt32ParamChoice uint32ParamChoice1 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final UInt32ParamChoice uint32ParamChoice2 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

        final byte value = 99;
        uint32ParamChoice1.setA(value);
        assertTrue(uint32ParamChoice1.hashCode() != uint32ParamChoice2.hashCode());

        uint32ParamChoice2.setA(value);
        assertEquals(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

        final byte diffValue = value + 1;
        uint32ParamChoice2.setA(diffValue);
        assertTrue(uint32ParamChoice1.hashCode() != uint32ParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(31623, uint32ParamChoice1.hashCode());
        assertEquals(31624, uint32ParamChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;
        final UInt32ParamChoice uint32ParamChoiceA = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(9, uint32ParamChoiceA.initializeOffsets(bitPosition));

        final UInt32ParamChoice uint32ParamChoiceB = new UInt32ParamChoice(VARIANT_B_SELECTOR2);
        assertEquals(17, uint32ParamChoiceB.initializeOffsets(bitPosition));

        final UInt32ParamChoice uint32ParamChoiceEmpty = new UInt32ParamChoice(EMPTY_SELECTOR1);
        assertEquals(1, uint32ParamChoiceEmpty.initializeOffsets(bitPosition));

        final UInt32ParamChoice uint32ParamChoiceC = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        assertEquals(33, uint32ParamChoiceC.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        final UInt32ParamChoice uint32ParamChoiceA = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint32ParamChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        uint32ParamChoiceA.write(file);
        final UInt32ParamChoice readUInt32ParamChoiceA = new UInt32ParamChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readUInt32ParamChoiceA.getA());

        final UInt32ParamChoice uint32ParamChoiceB = new UInt32ParamChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        uint32ParamChoiceB.setB(shortValueB);
        uint32ParamChoiceB.write(file);
        final UInt32ParamChoice readUInt32ParamChoiceB = new UInt32ParamChoice(file, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readUInt32ParamChoiceB.getB());

        final UInt32ParamChoice uint32ParamChoiceC = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint32ParamChoiceC.setC(intValueC);
        uint32ParamChoiceC.write(file);
        final UInt32ParamChoice readUInt32ParamChoiceC = new UInt32ParamChoice(file, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readUInt32ParamChoiceC.getC());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final UInt32ParamChoice uint32ParamChoiceA = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint32ParamChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        uint32ParamChoiceA.write(writer);
        writer.close();
        final UInt32ParamChoice readUInt32ParamChoiceA = new UInt32ParamChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readUInt32ParamChoiceA.getA());

        final UInt32ParamChoice uint32ParamChoiceB = new UInt32ParamChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        uint32ParamChoiceB.setB(shortValueB);
        writer = new FileBitStreamWriter(file);
        uint32ParamChoiceB.write(writer);
        writer.close();
        final UInt32ParamChoice readUInt32ParamChoiceB = new UInt32ParamChoice(file, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readUInt32ParamChoiceB.getB());

        final UInt32ParamChoice uint32ParamChoiceC = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint32ParamChoiceC.setC(intValueC);
        writer = new FileBitStreamWriter(file);
        uint32ParamChoiceC.write(writer);
        writer.close();
        final UInt32ParamChoice readUInt32ParamChoiceC = new UInt32ParamChoice(file, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readUInt32ParamChoiceC.getC());
    }

    private void writeUInt32ParamChoiceToFile(File file, long selector, int value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        if (selector == 1)
        {
            stream.writeByte(value);
        }
        else if (selector == 2 || selector == 3 || selector == 4)
        {
            stream.writeShort(value);
        }
        else if (selector != 5 && selector != 6)
        {
            stream.writeInt(value);
        }

        stream.close();
    }

    private static long VARIANT_A_SELECTOR = 1;
    private static long VARIANT_B_SELECTOR1 = 2;
    private static long VARIANT_B_SELECTOR2 = 3;
    private static long VARIANT_B_SELECTOR3 = 4;
    private static long EMPTY_SELECTOR1 = 5;
    private static long EMPTY_SELECTOR2 = 6;
    private static long VARIANT_C_SELECTOR = 7;
}
