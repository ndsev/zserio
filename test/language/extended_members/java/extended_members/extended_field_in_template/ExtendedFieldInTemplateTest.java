package extended_members.extended_field_in_template;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ExtendedFieldInTemplateTest
{
    @Test
    public void defaultConstructorSimple()
    {
        final ExtendedSimple extended = new ExtendedSimple();

        // always present when not read from stream
        assertTrue(extended.isExtendedValuePresent());

        // default initialized
        assertEquals(0, extended.getValue());
        assertEquals(0, extended.getExtendedValue());
    }

    @Test
    public void defaultConstructorCompound()
    {
        final ExtendedCompound extended = new ExtendedCompound();

        // always present when not read from stream
        assertTrue(extended.isExtendedValuePresent());

        // default initialized
        assertEquals(0, extended.getValue());
        assertNull(extended.getExtendedValue());
    }

    @Test
    public void fieldConstructorSimple()
    {
        final ExtendedSimple extended = new ExtendedSimple(42, UINT32_MAX);
        assertTrue(extended.isExtendedValuePresent());

        assertEquals(42, extended.getValue());
        assertEquals(UINT32_MAX, extended.getExtendedValue());
    }

    @Test
    public void fieldConstructorCompound()
    {
        final ExtendedCompound extended = new ExtendedCompound(42, new Compound(UINT32_MAX));
        assertTrue(extended.isExtendedValuePresent());

        assertEquals(42, extended.getValue());
        assertEquals(UINT32_MAX, extended.getExtendedValue().getField());
    }

    @Test
    public void equalsSimple()
    {
        final ExtendedSimple extended1 = new ExtendedSimple();
        final ExtendedSimple extended2 = new ExtendedSimple();
        assertEquals(extended1, extended2);

        extended1.setValue(13);
        assertFalse(extended1 == extended2);
        extended2.setValue(13);
        assertEquals(extended1, extended2);

        extended2.setExtendedValue(UINT32_MAX);
        assertFalse(extended1 == extended2);
        extended1.setExtendedValue(UINT32_MAX);
        assertEquals(extended1, extended2);
    }

    @Test
    public void equalsCompound()
    {
        final ExtendedCompound extended1 = new ExtendedCompound();
        final ExtendedCompound extended2 = new ExtendedCompound();
        assertEquals(extended1, extended2);

        extended1.setValue(13);
        assertFalse(extended1 == extended2);
        extended2.setValue(13);
        assertEquals(extended1, extended2);

        extended2.setExtendedValue(new Compound(UINT32_MAX));
        assertFalse(extended1 == extended2);
        extended1.setExtendedValue(new Compound(UINT32_MAX));
        assertEquals(extended1, extended2);
    }

    @Test
    public void hashCodeSimple()
    {
        final ExtendedSimple extended1 = new ExtendedSimple();
        final ExtendedSimple extended2 = new ExtendedSimple();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setValue(13);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setValue(13);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended2.setExtendedValue(42);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue(42);
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void hashCodeCompound()
    {
        final ExtendedCompound extended1 = new ExtendedCompound();
        final ExtendedCompound extended2 = new ExtendedCompound();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setValue(13);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setValue(13);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended2.setExtendedValue(new Compound(42));
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue(new Compound(42));
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void bitSizeOfSimple()
    {
        final ExtendedSimple extended = new ExtendedSimple();
        assertEquals(EXTENDED_BIT_SIZE, extended.bitSizeOf());
    }

    @Test
    public void bitSizeOfCompound()
    {
        final ExtendedCompound extended = new ExtendedCompound(42, new Compound());
        assertEquals(EXTENDED_BIT_SIZE, extended.bitSizeOf());
    }

    @Test
    public void initializeOffsetsSimple()
    {
        final ExtendedSimple extended = new ExtendedSimple();
        assertEquals(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
    }

    @Test
    public void initializeOffsetsCompound()
    {
        final ExtendedCompound extended = new ExtendedCompound(42, new Compound());
        assertEquals(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
    }

    @Test
    public void writeReadExtendedSimple()
    {
        final ExtendedSimple extended = new ExtendedSimple(42, UINT32_MAX);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final ExtendedSimple readExtended = SerializeUtil.deserialize(ExtendedSimple.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeReadExtendedCompound()
    {
        final ExtendedCompound extended = new ExtendedCompound(42, new Compound(UINT32_MAX));
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final ExtendedCompound readExtended = SerializeUtil.deserialize(ExtendedCompound.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeOriginalReadExtendedSimple()
    {
        final Original original = new Original(42);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final ExtendedSimple readExtended = SerializeUtil.deserialize(ExtendedSimple.class, bitBuffer);
        assertFalse(readExtended.isExtendedValuePresent());

        // extended value is default constructed
        assertEquals(0, readExtended.getExtendedValue());

        // bit size as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended.bitSizeOf());

        // initialize offsets as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended.initializeOffsets(0));

        // write as original
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

        // read original again
        final Original readOriginal = SerializeUtil.deserialize(Original.class, bitBuffer);
        assertEquals(original, readOriginal);

        // setter makes the value present!
        readExtended.setExtendedValue(UINT32_MAX);
        assertTrue(readExtended.isExtendedValuePresent());

        // bit size as extended
        assertEquals(EXTENDED_BIT_SIZE, readExtended.bitSizeOf());

        // initialize offsets as extended
        assertEquals(EXTENDED_BIT_SIZE, readExtended.initializeOffsets(0));

        // writes as extended
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());
    }

    @Test
    public void writeOriginalReadExtendedCompound()
    {
        final Original original = new Original(42);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final ExtendedCompound readExtended = SerializeUtil.deserialize(ExtendedCompound.class, bitBuffer);
        assertFalse(readExtended.isExtendedValuePresent());

        // extended value is null
        assertNull(readExtended.getExtendedValue());

        // bit size as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended.bitSizeOf());

        // initialize offsets as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended.initializeOffsets(0));

        // write as original
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

        // read original again
        final Original readOriginal = SerializeUtil.deserialize(Original.class, bitBuffer);
        assertEquals(original, readOriginal);

        // setter makes the value present!
        readExtended.setExtendedValue(new Compound(UINT32_MAX));
        assertTrue(readExtended.isExtendedValuePresent());

        // bit size as extended
        assertEquals(EXTENDED_BIT_SIZE, readExtended.bitSizeOf());

        // initialize offsets as extended
        assertEquals(EXTENDED_BIT_SIZE, readExtended.initializeOffsets(0));

        // writes as extended
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());
    }

    @Test
    public void writeExtendedSimpleReadOriginal() throws IOException
    {
        final ExtendedSimple extended = new ExtendedSimple(42, UINT32_MAX);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getValue(), readOriginal.getValue());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    @Test
    public void writeExtendedCompoundReadOriginal() throws IOException
    {
        final ExtendedCompound extended = new ExtendedCompound(42, new Compound(UINT32_MAX));
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getValue(), readOriginal.getValue());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    private static final long UINT32_MAX = (1L << 32) - 1;

    private static final long ORIGINAL_BIT_SIZE = 4 * 8;
    private static final long EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE + 4 * 8;
}
