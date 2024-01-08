package extended_members.extended_optional_parameterized_field;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ExtendedOptionalParameterizedFieldTest
{
    @Test
    public void defaultConstructor()
    {
        final Extended extended = new Extended();

        // always present when not read from stream
        assertTrue(extended.isExtendedValuePresent());

        // default initialized
        assertEquals(0, extended.getValue());
        // optional is unset
        assertFalse(extended.isExtendedValueSet());
        assertFalse(extended.isExtendedValueUsed());
    }

    @Test
    public void fieldConstructor()
    {
        final Extended extended = new Extended((short)ARRAY.length, new Parameterized((short)ARRAY.length));
        extended.getExtendedValue().setArray(ARRAY);
        assertTrue(extended.isExtendedValuePresent());

        assertEquals(ARRAY.length, extended.getValue());
        assertArrayEquals(ARRAY, extended.getExtendedValue().getArray());
    }

    @Test
    public void equals()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1, extended2);

        extended1.setValue((short)ARRAY.length);
        assertNotEquals(extended1, extended2);
        extended2.setValue((short)ARRAY.length);
        assertEquals(extended1, extended2);

        final Parameterized extendedValue = new Parameterized((short)ARRAY.length);
        extendedValue.setArray(ARRAY);
        extended2.setExtendedValue(extendedValue);
        assertNotEquals(extended1, extended2);
        extended1.setExtendedValue(extendedValue);
        assertEquals(extended1, extended2);
    }

    @Test
    public void hashCodeMethod()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1.hashCode(), extended2.hashCode());

        extended1.setValue((short)13);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setValue((short)13);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        final Parameterized extendedValue = new Parameterized((short)ARRAY.length);
        extendedValue.setArray(ARRAY);
        extended2.setExtendedValue(extendedValue);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue(extendedValue);
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void bitSizeOf()
    {
        final Extended extended = new Extended();
        assertEquals(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extended.bitSizeOf());

        final Parameterized extendedValue = new Parameterized((short)ARRAY.length);
        extendedValue.setArray(ARRAY);
        extended.setExtendedValue(extendedValue);
        assertEquals(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extended.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final Extended extended = new Extended();
        assertEquals(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extended.initializeOffsets(0));

        final Parameterized extendedValue = new Parameterized((short)ARRAY.length);
        extendedValue.setArray(ARRAY);
        extended.setExtendedValue(extendedValue);
        assertEquals(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extended.initializeOffsets(0));
    }

    @Test
    public void writeReadExtendedWithoutOptional()
    {
        final Extended extended = new Extended((short)0, null);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertFalse(readExtended.isExtendedValueSet());
        assertFalse(readExtended.isExtendedValueUsed());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeReadExtendedWithOptional()
    {
        final Extended extended = new Extended((short)ARRAY.length, new Parameterized((short)ARRAY.length));
        extended.getExtendedValue().setArray(ARRAY);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertTrue(readExtended.isExtendedValueSet());
        assertTrue(readExtended.isExtendedValueUsed());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeOriginalReadExtended()
    {
        final Original original = new Original((short)ARRAY.length);
        BitBuffer bitBuffer = SerializeUtil.serialize(original);
        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertFalse(readExtended.isExtendedValuePresent());

        // extended value is default constructed (NullOpt)
        assertFalse(readExtended.isExtendedValueSet());
        assertFalse(readExtended.isExtendedValueUsed());

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

        // setter makes the value present! (or even resetter)
        final Extended extendedWithoutOptional = SerializeUtil.deserialize(Extended.class, bitBuffer);
        extendedWithoutOptional.resetExtendedValue();
        assertTrue(extendedWithoutOptional.isExtendedValuePresent());

        final Extended extendedWithOptional = SerializeUtil.deserialize(Extended.class, bitBuffer);
        final Parameterized extendedValue = new Parameterized((short)ARRAY.length, ARRAY);
        extendedWithOptional.setExtendedValue(extendedValue);
        assertTrue(extendedWithOptional.isExtendedValuePresent());

        // bit size as extended
        assertEquals(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extendedWithoutOptional.bitSizeOf());
        assertEquals(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extendedWithOptional.bitSizeOf());

        // initialize offsets as extended
        assertEquals(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extendedWithoutOptional.initializeOffsets(0));
        assertEquals(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extendedWithOptional.initializeOffsets(0));

        // writes as extended
        bitBuffer = SerializeUtil.serialize(extendedWithoutOptional);
        assertEquals(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.getBitSize());
        bitBuffer = SerializeUtil.serialize(extendedWithOptional);
        assertEquals(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.getBitSize());
    }

    @Test
    public void writeExtendedWithoutOptionalReadOriginal() throws IOException
    {
        final Extended extended = new Extended((short)0, null);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getValue(), readOriginal.getValue());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    @Test
    public void writeExtendedWithOptionalReadOriginal() throws IOException
    {
        final Extended extended = new Extended((short)ARRAY.length, new Parameterized((short)ARRAY.length));
        extended.getExtendedValue().setArray(ARRAY);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getValue(), readOriginal.getValue());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    private static final String ARRAY[] = new String[] {"this", "is", "test"};
    private static final long ARRAY_BIT_SIZE =
            Arrays.stream(ARRAY).mapToLong(str -> BitSizeOfCalculator.getBitSizeOfString(str)).sum();

    private static final long ORIGINAL_BIT_SIZE = 11;
    private static final long EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL =
            zserio.runtime.BitPositionUtil.alignTo(8, ORIGINAL_BIT_SIZE) + 1;
    private static final long EXTENDED_BIT_SIZE_WITH_OPTIONAL =
            zserio.runtime.BitPositionUtil.alignTo(8, ORIGINAL_BIT_SIZE) + 1 + ARRAY_BIT_SIZE;
}
