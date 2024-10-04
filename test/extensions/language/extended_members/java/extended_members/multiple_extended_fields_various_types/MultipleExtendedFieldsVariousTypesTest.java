package extended_members.multiple_extended_fields_various_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class MultipleExtendedFieldsVariousTypesTest
{
    @Test
    public void defaultConstructor()
    {
        final Extended2 extended2 = new Extended2();

        // always present when not read from stream
        checkAllExtendedFieldsPresent(extended2, true);

        // default constructed
        assertFalse(extended2.isExtendedValue1Set());
        assertFalse(extended2.isExtendedValue1Used());
        assertNull(extended2.getExtendedValue2());
        assertNull(extended2.getExtendedValue3());
        assertFalse(extended2.isExtendedValue4Set());
        assertFalse(extended2.isExtendedValue4Used());
        assertEquals(0, extended2.getExtendedValue5());
        assertNull(extended2.getExtendedValue6());
        assertNull(extended2.getExtendedValue7());
        assertFalse(extended2.isExtendedValue8Set());
        assertFalse(extended2.isExtendedValue8Used());
        assertFalse(extended2.isExtendedValue9Set());
        assertFalse(extended2.isExtendedValue9Used());
    }

    @Test
    public void fieldConstructor()
    {
        final Union extendedValue7 = new Union(EXTENDED_VALUE5);
        final Extended2 extended2 = new Extended2(VALUE, EXTENDED_VALUE1, EXTENDED_VALUE2, EXTENDED_VALUE3,
                null, EXTENDED_VALUE5, EXTENDED_VALUE6, extendedValue7, null, EXTENDED_VALUE9);

        checkAllExtendedFieldsPresent(extended2, true);

        assertTrue(extended2.isExtendedValue1Set());
        assertTrue(extended2.isExtendedValue1Used());
        assertEquals(EXTENDED_VALUE1, extended2.getExtendedValue1());
        assertEquals(EXTENDED_VALUE2, extended2.getExtendedValue2());
        assertArrayEquals(EXTENDED_VALUE3, extended2.getExtendedValue3());
        assertFalse(extended2.isExtendedValue4Set());
        assertFalse(extended2.isExtendedValue4Used());
        assertEquals(EXTENDED_VALUE5, extended2.getExtendedValue5());
        assertArrayEquals(EXTENDED_VALUE6, extended2.getExtendedValue6());
        assertEquals(extendedValue7, extended2.getExtendedValue7());
        assertFalse(extended2.isExtendedValue8Set());
        assertFalse(extended2.isExtendedValue8Used());
        assertEquals(EXTENDED_VALUE9, extended2.getExtendedValue9());
    }

    @Test
    public void equals()
    {
        final Extended2 extended1 = new Extended2();
        final Extended2 extended2 = new Extended2();
        final Extended2 extended3 = createExtended2();
        final Extended2 extended4 = createExtended2();

        assertEquals(extended1, extended2);
        assertNotEquals(extended1, extended3);
        assertEquals(extended3, extended4);

        extended3.setExtendedValue9(BigInteger.ZERO);
        assertNotEquals(extended3, extended4);
    }

    @Test
    public void hashCodeTest()
    {
        final Extended2 extended1 = new Extended2();
        final Extended2 extended2 = new Extended2();
        final Extended2 extended3 = createExtended2();
        final Extended2 extended4 = createExtended2();

        assertEquals(extended1.hashCode(), extended2.hashCode());
        assertNotEquals(extended1.hashCode(), extended3.hashCode());
        assertEquals(extended3.hashCode(), extended4.hashCode());

        extended3.setExtendedValue9(BigInteger.ZERO);
        assertNotEquals(extended3.hashCode(), extended4.hashCode());
    }

    @Test
    public void bitSizeOf()
    {
        final Extended1 extended1 = createExtended1();
        assertEquals(EXTENDED1_BIT_SIZE, extended1.bitSizeOf());

        final Extended2 extended2 = createExtended2();
        assertEquals(EXTENDED2_BIT_SIZE, extended2.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final Extended1 extended1 = createExtended1();
        assertEquals(EXTENDED1_BIT_SIZE, extended1.initializeOffsets(0));

        final Extended2 extended2 = createExtended2();
        assertEquals(EXTENDED2_BIT_SIZE, extended2.initializeOffsets(0));
    }

    @Test
    public void writeReadExtended2()
    {
        final Extended2 extended2 = createExtended2();
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended2);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

        final Extended2 readExtended2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        checkAllExtendedFieldsPresent(extended2, true);
        assertEquals(extended2, readExtended2);
    }

    @Test
    public void writeOriginalReadExtended2()
    {
        final Original original = new Original(VALUE);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final Extended2 readExtended2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        checkAllExtendedFieldsPresent(readExtended2, false);

        // extended fields are default constructed
        assertFalse(readExtended2.isExtendedValue1Set());
        assertFalse(readExtended2.isExtendedValue1Used());
        assertNull(readExtended2.getExtendedValue2());
        assertNull(readExtended2.getExtendedValue3());
        assertFalse(readExtended2.isExtendedValue4Set());
        assertFalse(readExtended2.isExtendedValue4Used());
        assertEquals(0, readExtended2.getExtendedValue5());
        assertNull(readExtended2.getExtendedValue6());
        assertNull(readExtended2.getExtendedValue7());
        assertFalse(readExtended2.isExtendedValue8Set());
        assertFalse(readExtended2.isExtendedValue8Used());
        assertFalse(readExtended2.isExtendedValue9Set());
        assertFalse(readExtended2.isExtendedValue9Used());

        // bit size as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended2.bitSizeOf());

        // initialize offsets as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended2.initializeOffsets(0));

        // writes as original
        bitBuffer = SerializeUtil.serialize(readExtended2);
        assertEquals(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

        // read original again
        final Original readOriginal = SerializeUtil.deserialize(Original.class, bitBuffer);
        assertEquals(original, readOriginal);

        // any setter makes all values present!
        readExtended2.setExtendedValue2(EXTENDED_VALUE2);
        checkAllExtendedFieldsPresent(readExtended2, true);
    }

    @Test
    public void writeExtended1ReadExtended2()
    {
        Extended1 extended1 = createExtended1();
        BitBuffer bitBuffer = SerializeUtil.serialize(extended1);
        final Extended2 readExtended2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        checkExtended1FieldsPresent(readExtended2, true);
        checkExtended2FieldsPresent(readExtended2, false);

        // extended1 fields are read from the stream
        assertTrue(readExtended2.isExtendedValue1Set());
        assertTrue(readExtended2.isExtendedValue1Used());
        assertEquals(EXTENDED_VALUE1, readExtended2.getExtendedValue1());
        assertEquals(EXTENDED_VALUE2, readExtended2.getExtendedValue2());
        assertArrayEquals(EXTENDED_VALUE3, readExtended2.getExtendedValue3());

        // extended2 fields are default constructed
        assertFalse(readExtended2.isExtendedValue4Set());
        assertFalse(readExtended2.isExtendedValue4Used());
        assertEquals(0, readExtended2.getExtendedValue5());
        assertNull(readExtended2.getExtendedValue6());
        assertNull(readExtended2.getExtendedValue7());
        assertFalse(readExtended2.isExtendedValue8Set());
        assertFalse(readExtended2.isExtendedValue8Used());
        assertFalse(readExtended2.isExtendedValue9Set());
        assertFalse(readExtended2.isExtendedValue9Used());

        // bit size as extended1
        assertEquals(EXTENDED1_BIT_SIZE, readExtended2.bitSizeOf());

        // initialize offsets as extended1
        assertEquals(EXTENDED1_BIT_SIZE, readExtended2.initializeOffsets(0));

        // writes as extended1
        bitBuffer = SerializeUtil.serialize(readExtended2);
        assertEquals(EXTENDED1_BIT_SIZE, bitBuffer.getBitSize());

        // read extended1 again
        final Extended1 readExtended1 = SerializeUtil.deserialize(Extended1.class, bitBuffer);
        assertEquals(extended1, readExtended1);

        // read original
        final Original readOriginal = SerializeUtil.deserialize(Original.class, bitBuffer);
        assertEquals(VALUE, readOriginal.getValue());

        // resetter of actually present optional field will not make all fields present
        final Extended2 readExtended2Setter1 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        assertTrue(readExtended2Setter1.isExtendedValue1Set());
        readExtended2Setter1.resetExtendedValue1(); // reset value from Extended1
        assertFalse(readExtended2Setter1.isExtendedValue1Set());
        checkExtended1FieldsPresent(readExtended2Setter1, true);
        checkExtended2FieldsPresent(readExtended2Setter1, false);

        // setter of actually present field will not make all fields present
        final Extended2 readExtended2Setter2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        readExtended2Setter2.setExtendedValue2(EXTENDED_VALUE2); // set value from Extended1
        checkExtended1FieldsPresent(readExtended2Setter2, true);
        checkExtended2FieldsPresent(readExtended2Setter2, false);

        // setter of non-present field will make all fields present
        final Extended2 readExtended2Setter5 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        readExtended2Setter5.setExtendedValue5(EXTENDED_VALUE5); // set value from Extended2
        checkAllExtendedFieldsPresent(readExtended2Setter5, true);
    }

    private void checkExtended1FieldsPresent(Extended2 extended2, boolean expectedExtended1FieldsPresent)
    {
        assertEquals(expectedExtended1FieldsPresent, extended2.isExtendedValue1Present());
        assertEquals(expectedExtended1FieldsPresent, extended2.isExtendedValue2Present());
        assertEquals(expectedExtended1FieldsPresent, extended2.isExtendedValue3Present());
    }

    private void checkExtended2FieldsPresent(Extended2 extended2, boolean expectedExtended2FieldsPresent)
    {
        assertEquals(expectedExtended2FieldsPresent, extended2.isExtendedValue4Present());
        assertEquals(expectedExtended2FieldsPresent, extended2.isExtendedValue5Present());
        assertEquals(expectedExtended2FieldsPresent, extended2.isExtendedValue6Present());
        assertEquals(expectedExtended2FieldsPresent, extended2.isExtendedValue7Present());
        assertEquals(expectedExtended2FieldsPresent, extended2.isExtendedValue8Present());
        assertEquals(expectedExtended2FieldsPresent, extended2.isExtendedValue9Present());
    }

    private void checkAllExtendedFieldsPresent(Extended2 extended2, boolean expectedPresent)
    {
        checkExtended1FieldsPresent(extended2, expectedPresent);
        checkExtended2FieldsPresent(extended2, expectedPresent);
    }

    private static Extended1 createExtended1()
    {
        return new Extended1(VALUE, EXTENDED_VALUE1, EXTENDED_VALUE2, EXTENDED_VALUE3);
    }

    private static Extended2 createExtended2()
    {
        final Union extendedValue7 = new Union(EXTENDED_VALUE5);
        extendedValue7.setValueU32(UINT32_MAX);
        return new Extended2(VALUE, EXTENDED_VALUE1, EXTENDED_VALUE2, EXTENDED_VALUE3, null, EXTENDED_VALUE5,
                EXTENDED_VALUE6, extendedValue7, null, EXTENDED_VALUE9);
    }

    private static long calcExtended1BitSize()
    {
        long bitSize = ORIGINAL_BIT_SIZE;
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += 1 + 4 * 8; // optional extendedValue1
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += BitSizeOfCalculator.getBitSizeOfBitBuffer(EXTENDED_VALUE2);
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += BitSizeOfCalculator.getBitSizeOfBytes(EXTENDED_VALUE3);
        return bitSize;
    }

    private static long calcExtended2BitSize()
    {
        long bitSize = calcExtended1BitSize();
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += 1; // unset optional extendedValue4
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += BitSizeOfCalculator.getBitSizeOfVarSize(EXTENDED_VALUE5);
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += Arrays.stream(EXTENDED_VALUE6)
                           .mapToLong(str -> BitSizeOfCalculator.getBitSizeOfString(str))
                           .sum();
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += 8 + 4 * 8; // extendedValue7 (choiceTag + valueU32)
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += 1; // unset optional extendedValue8
        bitSize = BitPositionUtil.alignTo(8, bitSize);
        bitSize += EXTENDED_VALUE5; // used non-auto optional dynamic bit field extendedValue9
        return bitSize;
    }

    private static final long UINT32_MAX = (1L << 32) - 1;

    private static final byte VALUE = -13;
    private static final long EXTENDED_VALUE1 = 42;
    private static final BitBuffer EXTENDED_VALUE2 = new BitBuffer(new byte[] {(byte)0xCA, (byte)0xFE}, 16);
    private static final byte[] EXTENDED_VALUE3 = new byte[] {(byte)0xDE, (byte)0xAD};
    private static final int EXTENDED_VALUE5 = 3;
    private static final String[] EXTENDED_VALUE6 = new String[] {"this", "is", "test"};
    private static final BigInteger EXTENDED_VALUE9 = BigInteger.valueOf(7); // bit<EXTENDED_VALUE5> == bit<3>

    private static final long ORIGINAL_BIT_SIZE = 7;
    private static final long EXTENDED1_BIT_SIZE = calcExtended1BitSize();
    private static final long EXTENDED2_BIT_SIZE = calcExtended2BitSize();
}
