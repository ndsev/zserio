package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import choice_types.uint64_param_choice.UInt64ParamChoice;

public class UInt64ParamChoiceTest
{
    @Test
    public void selectorConstructor()
    {
        final UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(VARIANT_A_SELECTOR, uint64ParamChoice.getSelector());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final short value = 234;
        final BitBuffer buffer = writeUInt64ParamChoiceToBitBuffer(VARIANT_B_SELECTOR, value);
        final ByteArrayBitStreamReader stream =
                new ByteArrayBitStreamReader(buffer.getBuffer(), buffer.getBitSize());
        final UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(stream, VARIANT_B_SELECTOR);
        stream.close();
        assertEquals(VARIANT_B_SELECTOR, uint64ParamChoice.getSelector());
        assertEquals(value, uint64ParamChoice.getValueB());
    }

    @Test
    public void choiceTag()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(UInt64ParamChoice.CHOICE_valueA, uint64ParamChoice.choiceTag());

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        assertEquals(UInt64ParamChoice.CHOICE_valueB, uint64ParamChoice.choiceTag());

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_C_SELECTOR);
        assertEquals(UInt64ParamChoice.CHOICE_valueC, uint64ParamChoice.choiceTag());

        uint64ParamChoice = new UInt64ParamChoice(EMPTY_SELECTOR);
        assertEquals(UInt64ParamChoice.UNDEFINED_CHOICE, uint64ParamChoice.choiceTag());
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
        uint64ParamChoice.setValueA(value);
        assertEquals(value, uint64ParamChoice.getValueA());
    }

    @Test
    public void getSetB()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        final short value = 234;
        uint64ParamChoice.setValueB(value);
        assertEquals(value, uint64ParamChoice.getValueB());
    }

    @Test
    public void getSetC()
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_C_SELECTOR);
        final int value = 23456;
        uint64ParamChoice.setValueC(value);
        assertEquals(value, uint64ParamChoice.getValueC());
    }

    @Test
    public void equals()
    {
        UInt64ParamChoice uint64ParamChoice1 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        UInt64ParamChoice uint64ParamChoice2 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertTrue(uint64ParamChoice1.equals(uint64ParamChoice2));

        final byte value = 99;
        uint64ParamChoice1.setValueA(value);
        assertFalse(uint64ParamChoice1.equals(uint64ParamChoice2));

        uint64ParamChoice2.setValueA(value);
        assertTrue(uint64ParamChoice1.equals(uint64ParamChoice2));

        final byte diffValue = value + 1;
        uint64ParamChoice2.setValueA(diffValue);
        assertFalse(uint64ParamChoice1.equals(uint64ParamChoice2));
    }

    @Test
    public void hashCodeMethod()
    {
        UInt64ParamChoice uint64ParamChoice1 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        UInt64ParamChoice uint64ParamChoice2 = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        assertEquals(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

        final byte value = 99;
        uint64ParamChoice1.setValueA(value);
        assertTrue(uint64ParamChoice1.hashCode() != uint64ParamChoice2.hashCode());

        uint64ParamChoice2.setValueA(value);
        assertEquals(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

        final byte diffValue = value + 1;
        uint64ParamChoice2.setValueA(diffValue);
        assertTrue(uint64ParamChoice1.hashCode() != uint64ParamChoice2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(31623, uint64ParamChoice1.hashCode());
        assertEquals(31624, uint64ParamChoice2.hashCode());
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
    public void writeRead() throws IOException, ZserioError
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValue = 99;
        uint64ParamChoice.setValueA(byteValue);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint64ParamChoice.write(writer);
        ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        UInt64ParamChoice readUInt64ParamChoice = new UInt64ParamChoice(reader, VARIANT_A_SELECTOR);
        assertEquals(byteValue, readUInt64ParamChoice.getValueA());

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        final short shortValue = 234;
        uint64ParamChoice.setValueB(shortValue);
        writer = new ByteArrayBitStreamWriter();
        uint64ParamChoice.write(writer);
        reader = new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        readUInt64ParamChoice = new UInt64ParamChoice(reader, VARIANT_B_SELECTOR);
        assertEquals(shortValue, readUInt64ParamChoice.getValueB());
    }

    @Test
    public void writeReadFile() throws IOException, ZserioError
    {
        UInt64ParamChoice uint64ParamChoice = new UInt64ParamChoice(VARIANT_A_SELECTOR);
        final byte byteValue = 99;
        uint64ParamChoice.setValueA(byteValue);
        final File fileA = new File(BLOB_NAME_BASE + "a.blob");
        SerializeUtil.serializeToFile(uint64ParamChoice, fileA);
        UInt64ParamChoice readUInt64ParamChoice =
                SerializeUtil.deserializeFromFile(UInt64ParamChoice.class, fileA, VARIANT_A_SELECTOR);
        assertEquals(byteValue, readUInt64ParamChoice.getValueA());

        uint64ParamChoice = new UInt64ParamChoice(VARIANT_B_SELECTOR);
        final short shortValue = 234;
        uint64ParamChoice.setValueB(shortValue);
        final File fileB = new File(BLOB_NAME_BASE + "b.blob");
        SerializeUtil.serializeToFile(uint64ParamChoice, fileB);
        readUInt64ParamChoice =
                SerializeUtil.deserializeFromFile(UInt64ParamChoice.class, fileB, VARIANT_B_SELECTOR);
        assertEquals(shortValue, readUInt64ParamChoice.getValueB());
    }

    private BitBuffer writeUInt64ParamChoiceToBitBuffer(BigInteger selector, int value) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            if (selector.compareTo(new BigInteger("1")) == 0)
                writer.writeByte((byte)value);
            else if (selector.compareTo(new BigInteger("2")) == 0 ||
                    selector.compareTo(new BigInteger("3")) == 0 ||
                    selector.compareTo(new BigInteger("4")) == 0)
                writer.writeShort((short)value);
            else if (selector.compareTo(new BigInteger("5")) == 0 ||
                    selector.compareTo(new BigInteger("6")) == 0)
                ;
            else
                writer.writeInt(value);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME_BASE = "uint64_param_choice_";
    private static final BigInteger VARIANT_A_SELECTOR = BigInteger.ONE;
    private static final BigInteger VARIANT_B_SELECTOR = new BigInteger("2");
    private static final BigInteger VARIANT_C_SELECTOR = new BigInteger("7");
    private static final BigInteger EMPTY_SELECTOR = new BigInteger("5");
}
