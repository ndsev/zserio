package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import with_range_check_code.varint32_range_check.VarInt32RangeCheckCompound;

public class VarInt32RangeCheckTest
{
    @Test
    public void varInt32LowerBound() throws IOException, ZserioError
    {
        checkVarInt32Value(VARINT32_LOWER_BOUND);
    }

    @Test
    public void varInt32UpperBound() throws IOException, ZserioError
    {
        checkVarInt32Value(VARINT32_UPPER_BOUND);
    }

    @Test
    public void varInt32BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarInt32Value(VARINT32_LOWER_BOUND - 1));
    }

    @Test
    public void varInt32AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarInt32Value(VARINT32_UPPER_BOUND + 1));
    }

    private void checkVarInt32Value(int value) throws IOException, ZserioError
    {
        VarInt32RangeCheckCompound varInt32RangeCheckCompound = new VarInt32RangeCheckCompound();
        varInt32RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varInt32RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final VarInt32RangeCheckCompound readVarInt32RangeCheckCompound =
                new VarInt32RangeCheckCompound(reader);
        assertEquals(varInt32RangeCheckCompound, readVarInt32RangeCheckCompound);
    }

    private static final int VARINT32_LOWER_BOUND = -268435455;
    private static final int VARINT32_UPPER_BOUND = -VARINT32_LOWER_BOUND;
}
