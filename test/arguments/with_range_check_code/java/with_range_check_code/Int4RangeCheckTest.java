package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.int4_range_check.Int4RangeCheckCompound;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class Int4RangeCheckTest
{
    @Test
    public void int4LowerBound() throws IOException, ZserioError
    {
        checkInt4Value(INT4_LOWER_BOUND);
    }

    @Test
    public void int4UpperBound() throws IOException, ZserioError
    {
        checkInt4Value(INT4_UPPER_BOUND);
    }

    @Test(expected=ZserioError.class)
    public void int4BelowLowerBound() throws IOException, ZserioError
    {
        checkInt4Value((byte)(INT4_LOWER_BOUND - 1));
    }

    @Test(expected=ZserioError.class)
    public void int4AboveUpperBound() throws IOException, ZserioError
    {
        checkInt4Value((byte)(INT4_UPPER_BOUND + 1));
    }

    private void checkInt4Value(byte value) throws IOException, ZserioError
    {
        Int4RangeCheckCompound int4RangeCheckCompound = new Int4RangeCheckCompound();
        int4RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        int4RangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final Int4RangeCheckCompound readInt4RangeCheckCompound = new Int4RangeCheckCompound(reader);
        assertEquals(int4RangeCheckCompound, readInt4RangeCheckCompound);
    }

    private static final byte INT4_LOWER_BOUND = -8;
    private static final byte INT4_UPPER_BOUND = 7;
}
