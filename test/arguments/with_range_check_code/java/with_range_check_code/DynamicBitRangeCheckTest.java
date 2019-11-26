package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.dynamic_bit_range_check.DynamicBitRangeCheckCompound;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;
import org.junit.Ignore;

import zserio.runtime.ZserioError;
import zserio.runtime.Util;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class DynamicBitRangeCheckTest
{
    @Test
    public void dynamicBitLowerBound() throws IOException, ZserioError
    {
        checkDynamicBitValue(DYNAMIC_BIT_LOWER_BOUND);
    }

    @Test
    public void dynamicBitUpperBound() throws IOException, ZserioError
    {
        checkDynamicBitValue(DYNAMIC_BIT_UPPER_BOUND);
    }

    @Ignore("Range check for Big Integers are not implemented yet")
    @Test(expected=ZserioError.class)
    public void dynamicBitBelowLowerBound() throws IOException, ZserioError
    {
        checkDynamicBitValue(DYNAMIC_BIT_LOWER_BOUND - 1);
    }

    @Ignore("Range check for Big Integers are not implemented yet")
    @Test(expected=ZserioError.class)
    public void dynamicBitAboveUpperBound() throws IOException, ZserioError
    {
        checkDynamicBitValue(DYNAMIC_BIT_UPPER_BOUND + 1);
    }

    private void checkDynamicBitValue(long value) throws IOException, ZserioError
    {
        DynamicBitRangeCheckCompound dynamicBitRangeCheckCompound = new DynamicBitRangeCheckCompound();
        dynamicBitRangeCheckCompound.setNumBits(NUM_BITS);
        dynamicBitRangeCheckCompound.setValue(BigInteger.valueOf(value));
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        dynamicBitRangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final DynamicBitRangeCheckCompound readDynamicBitRangeCheckCompound =
                new DynamicBitRangeCheckCompound(reader);
        assertEquals(dynamicBitRangeCheckCompound, readDynamicBitRangeCheckCompound);
    }

    private static final short NUM_BITS = 10;
    private static final long DYNAMIC_BIT_LOWER_BOUND = Util.getBitFieldLowerBound(NUM_BITS, false);
    private static final long DYNAMIC_BIT_UPPER_BOUND = Util.getBitFieldUpperBound(NUM_BITS, false);
}
