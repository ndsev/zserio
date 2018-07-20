package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.variable_bit_range_check.VariableBitRangeCheckCompound;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.Util;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VariableBitRangeCheckTest
{
    @Test
    public void variableBitLowerBound() throws IOException, ZserioError
    {
        checkVariableBitValue(VARIABLE_BIT_LOWER_BOUND);
    }

    @Test
    public void variableBitUpperBound() throws IOException, ZserioError
    {
        checkVariableBitValue(VARIABLE_BIT_UPPER_BOUND);
    }

    @Test(expected=ZserioError.class)
    public void variableBitBelowLowerBound() throws IOException, ZserioError
    {
        checkVariableBitValue(VARIABLE_BIT_LOWER_BOUND - 1);
    }

    @Test(expected=ZserioError.class)
    public void variableBitAboveUpperBound() throws IOException, ZserioError
    {
        checkVariableBitValue(VARIABLE_BIT_UPPER_BOUND + 1);
    }

    private void checkVariableBitValue(long value) throws IOException, ZserioError
    {
        VariableBitRangeCheckCompound variableBitRangeCheckCompound = new VariableBitRangeCheckCompound();
        variableBitRangeCheckCompound.setNumBits(NUM_BITS);
        variableBitRangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        variableBitRangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final VariableBitRangeCheckCompound readVariableBitRangeCheckCompound =
                new VariableBitRangeCheckCompound(reader);
        assertEquals(variableBitRangeCheckCompound, readVariableBitRangeCheckCompound);
    }

    private static final short NUM_BITS = 10;
    private static final long VARIABLE_BIT_LOWER_BOUND = Util.getBitFieldLowerBound(NUM_BITS, false);
    private static final long VARIABLE_BIT_UPPER_BOUND = Util.getBitFieldUpperBound(NUM_BITS, false);
}
