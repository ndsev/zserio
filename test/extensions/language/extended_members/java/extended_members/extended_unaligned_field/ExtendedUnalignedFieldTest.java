package extended_members.extended_unaligned_field;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ExtendedUnalignedFieldTest
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
        final Extended extended = new Extended((byte)2, UINT64_MAX);
        assertTrue(extended.isExtendedValuePresent());

        assertEquals(2, extended.getValue());
        assertEquals(UINT64_MAX, extended.getExtendedValue());
    }

    @Test
    public void operatorEquality()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1, extended2);

        extended1.setValue((byte)2);
        assertNotEquals(extended1, extended2);
        extended2.setValue((byte)2);
        assertEquals(extended1, extended2);

        extended2.setExtendedValue(BigInteger.valueOf(42));
        assertNotEquals(extended1, extended2);
        extended1.setExtendedValue(BigInteger.valueOf(42));
        assertEquals(extended1, extended2);
    }

    @Test
    public void hashCodeMethod()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setValue((byte)2);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setValue((byte)2);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended2.setExtendedValue(BigInteger.valueOf(42));
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue(BigInteger.valueOf(42));
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
        final Extended extended = new Extended((byte)2, UINT64_MAX);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeOriginalReadExtended()
    {
        final Original original = new Original((byte)2);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertFalse(readExtended.isExtendedValuePresent());

        // default initialized
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
        readExtended.setExtendedValue(UINT64_MAX);
        assertTrue(readExtended.isExtendedValuePresent());

        // bit size as extended
        assertEquals(EXTENDED_BIT_SIZE, readExtended.bitSizeOf());

        // initialize offsets as extended
        assertEquals(EXTENDED_BIT_SIZE, readExtended.initializeOffsets(0));

        // write as extended
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());
    }

    @Test
    public void writeExtendedReadOriginal() throws IOException
    {
        final Extended extended = new Extended((byte)2, UINT64_MAX);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getValue(), readOriginal.getValue());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    private static final BigInteger UINT64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

    private static final long ORIGINAL_BIT_SIZE = 3;
    private static final long EXTENDED_BIT_SIZE =
            zserio.runtime.BitPositionUtil.alignTo(8, ORIGINAL_BIT_SIZE) + 8 * 8;
}
