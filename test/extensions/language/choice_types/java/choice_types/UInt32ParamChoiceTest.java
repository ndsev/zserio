package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import choice_types.uint32_param_choice.UInt32ParamChoice;

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
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final long selector = VARIANT_B_SELECTOR1;
        final int value = 234;
        final BitBuffer buffer = writeUInt32ParamChoiceToBitBuffer(selector, value);
        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(buffer.getBuffer(), buffer.getBitSize());
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(reader, selector);
        assertEquals(selector, uint32ParamChoice.getSelector());
        assertEquals((short)value, uint32ParamChoice.getValueB());
    }

    @Test
    public void choiceTag()
    {
        UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(UInt32ParamChoice.CHOICE_valueA, uint32ParamChoice.choiceTag());

        uint32ParamChoice = new UInt32ParamChoice(VARIANT_B_SELECTOR1);
        assertEquals(UInt32ParamChoice.CHOICE_valueB, uint32ParamChoice.choiceTag());

        uint32ParamChoice = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        assertEquals(UInt32ParamChoice.CHOICE_valueC, uint32ParamChoice.choiceTag());

        uint32ParamChoice = new UInt32ParamChoice(EMPTY_SELECTOR1);
        assertEquals(UInt32ParamChoice.UNDEFINED_CHOICE, uint32ParamChoice.choiceTag());
    }

    @Test
    public void bitSizeOf()
    {
        final UInt32ParamChoice uint32ParamChoiceA = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint32ParamChoiceA.setValueA(byteValueA);
        assertEquals(8, uint32ParamChoiceA.bitSizeOf());

        final UInt32ParamChoice uint32ParamChoiceB = new UInt32ParamChoice(VARIANT_B_SELECTOR2);
        final short shortValueB = 234;
        uint32ParamChoiceB.setValueB(shortValueB);
        assertEquals(16, uint32ParamChoiceB.bitSizeOf());

        final UInt32ParamChoice uint32ParamChoiceEmpty = new UInt32ParamChoice(EMPTY_SELECTOR1);
        assertEquals(0, uint32ParamChoiceEmpty.bitSizeOf());

        final UInt32ParamChoice uint32ParamChoiceC = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint32ParamChoiceC.setValueC(intValueC);
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
        uint32ParamChoice.setValueA(value);
        assertEquals(value, uint32ParamChoice.getValueA());
    }

    @Test
    public void getSetB()
    {
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(VARIANT_B_SELECTOR3);
        final short value = 234;
        uint32ParamChoice.setValueB(value);
        assertEquals(value, uint32ParamChoice.getValueB());
    }

    @Test
    public void getSetC()
    {
        final UInt32ParamChoice uint32ParamChoice = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        final int value = 65535;
        uint32ParamChoice.setValueC(value);
        assertEquals(value, uint32ParamChoice.getValueC());
    }

    @Test
    public void equals()
    {
        final UInt32ParamChoice uint32ParamChoice1 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final UInt32ParamChoice uint32ParamChoice2 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        assertTrue(uint32ParamChoice1.equals(uint32ParamChoice2));

        final byte value = 99;
        uint32ParamChoice1.setValueA(value);
        assertFalse(uint32ParamChoice1.equals(uint32ParamChoice2));

        uint32ParamChoice2.setValueA(value);
        assertTrue(uint32ParamChoice1.equals(uint32ParamChoice2));

        final byte diffValue = value + 1;
        uint32ParamChoice2.setValueA(diffValue);
        assertFalse(uint32ParamChoice1.equals(uint32ParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        final UInt32ParamChoice uint32ParamChoice1 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final UInt32ParamChoice uint32ParamChoice2 = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

        final byte value = 99;
        uint32ParamChoice1.setValueA(value);
        assertTrue(uint32ParamChoice1.hashCode() != uint32ParamChoice2.hashCode());

        uint32ParamChoice2.setValueA(value);
        assertEquals(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

        final byte diffValue = value + 1;
        uint32ParamChoice2.setValueA(diffValue);
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
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final UInt32ParamChoice uint32ParamChoiceA = new UInt32ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValueA = 99;
        uint32ParamChoiceA.setValueA(byteValueA);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint32ParamChoiceA.write(writer);
        ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final UInt32ParamChoice readUInt32ParamChoiceA = new UInt32ParamChoice(reader, VARIANT_A_SELECTOR);
        assertEquals(byteValueA, readUInt32ParamChoiceA.getValueA());

        final UInt32ParamChoice uint32ParamChoiceB = new UInt32ParamChoice(VARIANT_B_SELECTOR1);
        final short shortValueB = 234;
        uint32ParamChoiceB.setValueB(shortValueB);
        writer = new ByteArrayBitStreamWriter();
        uint32ParamChoiceB.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final UInt32ParamChoice readUInt32ParamChoiceB = new UInt32ParamChoice(reader, VARIANT_B_SELECTOR1);
        assertEquals(shortValueB, readUInt32ParamChoiceB.getValueB());

        final UInt32ParamChoice uint32ParamChoiceC = new UInt32ParamChoice(VARIANT_C_SELECTOR);
        final int intValueC = 65535;
        uint32ParamChoiceC.setValueC(intValueC);
        writer = new ByteArrayBitStreamWriter();
        uint32ParamChoiceC.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final UInt32ParamChoice readUInt32ParamChoiceC = new UInt32ParamChoice(reader, VARIANT_C_SELECTOR);
        assertEquals(intValueC, readUInt32ParamChoiceC.getValueC());
    }

    private BitBuffer writeUInt32ParamChoiceToBitBuffer(long selector, int value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            if (selector == 1)
            {
                writer.writeByte((byte)value);
            }
            else if (selector == 2 || selector == 3 || selector == 4)
            {
                writer.writeShort((short)value);
            }
            else if (selector != 5 && selector != 6)
            {
                writer.writeInt(value);
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static long VARIANT_A_SELECTOR = 1;
    private static long VARIANT_B_SELECTOR1 = 2;
    private static long VARIANT_B_SELECTOR2 = 3;
    private static long VARIANT_B_SELECTOR3 = 4;
    private static long EMPTY_SELECTOR1 = 5;
    private static long EMPTY_SELECTOR2 = 6;
    private static long VARIANT_C_SELECTOR = 7;
}
