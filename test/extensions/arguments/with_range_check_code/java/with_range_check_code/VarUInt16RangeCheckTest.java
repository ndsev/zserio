package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import with_range_check_code.varuint16_range_check.VarUInt16RangeCheckCompound;

public class VarUInt16RangeCheckTest
{
    @Test
    public void varUInt16LowerBound() throws IOException, ZserioError
    {
        checkVarUInt16Value(VARUINT16_LOWER_BOUND);
    }

    @Test
    public void varUInt16UpperBound() throws IOException, ZserioError
    {
        checkVarUInt16Value(VARUINT16_UPPER_BOUND);
    }

    @Test
    public void varUInt16BelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarUInt16Value((short)(VARUINT16_LOWER_BOUND - 1)));
    }

    @Test
    public void varUInt16AboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class, () -> checkVarUInt16Value((short)(VARUINT16_UPPER_BOUND + 1)));
    }

    private void checkVarUInt16Value(short value) throws IOException, ZserioError
    {
        VarUInt16RangeCheckCompound varUInt16RangeCheckCompound = new VarUInt16RangeCheckCompound();
        varUInt16RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        varUInt16RangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final VarUInt16RangeCheckCompound readVarUInt16RangeCheckCompound =
                new VarUInt16RangeCheckCompound(reader);
        assertEquals(varUInt16RangeCheckCompound, readVarUInt16RangeCheckCompound);
    }

    private static final short VARUINT16_LOWER_BOUND = 0;
    private static final short VARUINT16_UPPER_BOUND = 32767;
}
