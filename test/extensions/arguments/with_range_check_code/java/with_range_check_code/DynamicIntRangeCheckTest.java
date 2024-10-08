package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitFieldUtil;
import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import with_range_check_code.dynamic_int_range_check.DynamicIntRangeCheckCompound;

public class DynamicIntRangeCheckTest
{
    @Test
    public void dynamicIntLowerBound() throws IOException, ZserioError
    {
        checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_LOWER_BOUND);
    }

    @Test
    public void dynamicIntUpperBound() throws IOException, ZserioError
    {
        checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_UPPER_BOUND);
    }

    @Test
    public void dynamicIntBelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_LOWER_BOUND - 1));
    }

    @Test
    public void dynamicIntAboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_UPPER_BOUND + 1));
    }

    @Test
    public void numBitsMax() throws IOException, ZserioError
    {
        checkDynamicIntValue((short)64, Long.MAX_VALUE);
    }

    @Test
    public void numBitsAboveMax() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkDynamicIntValue((short)65, Long.MAX_VALUE));
    }

    private void checkDynamicIntValue(short numBits, long value) throws IOException, ZserioError
    {
        DynamicIntRangeCheckCompound dynamicIntRangeCheckCompound = new DynamicIntRangeCheckCompound();
        dynamicIntRangeCheckCompound.setNumBits(numBits);
        dynamicIntRangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        dynamicIntRangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final DynamicIntRangeCheckCompound readDynamicIntRangeCheckCompound =
                new DynamicIntRangeCheckCompound(reader);
        assertEquals(dynamicIntRangeCheckCompound, readDynamicIntRangeCheckCompound);
    }

    private static final short NUM_BITS = 10;
    private static final long DYNAMIC_INT_LOWER_BOUND = BitFieldUtil.getBitFieldLowerBound(NUM_BITS, true);
    private static final long DYNAMIC_INT_UPPER_BOUND = BitFieldUtil.getBitFieldUpperBound(NUM_BITS, true);
}
