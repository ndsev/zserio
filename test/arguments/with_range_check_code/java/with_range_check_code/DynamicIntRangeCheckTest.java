package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.dynamic_int_range_check.DynamicIntRangeCheckCompound;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.Util;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class DynamicIntRangeCheckTest
{
    @Test
    public void dynamicIntLowerBound() throws IOException, ZserioError
    {
        checkDynamicIntValue(DYNAMIC_INT_LOWER_BOUND);
    }

    @Test
    public void dynamicIntUpperBound() throws IOException, ZserioError
    {
        checkDynamicIntValue(DYNAMIC_INT_UPPER_BOUND);
    }

    @Test(expected=ZserioError.class)
    public void dynamicIntBelowLowerBound() throws IOException, ZserioError
    {
        checkDynamicIntValue(DYNAMIC_INT_LOWER_BOUND - 1);
    }

    @Test(expected=ZserioError.class)
    public void dynamicIntAboveUpperBound() throws IOException, ZserioError
    {
        checkDynamicIntValue(DYNAMIC_INT_UPPER_BOUND + 1);
    }

    private void checkDynamicIntValue(long value) throws IOException, ZserioError
    {
        DynamicIntRangeCheckCompound dynamicIntRangeCheckCompound = new DynamicIntRangeCheckCompound();
        dynamicIntRangeCheckCompound.setNumBits(NUM_BITS);
        dynamicIntRangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        dynamicIntRangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final DynamicIntRangeCheckCompound readDynamicIntRangeCheckCompound =
                new DynamicIntRangeCheckCompound(reader);
        assertEquals(dynamicIntRangeCheckCompound, readDynamicIntRangeCheckCompound);
    }

    private static final short NUM_BITS = 10;
    private static final long DYNAMIC_INT_LOWER_BOUND = Util.getBitFieldLowerBound(NUM_BITS, true);
    private static final long DYNAMIC_INT_UPPER_BOUND = Util.getBitFieldUpperBound(NUM_BITS, true);
}
