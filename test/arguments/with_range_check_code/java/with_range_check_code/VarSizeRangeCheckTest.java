package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.varsize_range_check.VarSizeRangeCheckCompound;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarSizeRangeCheckTest
{
    @Test
    public void varSizeLowerBound() throws IOException, ZserioError
    {
        checkVarSizeValue(VARSIZE_LOWER_BOUND);
    }

    @Test
    public void varSizeUpperBound() throws IOException, ZserioError
    {
        checkVarSizeValue(VARSIZE_UPPER_BOUND);
    }

    @Test(expected=ZserioError.class)
    public void varSizeBelowLowerBound() throws IOException, ZserioError
    {
        checkVarSizeValue(VARSIZE_LOWER_BOUND - 1);
    }

    private void checkVarSizeValue(int value) throws IOException, ZserioError
    {
        VarSizeRangeCheckCompound varSizeRangeCheckCompound = new VarSizeRangeCheckCompound();
        varSizeRangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varSizeRangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final VarSizeRangeCheckCompound readVarSizeRangeCheckCompound =
                new VarSizeRangeCheckCompound(reader);
        assertEquals(varSizeRangeCheckCompound, readVarSizeRangeCheckCompound);
    }

    private static final int VARSIZE_LOWER_BOUND = 0;
    private static final int VARSIZE_UPPER_BOUND = 2147483647;
}
