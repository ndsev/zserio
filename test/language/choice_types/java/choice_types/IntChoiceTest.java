package choice_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import choice_types.int_choice.IntChoice;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class IntChoiceTest
{
    @Test
    public void containerConstructor()
    {
        final int tag = VARIANT_A_SELECTOR;
        final IntChoice intChoice = new IntChoice(tag);
        assertEquals(tag, intChoice.getTag());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final int tag = VARIANT_A_SELECTOR;
        final File file = new File("test.bin");
        final int value = 99;
        writeIntChoiceToFile(file, tag, value);
        final IntChoice intChoice = new IntChoice(file, tag);
        assertEquals(tag, intChoice.getTag());
        assertEquals((byte)value, intChoice.getA());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final int tag = VARIANT_B_SELECTOR1;
        final File file = new File("test.bin");
        final int value = 234;
        writeIntChoiceToFile(file, tag, value);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final IntChoice intChoice = new IntChoice(stream, tag);
        stream.close();
        assertEquals(tag, intChoice.getTag());
        assertEquals((short)value, intChoice.getB());
    }

    @Test
    public void bitSizeOf()
    {
        final IntChoice intChoiceA = new IntChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        intChoiceA.setA(byteValueA);
        assertEquals(8, intChoiceA.bitSizeOf());

        final IntChoice intChoiceB = new IntChoice(VARIANT_B_SELECTOR2);
        final short shortValueB = 234;
        intChoiceB.setB(shortValueB);
        assertEquals(16, intChoiceB.bitSizeOf());

        final IntChoice intChoiceEmpty = new IntChoice(EMPTY_SELECTOR1);
        assertEquals(0, intChoiceEmpty.bitSizeOf());

        final IntChoice intChoiceC = new IntChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        intChoiceC.setC(intValueC);
        assertEquals(32, intChoiceC.bitSizeOf());
    }

    @Test
    public void getTag()
    {
        final int tag = EMPTY_SELECTOR2;
        final IntChoice intChoice = new IntChoice(tag);
        assertEquals(tag, intChoice.getTag());
    }

    @Test
    public void getSetA()
    {
        final IntChoice intChoice = new IntChoice(VARIANT_A_SELECTOR);
        final byte value = 99;
        intChoice.setA(value);
        assertEquals(value, intChoice.getA());
    }

    @Test
    public void getSetB()
    {
        final IntChoice intChoice = new IntChoice(VARIANT_B_SELECTOR3);
        final short value = 234;
        intChoice.setB(value);
        assertEquals(value, intChoice.getB());
    }

    @Test
    public void getSetC()
    {
        final IntChoice intChoice = new IntChoice(VARIANT_C_SELECTOR);
        final int value = 65535;
        intChoice.setC(value);
        assertEquals(value, intChoice.getC());
    }

    @Test
    public void equals()
    {
        final IntChoice intChoice1 = new IntChoice(VARIANT_A_SELECTOR);
        final IntChoice intChoice2 = new IntChoice(VARIANT_A_SELECTOR);
        assertTrue(intChoice1.equals(intChoice2));

        final byte value = 99;
        intChoice1.setA(value);
        assertFalse(intChoice1.equals(intChoice2));

        intChoice2.setA(value);
        assertTrue(intChoice1.equals(intChoice2));

        final byte diffValue = value + 1;
        intChoice2.setA(diffValue);
        assertFalse(intChoice1.equals(intChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        final IntChoice intChoice1 = new IntChoice(VARIANT_A_SELECTOR);
        final IntChoice intChoice2 = new IntChoice(VARIANT_A_SELECTOR);
        assertEquals(intChoice1.hashCode(), intChoice2.hashCode());

        final byte value = 99;
        intChoice1.setA(value);
        assertTrue(intChoice1.hashCode() != intChoice2.hashCode());

        intChoice2.setA(value);
        assertEquals(intChoice1.hashCode(), intChoice2.hashCode());

        final byte diffValue = value + 1;
        intChoice2.setA(diffValue);
        assertTrue(intChoice1.hashCode() != intChoice2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;
        final IntChoice intChoiceA = new IntChoice(VARIANT_A_SELECTOR);
        assertEquals(9, intChoiceA.initializeOffsets(bitPosition));

        final IntChoice intChoiceB = new IntChoice(VARIANT_B_SELECTOR2);
        assertEquals(17, intChoiceB.initializeOffsets(bitPosition));

        final IntChoice intChoiceEmpty = new IntChoice(EMPTY_SELECTOR1);
        assertEquals(1, intChoiceEmpty.initializeOffsets(bitPosition));

        final IntChoice intChoiceC = new IntChoice(VARIANT_C_SELECTOR);
        assertEquals(33, intChoiceC.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        final IntChoice intChoiceA = new IntChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        intChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        intChoiceA.write(file);
        final IntChoice readIntChoiceA = new IntChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readIntChoiceA.getA());

        final IntChoice intChoiceB = new IntChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        intChoiceB.setB(shortValueB);
        intChoiceB.write(file);
        final IntChoice readIntChoiceB = new IntChoice(file, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readIntChoiceB.getB());

        final IntChoice intChoiceC = new IntChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        intChoiceC.setC(intValueC);
        intChoiceC.write(file);
        final IntChoice readIntChoiceC = new IntChoice(file, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readIntChoiceC.getC());
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final IntChoice intChoiceA = new IntChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        intChoiceA.setA(byteValueA);
        final File file = new File("test.bin");
        BitStreamWriter writer = new FileBitStreamWriter(file);
        intChoiceA.write(writer);
        writer.close();
        final IntChoice readIntChoiceA = new IntChoice(file, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readIntChoiceA.getA());

        final IntChoice intChoiceB = new IntChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        intChoiceB.setB(shortValueB);
        writer = new FileBitStreamWriter(file);
        intChoiceB.write(writer);
        writer.close();
        final IntChoice readIntChoiceB = new IntChoice(file, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readIntChoiceB.getB());

        final IntChoice intChoiceC = new IntChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        intChoiceC.setC(intValueC);
        writer = new FileBitStreamWriter(file);
        intChoiceC.write(writer);
        writer.close();
        final IntChoice readIntChoiceC = new IntChoice(file, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readIntChoiceC.getC());
    }

    private void writeIntChoiceToFile(File file, int tag, int value) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        switch (tag)
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
