package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import choice_types.uint16_param_choice.UInt16ParamChoice;

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
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final int selector = VARIANT_B_SELECTOR1;
        final int value = 234;
        final BitBuffer buffer = writeUInt16ParamChoiceToBitBuffer(selector, value);
        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(buffer.getBuffer(), buffer.getBitSize());
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(reader, selector);
        assertEquals(selector, uint16ParamChoice.getSelector());
        assertEquals((short)value, uint16ParamChoice.getValueB());
    }

    @Test
    public void choiceTag()
    {
        UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(UInt16ParamChoice.CHOICE_valueA, uint16ParamChoice.choiceTag());

        uint16ParamChoice = new UInt16ParamChoice(VARIANT_B_SELECTOR1);
        assertEquals(UInt16ParamChoice.CHOICE_valueB, uint16ParamChoice.choiceTag());

        uint16ParamChoice = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        assertEquals(UInt16ParamChoice.CHOICE_valueC, uint16ParamChoice.choiceTag());

        uint16ParamChoice = new UInt16ParamChoice(EMPTY_SELECTOR1);
        assertEquals(UInt16ParamChoice.UNDEFINED_CHOICE, uint16ParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        final UInt16ParamChoice uint16ParamChoiceA = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint16ParamChoiceA.setValueA(byteValueA);
        assertEquals(8, uint16ParamChoiceA.bitSizeOf());

        final UInt16ParamChoice uint16ParamChoiceB = new UInt16ParamChoice(VARIANT_B_SELECTOR2);
        final short shortValueB = 234;
        uint16ParamChoiceB.setValueB(shortValueB);
        assertEquals(16, uint16ParamChoiceB.bitSizeOf());

        final UInt16ParamChoice uint16ParamChoiceEmpty = new UInt16ParamChoice(EMPTY_SELECTOR1);
        assertEquals(0, uint16ParamChoiceEmpty.bitSizeOf());

        final UInt16ParamChoice uint16ParamChoiceC = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint16ParamChoiceC.setValueC(intValueC);
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
        uint16ParamChoice.setValueA(value);
        assertEquals(value, uint16ParamChoice.getValueA());
    }

    @Test
    public void getSetB()
    {
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(VARIANT_B_SELECTOR3);
        final short value = 234;
        uint16ParamChoice.setValueB(value);
        assertEquals(value, uint16ParamChoice.getValueB());
    }

    @Test
    public void getSetC()
    {
        final UInt16ParamChoice uint16ParamChoice = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        final int value = 65535;
        uint16ParamChoice.setValueC(value);
        assertEquals(value, uint16ParamChoice.getValueC());
    }

    @Test
    public void equals()
    {
        final UInt16ParamChoice uint16ParamChoice1 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final UInt16ParamChoice uint16ParamChoice2 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        assertTrue(uint16ParamChoice1.equals(uint16ParamChoice2));

        final byte value = 99;
        uint16ParamChoice1.setValueA(value);
        assertFalse(uint16ParamChoice1.equals(uint16ParamChoice2));

        uint16ParamChoice2.setValueA(value);
        assertTrue(uint16ParamChoice1.equals(uint16ParamChoice2));

        final byte diffValue = value + 1;
        uint16ParamChoice2.setValueA(diffValue);
        assertFalse(uint16ParamChoice1.equals(uint16ParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        final UInt16ParamChoice uint16ParamChoice1 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final UInt16ParamChoice uint16ParamChoice2 = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());

        final byte value = 99;
        uint16ParamChoice1.setValueA(value);
        assertTrue(uint16ParamChoice1.hashCode() != uint16ParamChoice2.hashCode());

        uint16ParamChoice2.setValueA(value);
        assertEquals(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());

        final byte diffValue = value + 1;
        uint16ParamChoice2.setValueA(diffValue);
        assertTrue(uint16ParamChoice1.hashCode() != uint16ParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(31623, uint16ParamChoice1.hashCode());
        assertEquals(31624, uint16ParamChoice2.hashCode());
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
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final UInt16ParamChoice uint16ParamChoiceA = new UInt16ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint16ParamChoiceA.setValueA(byteValueA);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint16ParamChoiceA.write(writer);
        ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final UInt16ParamChoice readUInt16ParamChoiceA = new UInt16ParamChoice(reader, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readUInt16ParamChoiceA.getValueA());

        final UInt16ParamChoice uint16ParamChoiceB = new UInt16ParamChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        uint16ParamChoiceB.setValueB(shortValueB);
        writer = new ByteArrayBitStreamWriter();
        uint16ParamChoiceB.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final UInt16ParamChoice readUInt16ParamChoiceB = new UInt16ParamChoice(reader, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readUInt16ParamChoiceB.getValueB());

        final UInt16ParamChoice uint16ParamChoiceC = new UInt16ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint16ParamChoiceC.setValueC(intValueC);
        writer = new ByteArrayBitStreamWriter();
        uint16ParamChoiceC.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final UInt16ParamChoice readUInt16ParamChoiceC = new UInt16ParamChoice(reader, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readUInt16ParamChoiceC.getValueC());
    }

    private BitBuffer writeUInt16ParamChoiceToBitBuffer(int selector, int value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            switch (selector)
            {
            case 1:
                writer.writeByte((byte)value);
                break;

            case 2:
            case 3:
            case 4:
                writer.writeShort((short)value);
                break;

            case 5:
            case 6:
                break;

            default:
                writer.writeInt(value);
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static int VARIANT_A_SELECTOR = 1;
    private static int VARIANT_B_SELECTOR1 = 2;
    private static int VARIANT_B_SELECTOR2 = 3;
    private static int VARIANT_B_SELECTOR3 = 4;
    private static int EMPTY_SELECTOR1 = 5;
    private static int EMPTY_SELECTOR2 = 6;
    private static int VARIANT_C_SELECTOR = 7;
}
