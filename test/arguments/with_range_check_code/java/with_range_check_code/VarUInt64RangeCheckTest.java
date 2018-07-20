package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.varuint64_range_check.VarUInt64RangeCheckCompound;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarUInt64RangeCheckTest
{
    @Test
    public void varUInt64LowerBound() throws IOException, ZserioError
    {
        checkVarUInt64Value(VARUINT64_LOWER_BOUND);
    }

    @Test
    public void varUInt64UpperBound() throws IOException, ZserioError
    {
        checkVarUInt64Value(VARUINT64_UPPER_BOUND);
    }

    @Test(expected=ZserioError.class)
    public void varUInt64BelowLowerBound() throws IOException, ZserioError
    {
        checkVarUInt64Value(VARUINT64_LOWER_BOUND - 1);
    }

    @Test(expected=ZserioError.class)
    public void varUInt64AboveUpperBound() throws IOException, ZserioError
    {
        checkVarUInt64Value(VARUINT64_UPPER_BOUND + 1);
    }

    private void checkVarUInt64Value(long value) throws IOException, ZserioError
    {
        VarUInt64RangeCheckCompound varUInt64RangeCheckCompound = new VarUInt64RangeCheckCompound();
        varUInt64RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varUInt64RangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final VarUInt64RangeCheckCompound readVarUInt64RangeCheckCompound =
                new VarUInt64RangeCheckCompound(reader);
        assertEquals(varUInt64RangeCheckCompound, readVarUInt64RangeCheckCompound);
    }

    private static final long VARUINT64_LOWER_BOUND = 0L;
    private static final long VARUINT64_UPPER_BOUND = 144115188075855871L;
}
