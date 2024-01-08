package union_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import union_types.simple_union.SimpleUnion;

public class SimpleUnionTest
{
    @Test
    public void emptyConstructor()
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        assertEquals(SimpleUnion.UNDEFINED_CHOICE, simpleUnion.choiceTag());
    }

    @Test
    public void emptyConstructorBitSizeOf()
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        assertThrows(ZserioError.class, () -> simpleUnion.bitSizeOf());
    }

    @Test
    public void bitStreamReaderConstructorCase1_getCase1Field() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = bitStreamReaderConstructor(SimpleUnion.CHOICE_case1Field);
        assertEquals(CASE1_FIELD, simpleUnion.getCase1Field());
    }

    @Test
    public void bitStreamReaderConstructorCase2_getCase2Field() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = bitStreamReaderConstructor(SimpleUnion.CHOICE_case2Field);
        assertEquals(CASE2_FIELD, simpleUnion.getCase2Field());
    }

    @Test
    public void bitStreamReaderConstructorCase3_getCase3Field() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = bitStreamReaderConstructor(SimpleUnion.CHOICE_case3Field);
        assertEquals(CASE3_FIELD, simpleUnion.getCase3Field());
    }

    @Test
    public void bitStreamReaderConstructorCase4_getCase4Field() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = bitStreamReaderConstructor(SimpleUnion.CHOICE_case4Field);
        assertEquals(CASE4_FIELD, simpleUnion.getCase4Field());
    }

    @Test
    public void choiceTag()
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        assertEquals(SimpleUnion.UNDEFINED_CHOICE, simpleUnion.choiceTag());
        simpleUnion.setCase1Field(CASE1_FIELD);
        assertEquals(SimpleUnion.CHOICE_case1Field, simpleUnion.choiceTag());
        simpleUnion.setCase2Field(CASE2_FIELD);
        assertEquals(SimpleUnion.CHOICE_case2Field, simpleUnion.choiceTag());
        simpleUnion.setCase3Field(CASE3_FIELD);
        assertEquals(SimpleUnion.CHOICE_case3Field, simpleUnion.choiceTag());
        simpleUnion.setCase4Field(CASE4_FIELD);
        assertEquals(SimpleUnion.CHOICE_case4Field, simpleUnion.choiceTag());
    }

    @Test
    public void getCase1Field()
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase1Field(CASE1_FIELD);
        assertEquals(CASE1_FIELD, simpleUnion.getCase1Field());
    }

    @Test
    public void getCase2Field()
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase2Field(CASE2_FIELD);
        assertEquals(CASE2_FIELD, simpleUnion.getCase2Field());
    }

    @Test
    public void getCase3Field()
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase3Field(CASE3_FIELD);
        assertEquals(CASE3_FIELD, simpleUnion.getCase3Field());
    }

    @Test
    public void getCase4Field()
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase4Field(CASE4_FIELD);
        assertEquals(CASE4_FIELD, simpleUnion.getCase4Field());
    }

    @Test
    public void bitSizeOf()
    {
        SimpleUnion simpleUnion = new SimpleUnion();

        simpleUnion.setCase1Field(CASE1_FIELD);
        assertEquals(UNION_CASE1_BIT_SIZE, simpleUnion.bitSizeOf());

        simpleUnion.setCase2Field(CASE2_FIELD);
        assertEquals(UNION_CASE2_BIT_SIZE, simpleUnion.bitSizeOf());

        simpleUnion.setCase3Field(CASE3_FIELD);
        assertEquals(UNION_CASE3_BIT_SIZE, simpleUnion.bitSizeOf());

        simpleUnion.setCase4Field(CASE4_FIELD);
        assertEquals(UNION_CASE4_BIT_SIZE, simpleUnion.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final long bitPosition = 1;
        {
            SimpleUnion simpleUnion = new SimpleUnion();
            simpleUnion.setCase1Field(CASE1_FIELD);
            assertEquals(bitPosition + UNION_CASE1_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
        }
        {
            SimpleUnion simpleUnion = new SimpleUnion();
            simpleUnion.setCase2Field(CASE2_FIELD);
            assertEquals(bitPosition + UNION_CASE2_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
        }
        {
            SimpleUnion simpleUnion = new SimpleUnion();
            simpleUnion.setCase3Field(CASE3_FIELD);
            assertEquals(bitPosition + UNION_CASE3_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
        }
        {
            SimpleUnion simpleUnion = new SimpleUnion();
            simpleUnion.setCase4Field(CASE4_FIELD);
            assertEquals(bitPosition + UNION_CASE4_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
        }
    }

    @Test
    public void operatorEquality()
    {
        SimpleUnion simpleUnion11 = new SimpleUnion();
        SimpleUnion simpleUnion12 = new SimpleUnion();
        SimpleUnion simpleUnion13 = new SimpleUnion();
        assertTrue(simpleUnion11.equals(simpleUnion11));
        assertTrue(simpleUnion11.equals(simpleUnion12));
        simpleUnion11.setCase1Field(CASE1_FIELD);
        simpleUnion12.setCase1Field(CASE1_FIELD);
        simpleUnion13.setCase1Field((byte)(CASE1_FIELD + 1));
        assertTrue(simpleUnion11.equals(simpleUnion11));
        assertTrue(simpleUnion11.equals(simpleUnion12));
        assertFalse(simpleUnion11.equals(simpleUnion13));

        SimpleUnion simpleUnion21 = new SimpleUnion();
        simpleUnion21.setCase2Field(CASE2_FIELD);
        SimpleUnion simpleUnion22 = new SimpleUnion();
        simpleUnion22.setCase2Field(CASE2_FIELD);
        SimpleUnion simpleUnion23 = new SimpleUnion();
        simpleUnion23.setCase2Field(CASE2_FIELD - 1);
        assertTrue(simpleUnion21.equals(simpleUnion21));
        assertTrue(simpleUnion21.equals(simpleUnion22));
        assertFalse(simpleUnion21.equals(simpleUnion23));
        assertFalse(simpleUnion21.equals(simpleUnion11));

        SimpleUnion simpleUnion4 = new SimpleUnion();
        simpleUnion4.setCase4Field(CASE1_FIELD); // same value as simpleUnion11, but different choice
        assertFalse(simpleUnion11.equals(simpleUnion4));
    }

    @Test
    public void hashCodeMethod()
    {
        SimpleUnion simpleUnion1 = new SimpleUnion();
        SimpleUnion simpleUnion2 = new SimpleUnion();
        assertEquals(simpleUnion1.hashCode(), simpleUnion2.hashCode());
        simpleUnion1.setCase1Field(CASE1_FIELD);
        assertFalse(simpleUnion1.hashCode() == simpleUnion2.hashCode());
        simpleUnion2.setCase4Field(CASE4_FIELD);
        assertFalse(simpleUnion1.hashCode() == simpleUnion2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(31500, simpleUnion1.hashCode());
        assertEquals(31640, simpleUnion2.hashCode());

        simpleUnion2.setCase4Field(CASE1_FIELD); // same value as simpleUnion1
        assertFalse(simpleUnion1.hashCode() == simpleUnion2.hashCode());
        simpleUnion1.setCase4Field(CASE1_FIELD); // same value as simpleUnion2
        assertEquals(simpleUnion1.hashCode(), simpleUnion2.hashCode());
    }

    @Test
    public void readCase1_getCase1Field() throws IOException
    {
        SimpleUnion simpleUnion = read(SimpleUnion.CHOICE_case1Field);
        assertEquals(CASE1_FIELD, simpleUnion.getCase1Field());
    }

    @Test
    public void readCase2_getCase2Field() throws IOException
    {
        SimpleUnion simpleUnion = read(SimpleUnion.CHOICE_case2Field);
        assertEquals(CASE2_FIELD, simpleUnion.getCase2Field());
    }

    @Test
    public void readCase3_getCase3Field() throws IOException
    {
        SimpleUnion simpleUnion = read(SimpleUnion.CHOICE_case3Field);
        assertEquals(CASE3_FIELD, simpleUnion.getCase3Field());
    }

    @Test
    public void readCase4_getCase4Field() throws IOException
    {
        SimpleUnion simpleUnion = read(SimpleUnion.CHOICE_case4Field);
        assertEquals(CASE4_FIELD, simpleUnion.getCase4Field());
    }

    @Test
    public void writeCase1() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase1Field(CASE1_FIELD);
        write(simpleUnion);
    }

    @Test
    public void writeCase2() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase2Field(CASE2_FIELD);
        write(simpleUnion);
    }

    @Test
    public void writeCase3() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase3Field(CASE3_FIELD);
        write(simpleUnion);
    }

    @Test
    public void writeCase4() throws ZserioError, IOException
    {
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setCase4Field(CASE4_FIELD);
        write(simpleUnion);
    }

    private static SimpleUnion bitStreamReaderConstructor(int choiceTag) throws ZserioError, IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writeSimpleUnionToByteArray(writer, choiceTag);
        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        SimpleUnion simpleUnion = new SimpleUnion(reader);
        return simpleUnion;
    }

    private static SimpleUnion read(int choiceTag) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writeSimpleUnionToByteArray(writer, choiceTag);
        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.read(reader);
        return simpleUnion;
    }

    private static void write(SimpleUnion simpleUnion) throws ZserioError, IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        simpleUnion.write(writer);
        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        SimpleUnion readSimpleUnion = new SimpleUnion(reader);
        assertTrue(simpleUnion.equals(readSimpleUnion));
    }

    private static void writeSimpleUnionToByteArray(BitStreamWriter writer, int choiceTag) throws IOException
    {
        switch (choiceTag)
        {
        case SimpleUnion.CHOICE_case1Field:
            writeSimpleUnionCase1ToByteArray(writer);
            break;
        case SimpleUnion.CHOICE_case2Field:
            writeSimpleUnionCase2ToByteArray(writer);
            break;
        case SimpleUnion.CHOICE_case3Field:
            writeSimpleUnionCase3ToByteArray(writer);
            break;
        case SimpleUnion.CHOICE_case4Field:
            writeSimpleUnionCase4ToByteArray(writer);
            break;
        default:
            throw new RuntimeException("writeSimpleUnionToByteArray - unknown choiceTag" + choiceTag + "!");
        }
    }

    private static void writeSimpleUnionCase1ToByteArray(BitStreamWriter writer) throws IOException
    {
        writer.writeVarSize(SimpleUnion.CHOICE_case1Field); // choice tag
        writer.writeSignedBits(CASE1_FIELD, 8);
    }

    private static void writeSimpleUnionCase2ToByteArray(BitStreamWriter writer) throws IOException
    {
        writer.writeVarSize(SimpleUnion.CHOICE_case2Field); // choice tag
        writer.writeBits(CASE2_FIELD, 16);
    }

    private static void writeSimpleUnionCase3ToByteArray(BitStreamWriter writer) throws IOException
    {
        writer.writeVarSize(SimpleUnion.CHOICE_case3Field); // choice tag
        writer.writeString(CASE3_FIELD);
    }

    private static void writeSimpleUnionCase4ToByteArray(BitStreamWriter writer) throws IOException
    {
        writer.writeVarSize(SimpleUnion.CHOICE_case4Field); // choice tag
        writer.writeSignedBits(CASE4_FIELD, 8);
    }

    private static final byte CASE1_FIELD = 13;
    private static final int CASE2_FIELD = 65535;
    private static final String CASE3_FIELD = "SimpleUnion";
    private static final byte CASE4_FIELD = 42;
    private static final long UNION_CASE1_BIT_SIZE =
            BitSizeOfCalculator.getBitSizeOfVarUInt64(SimpleUnion.CHOICE_case1Field) + 8;
    private static final long UNION_CASE2_BIT_SIZE =
            BitSizeOfCalculator.getBitSizeOfVarUInt64(SimpleUnion.CHOICE_case2Field) + 16;
    private static final long UNION_CASE3_BIT_SIZE =
            BitSizeOfCalculator.getBitSizeOfVarUInt64(SimpleUnion.CHOICE_case3Field) +
            BitSizeOfCalculator.getBitSizeOfString(CASE3_FIELD);
    private static final long UNION_CASE4_BIT_SIZE =
            BitSizeOfCalculator.getBitSizeOfVarUInt64(SimpleUnion.CHOICE_case4Field) + 8;
}
