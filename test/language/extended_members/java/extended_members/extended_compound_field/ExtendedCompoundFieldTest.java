package extended_members.extended_compound_field;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.math.BigInteger;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ExtendedCompoundFieldTest
{
    @Test
    public void defaultConstructor()
    {
        final Extended extended = new Extended();

        // always present when not read from stream
        assertTrue(extended.isExtendedValuePresent());

        // default initialized
        assertEquals(0, extended.getValue());
        assertNull(extended.getExtendedValue());
    }

    @Test
    public void fieldConstructor()
    {
        final Extended extended = new Extended(42, new Compound(COMPOUND_ARRAY));
        assertTrue(extended.isExtendedValuePresent());

        assertEquals(42, extended.getValue());
        assertArrayEquals(COMPOUND_ARRAY, extended.getExtendedValue().getArray());
    }

    @Test
    public void equals()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1, extended2);

        extended1.setValue(13);
        assertNotEquals(extended1, extended2);
        extended2.setValue(13);
        assertEquals(extended1, extended2);

        extended2.setExtendedValue(new Compound(COMPOUND_ARRAY));
        assertNotEquals(extended1, extended2);
        extended1.setExtendedValue(new Compound(COMPOUND_ARRAY));
        assertEquals(extended1, extended2);
    }

    @Test
    public void hashCodeMethod()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setValue(13);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setValue(13);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended2.setExtendedValue(new Compound(COMPOUND_ARRAY));
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue(new Compound(COMPOUND_ARRAY));
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void bitSizeOf()
    {
        final Extended extended = new Extended(42, new Compound(COMPOUND_ARRAY));
        assertEquals(EXTENDED_BIT_SIZE_WITH_ARRAY, extended.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final Extended extended = new Extended(42, new Compound(COMPOUND_ARRAY));
        assertEquals(EXTENDED_BIT_SIZE_WITH_ARRAY, extended.initializeOffsets(0));
    }

    @Test
    public void writeReadExtended()
    {
        final Extended extended = new Extended(42, new Compound(COMPOUND_ARRAY));
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeOriginalReadExtended()
    {
        final Original original = new Original(42);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertFalse(readExtended.isExtendedValuePresent());

        // extended value is default constructed
        assertNull(readExtended.getExtendedValue());

        // bit size as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended.bitSizeOf());

        // initialize offsets as original
        assertEquals(ORIGINAL_BIT_SIZE, readExtended.initializeOffsets(0));

        // writes as original
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

        // read original again
        final Original readOriginal = SerializeUtil.deserialize(Original.class, bitBuffer);
        assertEquals(original, readOriginal);

        // setter makes the value present!
        readExtended.setExtendedValue(new Compound(COMPOUND_ARRAY));
        assertTrue(readExtended.isExtendedValuePresent());

        // bit size as extended
        assertEquals(EXTENDED_BIT_SIZE_WITH_ARRAY, readExtended.bitSizeOf());

        // initialize offsets as extended
        assertEquals(EXTENDED_BIT_SIZE_WITH_ARRAY, readExtended.initializeOffsets(0));

        // write as extended
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.getBitSize());
    }

    @Test
    public void writeExtendedReadOriginal() throws IOException
    {
        final Extended extended = new Extended(42, new Compound(COMPOUND_ARRAY));
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getValue(), readOriginal.getValue());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    private static final long COMPOUND_ARRAY[] = new long[]{ 0, 1, 2, 3, 4 };

    private static final long ORIGINAL_BIT_SIZE = 4 * 8;
    private static final long EXTENDED_BIT_SIZE_DEFAULT = ORIGINAL_BIT_SIZE +
            BitSizeOfCalculator.getBitSizeOfVarSize(0);
    private static final long EXTENDED_BIT_SIZE_WITH_ARRAY = ORIGINAL_BIT_SIZE +
            BitSizeOfCalculator.getBitSizeOfVarSize(COMPOUND_ARRAY.length) +
            COMPOUND_ARRAY.length * 4L * 8L;
}
