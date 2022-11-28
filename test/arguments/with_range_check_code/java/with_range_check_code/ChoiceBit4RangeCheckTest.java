package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import with_range_check_code.choice_bit4_range_check.ChoiceBit4RangeCheckCompound;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class ChoiceBit4RangeCheckTest
{
    @Test
    public void choiceBit4LowerBound() throws IOException, ZserioError
    {
        checkChoiceBit4Value(BIT4_LOWER_BOUND);
    }

    @Test
    public void choiceBit4UpperBound() throws IOException, ZserioError
    {
        checkChoiceBit4Value(BIT4_UPPER_BOUND);
    }

    @Test
    public void choiceBit4BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkChoiceBit4Value((byte)(BIT4_LOWER_BOUND - 1)));
    }

    @Test
    public void choiceBit4AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkChoiceBit4Value((byte)(BIT4_UPPER_BOUND + 1)));
    }

    private void checkChoiceBit4Value(byte value) throws IOException, ZserioError
    {
        final boolean selector = true;
        ChoiceBit4RangeCheckCompound choiceBit4RangeCheckCompound = new ChoiceBit4RangeCheckCompound(selector);
        choiceBit4RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        choiceBit4RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        final ChoiceBit4RangeCheckCompound readChoiceBit4RangeCheckCompound =
                new ChoiceBit4RangeCheckCompound(reader, selector);
        assertEquals(choiceBit4RangeCheckCompound, readChoiceBit4RangeCheckCompound);
    }

    private static final byte BIT4_LOWER_BOUND = 0;
    private static final byte BIT4_UPPER_BOUND = 15;
}
