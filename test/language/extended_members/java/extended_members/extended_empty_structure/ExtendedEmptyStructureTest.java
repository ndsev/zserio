package extended_members.extended_empty_structure;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.math.BigInteger;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ExtendedEmptyStructureTest
{
    @Test
    public void defaultConstructor()
    {
        final Extended extended = new Extended();

        // always present when not read from stream
        assertTrue(extended.isExtendedValuePresent());

        // default initialized
        assertEquals(0, extended.getExtendedValue());
    }

    @Test
    public void fieldConstructor()
    {
        final Extended extended = new Extended(UINT32_MAX);
        assertTrue(extended.isExtendedValuePresent());

        assertEquals(UINT32_MAX, extended.getExtendedValue());
    }

    @Test
    public void equals()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1, extended2);

        extended2.setExtendedValue(UINT32_MAX);
        assertNotEquals(extended1, extended2);
        extended1.setExtendedValue(UINT32_MAX);
        assertEquals(extended1, extended2);
    }

    @Test
    public void hashCodeMethod()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended2.setExtendedValue(42);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue(42);
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void bitSizeOf()
    {
        final Extended extended = new Extended();
        assertEquals(EXTENDED_BIT_SIZE, extended.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final Extended extended = new Extended();
        assertEquals(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
    }

    @Test
    public void writeReadExtended()
    {
        final Extended extended = new Extended(UINT32_MAX);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeOriginalReadExtended()
    {
        final Original original = new Original();
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertFalse(readExtended.isExtendedValuePresent());

        // extended value default initialized
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
    public void writeExtendedReadOriginal() throws IOException
    {
        final Extended extended = new Extended(UINT32_MAX);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        new Original(reader);
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    private static final long UINT32_MAX = (1L << 32) - 1;

    private static final long ORIGINAL_BIT_SIZE = 0;
    private static final long EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE + 4 * 8;
}
