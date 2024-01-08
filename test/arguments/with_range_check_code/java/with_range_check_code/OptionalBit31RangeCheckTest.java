package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import with_range_check_code.optional_bit31_range_check.OptionalBit31RangeCheckCompound;

public class OptionalBit31RangeCheckTest
{
    @Test
    public void optionalBit31LowerBound() throws IOException, ZserioError
    {
        checkOptionalBit31Value(OPTIONAL_BIT31_LOWER_BOUND);
    }

    @Test
    public void optionalBit31UpperBound() throws IOException, ZserioError
    {
        checkOptionalBit31Value(OPTIONAL_BIT31_UPPER_BOUND);
    }

    @Test
    public void optionalBit31BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkOptionalBit31Value(OPTIONAL_BIT31_LOWER_BOUND - 1));
    }

    @Test
    public void optionalBit31AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkOptionalBit31Value(OPTIONAL_BIT31_UPPER_BOUND + 1));
    }

    @Test
    public void optionalBit31Null()
    {
        OptionalBit31RangeCheckCompound optionalBit31RangeCheckCompound = new OptionalBit31RangeCheckCompound();
        optionalBit31RangeCheckCompound.setValue(null);
    }

    private void checkOptionalBit31Value(int value) throws IOException, ZserioError
    {
        OptionalBit31RangeCheckCompound optionalBit31RangeCheckCompound = new OptionalBit31RangeCheckCompound();
        optionalBit31RangeCheckCompound.setHasOptional(true);
        optionalBit31RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        optionalBit31RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final OptionalBit31RangeCheckCompound readOptionalBit31RangeCheckCompound =
                new OptionalBit31RangeCheckCompound(reader);
        assertEquals(optionalBit31RangeCheckCompound, readOptionalBit31RangeCheckCompound);
    }

    private static final int OPTIONAL_BIT31_LOWER_BOUND = 0;
    private static final int OPTIONAL_BIT31_UPPER_BOUND = 2147483647;
}
