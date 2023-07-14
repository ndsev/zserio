package extended_members.extended_choice_field;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.math.BigInteger;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ExtendedChoiceFieldTest
{
    @Test
    public void defaultConstructor()
    {
        final Extended extended = new Extended();

        // always present when not read from stream
        assertTrue(extended.isExtendedValuePresent());

        // default initialized
        assertEquals(0, extended.getNumElements());

        assertNull(extended.getExtendedValue());
    }

    @Test
    public void fieldConstructor()
    {
        final Extended extended = new Extended(1, new Choice(1));
        extended.getExtendedValue().setValue(42);

        assertTrue(extended.isExtendedValuePresent());

        assertEquals(1, extended.getNumElements());

        assertEquals(Choice.CHOICE_value, extended.getExtendedValue().choiceTag());
        assertEquals(42, extended.getExtendedValue().getValue());
    }

    @Test
    public void equals()
    {
        final Extended extended1 = new Extended();
        final Extended extended2 = new Extended();
        assertEquals(extended1, extended2);

        // do not re-initialize children until the choice is properly set in setExtendedValue
        extended1.setNumElements(1);
        assertNotEquals(extended1, extended2);
        extended2.setNumElements(1);
        assertEquals(extended1, extended2);

        final Choice extendedValue = new Choice(1);
        extendedValue.setValue(42);
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

        // do not re-initialize children until the choice is properly set in setExtendedValue
        extended1.setNumElements(VALUES.length);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended2.setNumElements(VALUES.length);
        assertEquals(extended1.hashCode(), extended2.hashCode());

        final Choice extendedValue = new Choice(VALUES.length);
        extendedValue.setValues(VALUES);
        extended2.setExtendedValue(extendedValue);
        assertNotEquals(extended1.hashCode(), extended2.hashCode());
        extended1.setExtendedValue(extendedValue);
        assertEquals(extended1.hashCode(), extended2.hashCode());
    }

    @Test
    public void bitSizeOf()
    {
        final Extended extendedEmpty = new Extended(0, new Choice(0));
        assertEquals(EXTENDED_BIT_SIZE_EMPTY, extendedEmpty.bitSizeOf());

        final Extended extendedValue = new Extended(1, new Choice(1));
        extendedValue.getExtendedValue().setValue(42);
        assertEquals(EXTENDED_BIT_SIZE_VALUE, extendedValue.bitSizeOf());

        final Extended extendedValues = new Extended(VALUES.length, new Choice(VALUES.length));
        extendedValues.getExtendedValue().setValues(VALUES);
        assertEquals(EXTENDED_BIT_SIZE_VALUES, extendedValues.bitSizeOf());
    }

    @Test
    public void initializeOffsets()
    {
        final Extended extendedEmpty = new Extended(0, new Choice(0));
        assertEquals(EXTENDED_BIT_SIZE_EMPTY, extendedEmpty.initializeOffsets(0));

        final Extended extendedValue = new Extended(1, new Choice(1));
        extendedValue.getExtendedValue().setValue(42);
        assertEquals(EXTENDED_BIT_SIZE_VALUE, extendedValue.initializeOffsets(0));

        final Extended extendedValues = new Extended(VALUES.length, new Choice(VALUES.length));
        extendedValues.getExtendedValue().setValues(VALUES);
        assertEquals(EXTENDED_BIT_SIZE_VALUES, extendedValues.initializeOffsets(0));
    }

    @Test
    public void writeReadExtendedEmpty()
    {
        final Extended extended = new Extended(0, new Choice(0));
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_EMPTY, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertFalse(readExtended.isExtendedValuePresent());
        assertNotEquals(extended, readExtended);
    }

    @Test
    public void writeReadExtendedValue()
    {
        final Extended extended = new Extended(1, new Choice(1));
        extended.getExtendedValue().setValue(42);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_VALUE, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeReadExtendedValues()
    {
        final Extended extended = new Extended(VALUES.length, new Choice(VALUES.length));
        extended.getExtendedValue().setValues(VALUES);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_VALUES, bitBuffer.getBitSize());

        final Extended readExtended = SerializeUtil.deserialize(Extended.class, bitBuffer);
        assertTrue(readExtended.isExtendedValuePresent());
        assertEquals(extended, readExtended);
    }

    @Test
    public void writeOriginalReadExtended()
    {
        final Original original = new Original(VALUES.length);
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
        final Choice extendedValue = new Choice(VALUES.length);
        extendedValue.setValues(VALUES);
        readExtended.setExtendedValue(extendedValue);
        assertTrue(readExtended.isExtendedValuePresent());

        // bit size as extended
        assertEquals(EXTENDED_BIT_SIZE_VALUES, readExtended.bitSizeOf());

        // initialize offsets as extended
        assertEquals(EXTENDED_BIT_SIZE_VALUES, readExtended.initializeOffsets(0));

        // write as extended
        bitBuffer = SerializeUtil.serialize(readExtended);
        assertEquals(EXTENDED_BIT_SIZE_VALUES, bitBuffer.getBitSize());
    }

    @Test
    public void writeExtendedEmptyReadOriginal() throws IOException
    {
        final Extended extended = new Extended(0, new Choice(0));
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_EMPTY, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getNumElements(), readOriginal.getNumElements());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    @Test
    public void writeExtendedValueReadOriginal() throws IOException
    {
        final Extended extended = new Extended(1, new Choice(1));
        extended.getExtendedValue().setValue(42);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_VALUE, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getNumElements(), readOriginal.getNumElements());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    @Test
    public void writeExtendedValuesReadOriginal() throws IOException
    {
        final Extended extended = new Extended(VALUES.length, new Choice(VALUES.length));
        extended.getExtendedValue().setValues(VALUES);
        final BitBuffer bitBuffer = SerializeUtil.serialize(extended);
        assertEquals(EXTENDED_BIT_SIZE_VALUES, bitBuffer.getBitSize());

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final Original readOriginal = new Original(reader);
        assertEquals(extended.getNumElements(), readOriginal.getNumElements());
        assertEquals(ORIGINAL_BIT_SIZE, reader.getBitPosition());
    }

    private static final long VALUES[] = new long[]{ 0, 1, 2, 3, 4 };

    private static final long ORIGINAL_BIT_SIZE = 4 * 8;
    private static final long EXTENDED_BIT_SIZE_EMPTY = ORIGINAL_BIT_SIZE;
    private static final long EXTENDED_BIT_SIZE_VALUE = ORIGINAL_BIT_SIZE + 4 * 8;
    private static final long EXTENDED_BIT_SIZE_VALUES = ORIGINAL_BIT_SIZE + VALUES.length * 4L * 8L;
}
