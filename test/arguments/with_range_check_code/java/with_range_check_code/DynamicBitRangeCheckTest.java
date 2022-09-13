package with_range_check_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import with_range_check_code.dynamic_bit_range_check.DynamicBitRangeCheckCompound;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.ZserioError;
import zserio.runtime.BitFieldUtil;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class DynamicBitRangeCheckTest
{
    @Test
    public void dynamicBitLowerBound() throws IOException, ZserioError
    {
        checkDynamicBitValue(NUM_BITS, BigInteger.valueOf(DYNAMIC_BIT_LOWER_BOUND));
    }

    @Test
    public void dynamicBitUpperBound() throws IOException, ZserioError
    {
        checkDynamicBitValue(NUM_BITS, BigInteger.valueOf(DYNAMIC_BIT_UPPER_BOUND));
    }

    @Disabled("Range check for Big Integers are not implemented yet")
    @Test
    public void dynamicBitBelowLowerBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class,
                () -> checkDynamicBitValue(NUM_BITS, BigInteger.valueOf(DYNAMIC_BIT_LOWER_BOUND - 1)));
    }

    @Disabled("Range check for Big Integers are not implemented yet")
    @Test
    public void dynamicBitAboveUpperBound() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class,
                () -> checkDynamicBitValue(NUM_BITS, BigInteger.valueOf(DYNAMIC_BIT_UPPER_BOUND + 1)));
    }

    @Test
    public void numBitsMax() throws IOException, ZserioError
    {
        checkDynamicBitValue((short)64, BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));
    }

    @Disabled("Numbits check not yet implemented for types mapped to BigInteger")
    @Test
    public void numBitsAboveMax() throws IOException, ZserioError
    {
        assertThrows(ZserioError.class,
                () -> checkDynamicBitValue((short)65, BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE)));
    }

    private void checkDynamicBitValue(short numBits, BigInteger value) throws IOException, ZserioError
    {
        DynamicBitRangeCheckCompound dynamicBitRangeCheckCompound = new DynamicBitRangeCheckCompound();
        dynamicBitRangeCheckCompound.setNumBits(numBits);
        dynamicBitRangeCheckCompound.setValue(value);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        dynamicBitRangeCheckCompound.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final DynamicBitRangeCheckCompound readDynamicBitRangeCheckCompound =
                new DynamicBitRangeCheckCompound(reader);
        assertEquals(dynamicBitRangeCheckCompound, readDynamicBitRangeCheckCompound);
    }

    private static final short NUM_BITS = 10;
    private static final long DYNAMIC_BIT_LOWER_BOUND = BitFieldUtil.getBitFieldLowerBound(NUM_BITS, false);
    private static final long DYNAMIC_BIT_UPPER_BOUND = BitFieldUtil.getBitFieldUpperBound(NUM_BITS, false);
}
