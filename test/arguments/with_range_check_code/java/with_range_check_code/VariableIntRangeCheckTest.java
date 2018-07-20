package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.variable_int_range_check.VariableIntRangeCheckCompound;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.Util;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VariableIntRangeCheckTest
{
    @Test
    public void variableIntLowerBound() throws IOException, ZserioError
    {
        checkVariableIntValue(VARIABLE_INT_LOWER_BOUND);
    }

    @Test
    public void variableIntUpperBound() throws IOException, ZserioError
    {
        checkVariableIntValue(VARIABLE_INT_UPPER_BOUND);
    }

    @Test(expected=ZserioError.class)
    public void variableIntBelowLowerBound() throws IOException, ZserioError
    {
        checkVariableIntValue(VARIABLE_INT_LOWER_BOUND - 1);
    }

    @Test(expected=ZserioError.class)
    public void variableIntAboveUpperBound() throws IOException, ZserioError
    {
        checkVariableIntValue(VARIABLE_INT_UPPER_BOUND + 1);
    }

    private void checkVariableIntValue(long value) throws IOException, ZserioError
    {
        VariableIntRangeCheckCompound variableIntRangeCheckCompound = new VariableIntRangeCheckCompound();
        variableIntRangeCheckCompound.setNumBits(NUM_BITS);
        variableIntRangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        variableIntRangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final VariableIntRangeCheckCompound readVariableIntRangeCheckCompound =
                new VariableIntRangeCheckCompound(reader);
        assertEquals(variableIntRangeCheckCompound, readVariableIntRangeCheckCompound);
    }

    private static final short NUM_BITS = 10;
    private static final long VARIABLE_INT_LOWER_BOUND = Util.getBitFieldLowerBound(NUM_BITS, true);
    private static final long VARIABLE_INT_UPPER_BOUND = Util.getBitFieldUpperBound(NUM_BITS, true);
}
