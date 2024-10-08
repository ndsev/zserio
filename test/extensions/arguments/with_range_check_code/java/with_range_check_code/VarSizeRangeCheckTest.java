package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import with_range_check_code.varsize_range_check.VarSizeRangeCheckCompound;

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

    @Test
    public void varSizeBelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarSizeValue(VARSIZE_LOWER_BOUND - 1));
    }

    private void checkVarSizeValue(int value) throws IOException, ZserioError
    {
        VarSizeRangeCheckCompound varSizeRangeCheckCompound = new VarSizeRangeCheckCompound();
        varSizeRangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varSizeRangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final VarSizeRangeCheckCompound readVarSizeRangeCheckCompound = new VarSizeRangeCheckCompound(reader);
        assertEquals(varSizeRangeCheckCompound, readVarSizeRangeCheckCompound);
    }

    private static final int VARSIZE_LOWER_BOUND = 0;
    private static final int VARSIZE_UPPER_BOUND = 2147483647;
}
