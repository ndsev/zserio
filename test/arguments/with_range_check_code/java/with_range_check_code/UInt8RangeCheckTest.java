package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import with_range_check_code.uint8_range_check.UInt8RangeCheckCompound;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UInt8RangeCheckTest
{
    @Test
    public void uint8LowerBound() throws IOException, ZserioError
    {
        checkUInt8Value(UINT8_LOWER_BOUND);
    }

    @Test
    public void uint8UpperBound() throws IOException, ZserioError
    {
        checkUInt8Value(UINT8_UPPER_BOUND);
    }

    @Test
    public void uint8BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkUInt8Value((short)(UINT8_LOWER_BOUND - 1)));
    }

    @Test
    public void uint8AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkUInt8Value((short)(UINT8_UPPER_BOUND + 1)));
    }

    private void checkUInt8Value(short value) throws IOException, ZserioError
    {
        UInt8RangeCheckCompound uint8RangeCheckCompound = new UInt8RangeCheckCompound();
        uint8RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint8RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        final UInt8RangeCheckCompound readUInt8RangeCheckCompound = new UInt8RangeCheckCompound(reader);
        assertEquals(uint8RangeCheckCompound, readUInt8RangeCheckCompound);
    }

    private static final short UINT8_LOWER_BOUND = 0;
    private static final short UINT8_UPPER_BOUND = 255;
}
