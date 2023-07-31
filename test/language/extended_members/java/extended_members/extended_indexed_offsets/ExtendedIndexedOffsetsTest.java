package extended_members.extended_indexed_offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.math.BigInteger;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ExtendedIndexedOffsetsTest
{
    @Test
    public void defaultConstructor()
    {
        final Extended extended = new Extended();

        // always present when not read from stream
        assertTrue(extended.isArrayPresent());

        // default initialized
        assertNull(extended.getOffsets());
        assertNull(extended.getArray());
    }

    @Test
    public void fieldConstructor()
    {
        final Extended extended = new Extended(OFFSETS, ARRAY);
        assertTrue(extended.isArrayPresent());

        assertArrayEquals(OFFSETS, extended.getOffsets());
        assertArrayEquals(ARRAY, extended.getArray());
    }

    @Test
    public void equals()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1, extended2);

        extended1.setOffsets(OFFSETS);
        assertNotEquals(extended1, extended2);
        extended2.setOffsets(OFFSETS);
        assertEquals(extended1, extended2);

        extended2.setArray(ARRAY);
        assertNotEquals(extended1, extended2);
        extended1.setArray(ARRAY);
        assertEquals(extended1, extended2);
    }

    @Test
    public void hashCodeMethod()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setOffsets(OFFSETS);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setOffsets(OFFSETS);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended2.setArray(ARRAY);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setArray(ARRAY);
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void bitSizeOf()
    {
        final Extended extended = new Extended(OFFSETS, ARRAY);
        assertEquals(EXTENDED_BIT_SIZE, extended.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final Extended extended = new Extended(OFFSETS, ARRAY);
        assertEquals(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
    }

    @Test
    public void writeReadExtended()
    {
        final Extended extended = new Extended(OFFSETS, ARRAY);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isArrayPresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeOriginalReadExtended()
    {
        final Original original = new Original(OFFSETS);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertFalse(readExtended.isArrayPresent());

        // extended value is null
        assertNull(readExtended.getArray());

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
        readExtended.setArray(ARRAY);
        assertTrue(readExtended.isArrayPresent());

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
        final Extended extended = new Extended(OFFSETS, ARRAY);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertArrayEquals(extended.getOffsets(), readOriginal.getOffsets());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    private static final long[] OFFSETS = new long[] { 0, 0, 0, 0, 0 };
    private static final String[] ARRAY = new String[] { "extended", "indexed", "offsets", "test", "!" };
    private static final long ARRAY_BIT_SIZE =
            Arrays.stream(ARRAY).mapToLong(str -> BitSizeOfCalculator.getBitSizeOfString(str)).sum();

    private static final long ORIGINAL_BIT_SIZE =
            BitSizeOfCalculator.getBitSizeOfVarSize(OFFSETS.length) + OFFSETS.length * 4 * 8;
    private static final long EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE +
            BitSizeOfCalculator.getBitSizeOfVarSize(ARRAY.length) + ARRAY_BIT_SIZE;

}
