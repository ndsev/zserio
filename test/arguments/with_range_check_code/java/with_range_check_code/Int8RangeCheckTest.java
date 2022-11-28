package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import with_range_check_code.int8_range_check.Int8RangeCheckCompound;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class Int8RangeCheckTest
{
    @Test
    public void int8LowerBound() throws IOException, ZserioError
    {
        checkInt8Value(INT8_LOWER_BOUND);
    }

    @Test
    public void int8UpperBound() throws IOException, ZserioError
    {
        checkInt8Value(INT8_UPPER_BOUND);
    }

    private void checkInt8Value(byte value) throws IOException, ZserioError
    {
        Int8RangeCheckCompound int8RangeCheckCompound = new Int8RangeCheckCompound();
        int8RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        int8RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        final Int8RangeCheckCompound readInt8RangeCheckCompound = new Int8RangeCheckCompound(reader);
        assertEquals(int8RangeCheckCompound, readInt8RangeCheckCompound);
    }

    private static final byte INT8_LOWER_BOUND = -128;
    private static final byte INT8_UPPER_BOUND = 127;
}
