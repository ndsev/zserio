package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import with_range_check_code.int7_array_range_check.Int7ArrayRangeCheckCompound;

public class Int7ArrayRangeCheckTest
{
    @Test
    public void int7ArrayLowerBound() throws IOException, ZserioError
    {
        checkInt7ArrayValue(INT7_LOWER_BOUND);
    }

    @Test
    public void int7ArrayUpperBound() throws IOException, ZserioError
    {
        checkInt7ArrayValue(INT7_UPPER_BOUND);
    }

    @Test
    public void int7ArrayBelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(IllegalArgumentException.class, () -> checkInt7ArrayValue((byte)(INT7_LOWER_BOUND - 1)));
    }

    @Test
    public void int7ArrayAboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(IllegalArgumentException.class, () -> checkInt7ArrayValue((byte)(INT7_UPPER_BOUND + 1)));
    }

    private void checkInt7ArrayValue(byte value) throws IOException, ZserioError
    {
        Int7ArrayRangeCheckCompound int7ArrayRangeCheckCompound = new Int7ArrayRangeCheckCompound();
        final int numElements = 1;
        int7ArrayRangeCheckCompound.setNumElements(numElements);
        final byte[] values = new byte[] {value};
        int7ArrayRangeCheckCompound.setArray(values);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        int7ArrayRangeCheckCompound.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final Int7ArrayRangeCheckCompound readInt7ArrayRangeCheckCompound =
                new Int7ArrayRangeCheckCompound(reader);
        assertEquals(int7ArrayRangeCheckCompound, readInt7ArrayRangeCheckCompound);
    }

    private static final byte INT7_LOWER_BOUND = -64;
    private static final byte INT7_UPPER_BOUND = 63;
}
