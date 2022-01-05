package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import with_range_check_code.varuint32_range_check.VarUInt32RangeCheckCompound;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarUInt32RangeCheckTest
{
    @Test
    public void varUInt32LowerBound() throws IOException, ZserioError
    {
        checkVarUInt32Value(VARUINT32_LOWER_BOUND);
    }

    @Test
    public void varUInt32UpperBound() throws IOException, ZserioError
    {
        checkVarUInt32Value(VARUINT32_UPPER_BOUND);
    }

    @Test
    public void varUInt32BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarUInt32Value(VARUINT32_LOWER_BOUND - 1));
    }

    @Test
    public void varUInt32AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarUInt32Value(VARUINT32_UPPER_BOUND + 1));
    }

    private void checkVarUInt32Value(int value) throws IOException, ZserioError
    {
        VarUInt32RangeCheckCompound varUInt32RangeCheckCompound = new VarUInt32RangeCheckCompound();
        varUInt32RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varUInt32RangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final VarUInt32RangeCheckCompound readVarUInt32RangeCheckCompound =
                new VarUInt32RangeCheckCompound(reader);
        assertEquals(varUInt32RangeCheckCompound, readVarUInt32RangeCheckCompound);
    }

    private static final int VARUINT32_LOWER_BOUND = 0;
    private static final int VARUINT32_UPPER_BOUND = 536870911;
}
