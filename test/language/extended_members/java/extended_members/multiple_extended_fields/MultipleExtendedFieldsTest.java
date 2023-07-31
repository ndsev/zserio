package extended_members.multiple_extended_fields;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.math.BigInteger;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class MultipleExtendedFieldsTest
{
    @Test
    public void defaultConstructor()
    {
        final Extended2 extended2 = new Extended2();

        // always present when not read from stream
        assertTrue(extended2.isExtendedValue1Present());
        assertTrue(extended2.isExtendedValue2Present());

        // default initialized
        assertEquals(0, extended2.getValue());
        assertEquals(0, extended2.getExtendedValue1());
        assertEquals(DEFAULT_EXTENDED_VALUE2, extended2.getExtendedValue2());
    }

    @Test
    public void fieldConstructor()
    {
        final Extended2 extended2 = new Extended2(42, (byte)2, "other");
        assertTrue(extended2.isExtendedValue1Present());
        assertTrue(extended2.isExtendedValue2Present());

        assertEquals(42, extended2.getValue());
        assertEquals((byte)2, extended2.getExtendedValue1());
        assertEquals("other", extended2.getExtendedValue2());
    }

    @Test
    public void equals()
    {
        final Extended2 extended1 = new Extended2();
        final Extended2 extended2 = new Extended2();
        assertEquals(extended1, extended2);

        extended1.setValue(13);
        assertNotEquals(extended1, extended2);
        extended2.setValue(13);
        assertEquals(extended1, extended2);

        extended2.setExtendedValue1((byte)2);
        assertNotEquals(extended1, extended2);
        extended1.setExtendedValue1((byte)2);
        assertEquals(extended1, extended2);

        extended1.setExtendedValue2("value");
        assertNotEquals(extended1, extended2);
        extended2.setExtendedValue2("value");
        assertEquals(extended1, extended2);
    }

    @Test
    public void hashCodeMethod()
    {
        final Extended2 extended1 = new Extended2();
        final Extended2 extended2 = new Extended2();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setValue(13);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setValue(13);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended2.setExtendedValue1((byte)2);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue1((byte)2);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setExtendedValue2("value");
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setExtendedValue2("value");
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void bitSizeOf()
    {
        final Extended2 extended2 = new Extended2();
        assertEquals(EXTENDED2_BIT_SIZE, extended2.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final Extended2 extended2 = new Extended2();
        assertEquals(EXTENDED2_BIT_SIZE, extended2.initializeOffsets(0));
    }

    @Test
    public void writeReadExtended2()
    {
        final Extended2 extended2 = new Extended2(42, (byte)2, DEFAULT_EXTENDED_VALUE2);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended2);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

        final Extended2 readExtended = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        assertTrue(readExtended.isExtendedValue1Present());
        assertTrue(readExtended.isExtendedValue2Present());
        assertEquals(extended2, readExtended);
    }

    @Test
    public void writeOriginalReadExtended2()
    {
        final Original original = new Original((byte)42);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final Extended2 readExtended2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        assertFalse(readExtended2.isExtendedValue1Present());
        assertFalse(readExtended2.isExtendedValue2Present());

        // extended values are default constructed
        assertEquals(0, readExtended2.getExtendedValue1());
        assertEquals(DEFAULT_EXTENDED_VALUE2, readExtended2.getExtendedValue2());

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
        final Extended2 readExtended2Setter1 = SerializeUtil.deserialize(Extended2.class, bitBuffer);;
        readExtended2Setter1.setExtendedValue1((byte)2);
        assertTrue(readExtended2Setter1.isExtendedValue1Present());
        assertTrue(readExtended2Setter1.isExtendedValue2Present());

        final Extended2 readExtended2Setter2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);;
        readExtended2Setter2.setExtendedValue2(DEFAULT_EXTENDED_VALUE2);
        assertTrue(readExtended2Setter2.isExtendedValue1Present());
        assertTrue(readExtended2Setter2.isExtendedValue2Present());

        // bit size as extended2
        assertEquals(EXTENDED2_BIT_SIZE, readExtended2Setter1.bitSizeOf());
        assertEquals(EXTENDED2_BIT_SIZE, readExtended2Setter2.bitSizeOf());

        // initialize offsets as extended2
        assertEquals(EXTENDED2_BIT_SIZE, readExtended2Setter1.initializeOffsets(0));
        assertEquals(EXTENDED2_BIT_SIZE, readExtended2Setter2.initializeOffsets(0));

        // writes as extended2
        bitBuffer = SerializeUtil.serialize(readExtended2Setter1);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());
        bitBuffer = SerializeUtil.serialize(readExtended2Setter2);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());
    }

    @Test
    public void writeExtended1ReadExtended2()
    {
        final Extended1 extended1 = new Extended1(42, (byte)2);
        BitBuffer bitBuffer = SerializeUtil.serialize(extended1);
        final Extended2 readExtended2 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        assertTrue(readExtended2.isExtendedValue1Present());
        assertFalse(readExtended2.isExtendedValue2Present());

        assertEquals(2, readExtended2.getExtendedValue1());
        // extended value is default constructed
        assertEquals(DEFAULT_EXTENDED_VALUE2, readExtended2.getExtendedValue2());

        // bit size as extended1
        assertEquals(EXTENDED1_BIT_SIZE, readExtended2.bitSizeOf());

        // initialize offsets as extended1
        assertEquals(EXTENDED1_BIT_SIZE, readExtended2.initializeOffsets(0));

        // write as extended1
        bitBuffer = SerializeUtil.serialize(readExtended2);
        assertEquals(EXTENDED1_BIT_SIZE, bitBuffer.getBitSize());

        // read extended1 again
        final Extended1 readExtended1 = SerializeUtil.deserialize(Extended1.class, bitBuffer);
        assertEquals(extended1, readExtended1);

        // read original
        final Original readOriginal = SerializeUtil.deserialize(Original.class, bitBuffer);
        assertEquals(42, readOriginal.getValue());

        // setter of actually present field will not make all fields present
        final Extended2 readExtended2Setter1 = SerializeUtil.deserialize(Extended2.class, bitBuffer);
        readExtended2Setter1.setExtendedValue1((byte)2);
        assertTrue(readExtended2Setter1.isExtendedValue1Present());
        assertFalse(readExtended2Setter1.isExtendedValue2Present());

        // setter of non-present field makes all fields present
        final Extended2 readExtended2Setter2 = readExtended2;
        readExtended2Setter2.setExtendedValue2(DEFAULT_EXTENDED_VALUE2);
        assertTrue(readExtended2Setter2.isExtendedValue1Present());
        assertTrue(readExtended2Setter2.isExtendedValue2Present());

        // bit size as extended1
        assertEquals(EXTENDED1_BIT_SIZE, readExtended2Setter1.bitSizeOf());

        // bit size as extended2
        assertEquals(EXTENDED2_BIT_SIZE, readExtended2Setter2.bitSizeOf());

        // initialize offsets as extended1
        assertEquals(EXTENDED1_BIT_SIZE, readExtended2Setter1.initializeOffsets(0));

        // initialize offsets as extended2
        assertEquals(EXTENDED2_BIT_SIZE, readExtended2Setter2.initializeOffsets(0));

        // writes as extended1
        bitBuffer = SerializeUtil.serialize(readExtended2Setter1);
        assertEquals(EXTENDED1_BIT_SIZE, bitBuffer.getBitSize());

        // writes as extended2
        bitBuffer = SerializeUtil.serialize(readExtended2Setter2);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());
    }

    @Test
    public void writeExtended2ReadOriginal() throws IOException
    {
        final Extended2 extended2 = new Extended2(42, (byte)2, DEFAULT_EXTENDED_VALUE2);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended2);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended2.getValue(), readOriginal.getValue());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    @Test
    public void writeExtended2ReadExtended1() throws IOException
    {
        final Extended2 extended2 = new Extended2(42, (byte)2, DEFAULT_EXTENDED_VALUE2);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended2);
        assertEquals(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Extended1 readExtended1 = new Extended1(reader);
        assertEquals(extended2.getValue(), readExtended1.getValue());
        assertEquals(EXTENDED1_BIT_SIZE, reader.getBitPosition());
    }


    private static final String DEFAULT_EXTENDED_VALUE2 = "test";

    private static final long ORIGINAL_BIT_SIZE = 4 * 8;
    private static final long EXTENDED1_BIT_SIZE = ORIGINAL_BIT_SIZE + 4;
    private static final long EXTENDED2_BIT_SIZE = BitPositionUtil.alignTo(8, EXTENDED1_BIT_SIZE) +
            BitSizeOfCalculator.getBitSizeOfString(DEFAULT_EXTENDED_VALUE2);
}
