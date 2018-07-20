package with_range_check_code;

import static org.junit.Assert.*;

import with_range_check_code.union_int4_range_check.UnionInt4RangeCheckCompound;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UnionInt4RangeCheckTest
{
    @Test
    public void unionInt4LowerBound() throws IOException, ZserioError
    {
        checkUnionInt4Value(INT4_LOWER_BOUND);
    }

    @Test
    public void unionInt4UpperBound() throws IOException, ZserioError
    {
        checkUnionInt4Value(INT4_UPPER_BOUND);
    }

    @Test(expected=ZserioError.class)
    public void unionInt4BelowLowerBound() throws IOException, ZserioError
    {
        checkUnionInt4Value((byte)(INT4_LOWER_BOUND - 1));
    }

    @Test(expected=ZserioError.class)
    public void unionInt4AboveUpperBound() throws IOException, ZserioError
    {
        checkUnionInt4Value((byte)(INT4_UPPER_BOUND + 1));
    }

    private void checkUnionInt4Value(byte value) throws IOException, ZserioError
    {
        UnionInt4RangeCheckCompound unionInt4RangeCheckCompound = new UnionInt4RangeCheckCompound();
        unionInt4RangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        unionInt4RangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final UnionInt4RangeCheckCompound readUnionInt4RangeCheckCompound =
                new UnionInt4RangeCheckCompound(reader);
        assertEquals(unionInt4RangeCheckCompound, readUnionInt4RangeCheckCompound);
    }

    private static final byte INT4_LOWER_BOUND = -8;
    private static final byte INT4_UPPER_BOUND = 7;
}
