package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import with_range_check_code.bit4_range_check.Bit4RangeCheckCompound;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class Bit4RangeCheckTest
{
    @Test
    public void bit4LowerBound() throws IOException, ZserioError
    {
        checkBit4Value(BIT4_LOWER_BOUND);
    }

    @Test
    public void bit4UpperBound() throws IOException, ZserioError
    {
        checkBit4Value(BIT4_UPPER_BOUND);
    }

    @Test
    public void bit4BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkBit4Value((byte)(BIT4_LOWER_BOUND - 1)));
    }

    @Test
    public void bit4AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkBit4Value((byte)(BIT4_UPPER_BOUND + 1)));
    }

    private void checkBit4Value(byte value) throws IOException, ZserioError
    {
        Bit4RangeCheckCompound bit4RangeCheckCompound = new Bit4RangeCheckCompound();
        bit4RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        bit4RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        final Bit4RangeCheckCompound readBit4RangeCheckCompound = new Bit4RangeCheckCompound(reader);
        assertEquals(bit4RangeCheckCompound, readBit4RangeCheckCompound);
    }

    private static final byte BIT4_LOWER_BOUND = 0;
    private static final byte BIT4_UPPER_BOUND = 15;
}
