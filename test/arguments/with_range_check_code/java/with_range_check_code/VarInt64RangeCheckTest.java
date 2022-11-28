package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import with_range_check_code.varint64_range_check.VarInt64RangeCheckCompound;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarInt64RangeCheckTest
{
    @Test
    public void varInt64LowerBound() throws IOException, ZserioError
    {
        checkVarInt64Value(VARINT64_LOWER_BOUND);
    }

    @Test
    public void varInt64UpperBound() throws IOException, ZserioError
    {
        checkVarInt64Value(VARINT64_UPPER_BOUND);
    }

    @Test
    public void varInt64BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarInt64Value(VARINT64_LOWER_BOUND - 1));
    }

    @Test
    public void varInt64AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarInt64Value(VARINT64_UPPER_BOUND + 1));
    }

    private void checkVarInt64Value(long value) throws IOException, ZserioError
    {
        VarInt64RangeCheckCompound varInt64RangeCheckCompound = new VarInt64RangeCheckCompound();
        varInt64RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varInt64RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        final VarInt64RangeCheckCompound readVarInt64RangeCheckCompound =
                new VarInt64RangeCheckCompound(reader);
        assertEquals(varInt64RangeCheckCompound, readVarInt64RangeCheckCompound);
    }

    private static final long VARINT64_LOWER_BOUND = -72057594037927935L;
    private static final long VARINT64_UPPER_BOUND = -VARINT64_LOWER_BOUND;
}
