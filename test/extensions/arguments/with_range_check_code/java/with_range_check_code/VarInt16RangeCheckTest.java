package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import with_range_check_code.varint16_range_check.VarInt16RangeCheckCompound;

public class VarInt16RangeCheckTest
{
    @Test
    public void varInt16LowerBound() throws IOException, ZserioError
    {
        checkVarInt16Value(VARINT16_LOWER_BOUND);
    }

    @Test
    public void varInt16UpperBound() throws IOException, ZserioError
    {
        checkVarInt16Value(VARINT16_UPPER_BOUND);
    }

    @Test
    public void varInt16BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarInt16Value((short)(VARINT16_LOWER_BOUND - 1)));
    }

    @Test
    public void varInt16AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarInt16Value((short)(VARINT16_UPPER_BOUND + 1)));
    }

    private void checkVarInt16Value(short value) throws IOException, ZserioError
    {
        VarInt16RangeCheckCompound varInt16RangeCheckCompound = new VarInt16RangeCheckCompound();
        varInt16RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varInt16RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final VarInt16RangeCheckCompound readVarInt16RangeCheckCompound =
                new VarInt16RangeCheckCompound(reader);
        assertEquals(varInt16RangeCheckCompound, readVarInt16RangeCheckCompound);
    }

    private static final short VARINT16_LOWER_BOUND = -16383;
    private static final short VARINT16_UPPER_BOUND = -VARINT16_LOWER_BOUND;
}
